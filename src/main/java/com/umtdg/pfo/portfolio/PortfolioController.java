package com.umtdg.pfo.portfolio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.umtdg.pfo.SortParameters;
import com.umtdg.pfo.exception.NotFoundException;
import com.umtdg.pfo.exception.SortByValidationException;
import com.umtdg.pfo.exception.UpdateFundStatsException;
import com.umtdg.pfo.fund.FundController;
import com.umtdg.pfo.fund.FundFilter;
import com.umtdg.pfo.fund.FundService;
import com.umtdg.pfo.fund.stats.FundStats;
import com.umtdg.pfo.fund.info.FundInfo;
import com.umtdg.pfo.portfolio.dto.FundToBuy;
import com.umtdg.pfo.portfolio.dto.PortfolioCreate;
import com.umtdg.pfo.portfolio.dto.PortfolioUpdate;
import com.umtdg.pfo.portfolio.fund.PortfolioFund;
import com.umtdg.pfo.portfolio.fund.PortfolioFundRepository;
import com.umtdg.pfo.portfolio.fund.dto.PortfolioFundAdd;
import com.umtdg.pfo.portfolio.price.PortfolioFundPrice;
import com.umtdg.pfo.portfolio.price.PortfolioFundPriceRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/p")
public class PortfolioController {
    private static final String NOT_FOUND_CONTEXT = "Portfolio";

    private final FundService fundService;

    private final PortfolioRepository repository;
    private final PortfolioFundRepository portfolioFundRepository;
    private final PortfolioFundPriceRepository portfolioPriceRepository;

    private final Logger logger = LoggerFactory
        .getLogger(PortfolioController.class);

    public PortfolioController(
        FundService fundService,
        PortfolioRepository repository,
        PortfolioFundRepository portfolioFundRepository,
        PortfolioFundPriceRepository portfolioPriceRepository
    ) {
        this.fundService = fundService;

        this.repository = repository;
        this.portfolioFundRepository = portfolioFundRepository;
        this.portfolioPriceRepository = portfolioPriceRepository;
    }

    @PostMapping
    public Portfolio create(@RequestBody PortfolioCreate portfolioCreate) {
        Portfolio portfolio = new Portfolio();
        portfolio.setName(portfolioCreate.name());

        return repository.save(portfolio);
    }

    @PutMapping("{id}")
    @Transactional
    public ResponseEntity<Void> update(
        @PathVariable UUID id,
        @RequestBody @Valid PortfolioUpdate update
    )
        throws NotFoundException {
        Portfolio portfolio = repository
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException(NOT_FOUND_CONTEXT, id.toString())
            );

        List<PortfolioFundAdd> addCodes = update.getAddCodes();
        List<String> removeCodes = update.getRemoveCodes();

        if (!removeCodes.isEmpty()) {
            portfolioFundRepository
                .deleteAllByPortfolioIdAndFundCodeIn(id, removeCodes);
        }

        if (!addCodes.isEmpty()) {
            List<PortfolioFund> addList = addCodes
                .stream()
                .map(add -> add.toPortfolioFund(id))
                .toList();

            List<PortfolioFund> funds = portfolioFundRepository
                .findAllByPortfolioId(portfolio.getId());

            funds.addAll(addList);

            float totalWeights = 0.0f;
            for (PortfolioFund fund : funds) {
                totalWeights += fund.getWeight();
            }

            for (PortfolioFund fund : funds) {
                fund.setNormWeight(fund.getWeight() / totalWeights);
            }

            portfolioFundRepository.saveAll(funds);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public List<Portfolio> getAll() {
        // TODO: Return User's portfolio set

        return repository.findAll();
    }

    @GetMapping("{id}")
    public Portfolio getOne(@PathVariable UUID id) throws NotFoundException {
        // TODO: Find one portfolio with owner id matching current user

        return repository
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException(NOT_FOUND_CONTEXT, id.toString())
            );
    }

    @GetMapping("{id}/prices")
    @Transactional
    public ResponseEntity<List<FundToBuy>> getPrices(
        @PathVariable UUID id, FundFilter filter, float budget
    )
        throws NotFoundException {
        Portfolio portfolio = repository
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException(NOT_FOUND_CONTEXT, id.toString())
            );
        UUID portfolioId = portfolio.getId();

        filter = fundService.validateFundFilter(filter);
        List<String> codes = filter.getCodes();
        LocalDate date = filter.getDate();
        LocalDate fetchFrom = filter.getFetchFrom();

        logger
            .debug(
                "[PORTFOLIO:{}][CODES:{}][DATE:{}][FROM:{}] Get portfolio prices",
                portfolioId,
                codes,
                date,
                fetchFrom
            );

        // If the last fund update date is before requested date,
        // fetch fund information from Tefas and update funds and prices
        fundService.updateTefasFunds(filter);

        // Price set of funds that are in portfolio
        List<PortfolioFundPrice> prices = (codes == null || codes.isEmpty())
            ? portfolioPriceRepository
                .findAllByPortfolioIdAndDate(portfolioId, date)
            : portfolioPriceRepository
                .findAllByPortfolioIdAndDateAndCodeIn(portfolioId, date, codes);

        List<FundToBuy> buyPrices = new ArrayList<>();

        // A very naive solution to Knapsack Problem
        for (PortfolioFundPrice portfolioFundPrice : prices) {
            float allocated = budget * portfolioFundPrice.getNormalizedWeight();
            float unitPrice = portfolioFundPrice.getPrice();

            int minAmount = portfolioFundPrice.getMinAmount();
            int amount = Math
                .max(
                    minAmount,
                    (int) Math.floor(allocated / unitPrice)
                );

            amount = Math.max(minAmount, amount);
            float price = unitPrice * amount;
            float weight = price / budget;

            buyPrices
                .add(
                    new FundToBuy(
                        portfolioFundPrice.getCode(), portfolioFundPrice.getTitle(),
                        price, amount, weight
                    )
                );
        }

        return new ResponseEntity<>(buyPrices, HttpStatus.OK);
    }

    @GetMapping("{id}/info")
    @Transactional
    public ResponseEntity<List<FundInfo>> getInfos(
        @PathVariable UUID id, SortParameters sortParameters
    )
        throws NotFoundException,
            SortByValidationException {
        repository
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException(NOT_FOUND_CONTEXT, id.toString())
            );

        Sort sort = sortParameters
            .validate(FundController.ALLOWED_FUND_INFO_SORT_PROPERTIES);

        FundFilter filter = fundService.validateFundFilter(null);
        filter.setCodes(portfolioFundRepository.findAllFundCodesByPortfolioId(id));

        fundService.updateTefasFunds(filter);

        return fundService.getFundInfos(filter, sort);
    }

    @GetMapping("{id}/stats")
    @Transactional
    public List<FundStats> getStats(
        @PathVariable UUID id,
        @RequestParam(required = false, defaultValue = "false") boolean force,
        SortParameters sortParameters
    )
        throws NotFoundException,
            SortByValidationException,
            UpdateFundStatsException {
        repository
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException(NOT_FOUND_CONTEXT, id.toString())
            );

        return fundService
            .updateAndGetFundStats(
                portfolioFundRepository.findAllFundCodesByPortfolioId(id),
                sortParameters,
                force
            );
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable UUID id) {
        repository.deleteById(id);
    }
}
