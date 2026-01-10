package com.umtdg.pfo.portfolio.fund;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.umtdg.pfo.SortParameters;
import com.umtdg.pfo.exception.SortByValidationException;
import com.umtdg.pfo.fund.FundService;
import com.umtdg.pfo.fund.price.FundPriceStats;
import com.umtdg.pfo.fund.stats.FundStats;
import com.umtdg.pfo.portfolio.Portfolio;
import com.umtdg.pfo.portfolio.fund.dto.PortfolioFundUpdate;
import com.umtdg.pfo.portfolio.fund.dto.PortfolioFundPred;
import com.umtdg.pfo.portfolio.price.PortfolioFundPrice;
import com.umtdg.pfo.portfolio.price.PortfolioFundPriceRepository;

@Service
public class PortfolioFundService {
    private final Logger logger = LoggerFactory.getLogger(PortfolioFundService.class);

    private final FundService fundService;

    private final PortfolioFundRepository repository;
    private final PortfolioFundPriceRepository fundPriceRepository;

    public PortfolioFundService(
        FundService fundService,
        PortfolioFundRepository repository,
        PortfolioFundPriceRepository fundPriceRepository
    ) {
        this.fundService = fundService;

        this.repository = repository;
        this.fundPriceRepository = fundPriceRepository;
    }

    public void updateFunds(Portfolio portfolio, Set<PortfolioFundUpdate> updateInfos) {
        if (updateInfos == null || updateInfos.isEmpty()) {
            return;
        }

        UUID portfolioId = portfolio.getId();

        Map<String, PortfolioFund> funds = repository
            .findAll()
            .stream()
            .collect(Collectors.toMap(PortfolioFund::getCode, Function.identity()));

        float totalWeights = 0.0f;
        for (PortfolioFundUpdate update : updateInfos) {
            String code = update.getFundCode();
            PortfolioFund fund = funds.get(code);

            if (fund == null) {
                fund = update.toPortfolioFund(portfolioId);
                totalWeights += fund.getWeight();

                funds.put(code, fund);

                continue;
            }

            updateFund(fund, update);
            totalWeights += fund.getWeight();
        }

        // Normalize weights
        for (PortfolioFund fund : funds.values()) {
            fund.setNormWeight(fund.getWeight() / totalWeights);
        }

        repository.saveAll(funds.values());
    }

    public void removeFunds(Portfolio portfolio, List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return;
        }

        repository
            .deleteAllByPortfolioIdAndCodeIn(portfolio.getId(), codes);
    }

    public List<PortfolioFundPrice> getPrices(
        Portfolio portfolio, LocalDate date, SortParameters sortParameters
    )
        throws SortByValidationException {
        Sort sort = null;
        if (sortParameters != null) {
            sort = sortParameters
                .validate(PortfolioFundPrice.ALLOWED_SORT_PROPERTIES, "code");
        }

        UUID id = portfolio.getId();

        logger
            .debug(
                "[PORTFOLIO:{}][DATE:{}] Get all portfolio fund prices",
                id,
                date
            );

        if (date == null) {
            return fundPriceRepository.findAllLatestByPortfolioId(id, sort);
        } else {
            return fundPriceRepository.findAllByPortfolioIdAndDate(id, date);
        }
    }

    public List<FundStats> getFundStats(
        Portfolio portfolio, SortParameters sortParameters
    )
        throws SortByValidationException {
        Set<String> codes = repository
            .findAllFundCodesByPortfolioId(portfolio.getId())
            .stream()
            .collect(Collectors.toSet());
        return fundService.getStats(codes, sortParameters);
    }

    public List<FundPriceStats> getPricesWithStats(
        Portfolio portfolio, SortParameters sortParameters
    )
        throws SortByValidationException {
        Set<String> codes = repository
            .findAllFundCodesByPortfolioId(portfolio.getId())
            .stream()
            .collect(Collectors.toSet());
        return fundService.getPricesWithStats(codes, sortParameters);
    }

    public List<PortfolioFundPred> getPredictions(
        Portfolio portfolio, double budget
    ) {
        UUID id = portfolio.getId();
        logger.debug("[PORTFOLIO:{}] Get portfolio predictions", id);

        return fundPriceRepository
            .findAllLatestByPortfolioId(id, null)
            .stream()
            .map(fundPrice -> calculateFundPrediction(fundPrice, budget))
            .toList();
    }

    private PortfolioFundPred calculateFundPrediction(
        PortfolioFundPrice fundPrice, double budget
    ) {
        double unitPrice = fundPrice.getPrice();
        double allocated = budget * fundPrice.getNormalizedWeight();

        int amount = Math
            .max(fundPrice.getMinAmount(), (int) Math.floor(allocated / unitPrice));

        double price = unitPrice * amount;
        double weight = price / budget;

        return new PortfolioFundPred(
            fundPrice.getCode(), fundPrice.getTitle(), price, amount, weight
        );
    }

    private void updateFund(PortfolioFund fund, PortfolioFundUpdate update) {
        Float weight = update.getWeight();
        if (weight != null) {
            fund.setWeight(weight);
        }

        Integer minAmount = update.getMinAmount();
        if (minAmount != null) {
            fund.setMinAmount(minAmount);
        }

        Integer ownedAmount = update.getOwnedAmount();
        if (ownedAmount != null) {
            fund.setOwnedAmount(ownedAmount);
        }

        Double moneySpent = update.getMoneySpent();
        if (moneySpent != null) {
            fund.setMoneySpent(moneySpent);
        }
    }
}
