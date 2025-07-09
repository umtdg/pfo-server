package com.umtdg.pfo.portfolio;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.umtdg.pfo.NotFoundException;
import com.umtdg.pfo.fund.Fund;
import com.umtdg.pfo.fund.FundBatchRepository;
import com.umtdg.pfo.fund.FundPrice;
import com.umtdg.pfo.fund.FundPriceRepository;
import com.umtdg.pfo.tefas.TefasClient;
import com.umtdg.pfo.tefas.TefasFetchParams;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/p")
public class PortfolioController {
    private final PortfolioRepository repository;
    private final PortfolioFundRepository portfolioFundRepository;

    private final FundPriceRepository priceRepository;
    private final FundBatchRepository fundBatchRepository;

    private final Logger logger = LoggerFactory
        .getLogger(PortfolioController.class);

    public PortfolioController(
        PortfolioRepository repository,
        PortfolioFundRepository portfolioFundRepository,
        FundPriceRepository priceRepository,
        FundBatchRepository fundBatchRepository
    ) {
        this.repository = repository;
        this.portfolioFundRepository = portfolioFundRepository;
        this.priceRepository = priceRepository;
        this.fundBatchRepository = fundBatchRepository;
    }

    @PostMapping
    public Portfolio create(@RequestBody PortfolioCreate portfolioCreate) {
        Portfolio portfolio = new Portfolio();
        portfolio.setName(portfolioCreate.name);

        return repository.save(portfolio);
    }

    @PutMapping("{id}")
    @Transactional
    public ResponseEntity<Void> update(
        @PathVariable UUID id,
        @RequestBody @Valid PortfolioUpdate update
    ) {
        Portfolio portfolio = repository
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("Portfolio", id.toString())
            );

        List<PortfolioFundAdd> addCodes = update.getAddCodes();
        List<String> removeCodes = update.getRemoveCodes();

        if (!removeCodes.isEmpty()) {
            logger.debug("Removing funds: {}", removeCodes);
            portfolioFundRepository.deleteAllByPortfolioId(id, removeCodes);
        }

        if (!addCodes.isEmpty()) {
            logger.debug("Adding funds: {}", addCodes);

            List<PortfolioFund> addList = addCodes
                .stream()
                .map(add -> add.toPortfolioFund(id))
                .collect(Collectors.toList());

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

            logger.trace("Save {} portfolio funds to DB", funds.size());
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
    public Portfolio getOne(@PathVariable UUID id) {
        // TODO: Find one portfolio with owner id matching current user

        return repository
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("Portfolio", id.toString())
            );
    }

    @GetMapping("{id}/prices")
    @Transactional
    public ResponseEntity<Set<FundToBuy>> getPrices(
        @PathVariable UUID id, @RequestParam float budget,
        @RequestParam(required = false) LocalDate from
    ) {
        Portfolio portfolio = repository
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("Portfolio", id.toString())
            );

        if (from == null) {
            LocalDateTime now = LocalDateTime.now();
            if (now.getHour() < 18) {
                now = now.minusDays(1);
            }

            DayOfWeek dayOfWeek = now.getDayOfWeek();
            if (dayOfWeek == DayOfWeek.SATURDAY) {
                now = now.minusDays(1);
            } else if (dayOfWeek == DayOfWeek.SUNDAY) {
                now = now.minusDays(2);
            }

            from = now.toLocalDate();

            logger
                .debug(
                    "Using previous weekday {} as fund price date",
                    from
                );
        }

        // Find latest date of fund prices
        logger.trace("Find last fund price update date from DB");
        LocalDate lastUpdated = priceRepository.findLatestDate();
        if (lastUpdated == null) {
            lastUpdated = from.minusDays(1);

            logger
                .warn(
                    "No fund price data is found, using {} as last update date",
                    lastUpdated
                );
        }

        // If the last fund update date is before requested date,
        // fetch fund information from Tefas and update funds and prices
        if (lastUpdated.isBefore(from)) {
            logger.debug("Updating out of date fund price information");

            TefasFetchParams fetchParams = new TefasFetchParams(
                lastUpdated.plusDays(1), from
            );
            TefasClient tefasClient = null;

            try {
                tefasClient = new TefasClient();
            } catch (Exception exc) {
                logger
                    .error(
                        "Error while fetching fund prices from Tefas ({} - {}): {}",
                        fetchParams.getStart(),
                        fetchParams.getEnd(),
                        exc.getMessage()
                    );
                return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
            }

            final int batchSize = 2000;
            List<Fund> fundBatch = new ArrayList<>(batchSize);
            List<FundPrice> priceBatch = new ArrayList<>(batchSize);

            tefasClient.fetchStreaming(fetchParams, fund -> {
                fundBatch.add(fund.toFund());
                priceBatch.add(fund.toFundPrice());

                if (fundBatch.size() > batchSize) {
                    logger
                        .trace(
                            "Save Fund batch of {}",
                            fundBatch.size()
                        );
                    fundBatchRepository.batchInsertFunds(fundBatch);

                    logger
                        .trace(
                            "Save FundPrice batch of {}",
                            priceBatch.size()
                        );
                    fundBatchRepository
                        .batchInsertFundPrices(priceBatch);

                    fundBatch.clear();
                    priceBatch.clear();
                }
            });

            if (!fundBatch.isEmpty()) {
                logger
                    .trace(
                        "Save remaining Fund batch of {}",
                        fundBatch.size()
                    );
                fundBatchRepository.batchInsertFunds(fundBatch);
            }

            if (!priceBatch.isEmpty()) {
                logger
                    .trace(
                        "Save remaining FundPrice batch of {}",
                        priceBatch.size()
                    );
                fundBatchRepository.batchInsertFundPrices(priceBatch);
            }
        }

        // Price set of funds that are in portfolio
        // Set<PortfolioFund> funds = portfolioFundRepository
        // .findAllByPortfolioId(portfolio.getId());
        List<PortfolioFundPrice> prices = priceRepository
            .findAllByDate(from, portfolio.getId());

        Set<FundToBuy> buyPrices = new HashSet<>();

        // A very naive solution to Knapsack Problem
        for (PortfolioFundPrice pfp : prices) {
            float allocated = budget * pfp.normWeight;
            float unitPrice = pfp.price;

            int minAmount = pfp.minAmount;
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
                        pfp.code, pfp.title,
                        price, amount, weight
                    )
                );
        }

        return new ResponseEntity<Set<FundToBuy>>(buyPrices, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable UUID id) {
        repository.deleteById(id);
    }
}
