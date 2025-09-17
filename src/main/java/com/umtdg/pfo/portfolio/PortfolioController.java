package com.umtdg.pfo.portfolio;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.RestController;

import com.umtdg.pfo.DateUtils;
import com.umtdg.pfo.NotFoundException;
import com.umtdg.pfo.fund.FundBatchRepository;
import com.umtdg.pfo.fund.FundFilter;
import com.umtdg.pfo.fund.FundPriceRepository;
import com.umtdg.pfo.tefas.TefasClient;

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
        @PathVariable UUID id, @Valid FundFilter filter, float budget
    ) {
        Portfolio portfolio = repository
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("Portfolio", id.toString())
            );

        filter = DateUtils.checkFundDateFilters(filter, priceRepository);
        LocalDate date = filter.getDate();
        LocalDate fetchFrom = filter.getFetchFrom();

        UUID portfolioId = portfolio.getId();
        logger.info("Getting portfolio prices of {} for {}", portfolioId, filter);

        // If the last fund update date is before requested date,
        // fetch fund information from Tefas and update funds and prices
        if (fetchFrom.isBefore(date)) {
            try {
                TefasClient tefasClient = new TefasClient();

                fetchFrom = fetchFrom.plusDays(1);
                tefasClient.fetchDateRange(fundBatchRepository, fetchFrom, date);
            } catch (KeyManagementException keyMgmtExc) {
                logger
                    .error(
                        "Error while creating Tefas client: KeyManagementException: {}",
                        keyMgmtExc.getMessage()
                    );

                return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
            } catch (KeyStoreException keyStoreExc) {
                logger
                    .error(
                        "Error while creating Tefas client: KeyStoreException: {}",
                        keyStoreExc.getMessage()
                    );

                return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
            } catch (NoSuchAlgorithmException noAlgoExc) {
                logger
                    .error(
                        "Error while creating Tefas client: NoSuchAlgorithmException: {}",
                        noAlgoExc.getMessage()
                    );

                return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
            } catch (IllegalArgumentException illegalArgExc) {
                logger
                    .error("Error while fetching Fund information: {}", illegalArgExc);

                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        // Price set of funds that are in portfolio
        // Set<PortfolioFund> funds = portfolioFundRepository
        // .findAllByPortfolioId(portfolio.getId());
        List<PortfolioFundPrice> prices = null;

        List<String> codes = filter.getCodes();
        if (codes == null || codes.isEmpty()) {
            prices = priceRepository.findAllByDate(portfolioId, date);
        } else {
            prices = priceRepository.findAllByDateAndCode(portfolioId, date, codes);
        }

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
