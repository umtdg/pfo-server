package com.umtdg.pfo.portfolio.fund;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.umtdg.pfo.SortParameters;
import com.umtdg.pfo.exception.NotFoundException;
import com.umtdg.pfo.exception.SortByValidationException;
import com.umtdg.pfo.exception.UpdateFundStatsException;
import com.umtdg.pfo.fund.FundFilter;
import com.umtdg.pfo.fund.FundService;
import com.umtdg.pfo.fund.info.FundInfo;
import com.umtdg.pfo.fund.stats.FundStats;
import com.umtdg.pfo.portfolio.Portfolio;
import com.umtdg.pfo.portfolio.fund.dto.PortfolioFundUpdate;
import com.umtdg.pfo.portfolio.fund.dto.PortfolioFundBuyPred;
import com.umtdg.pfo.portfolio.price.PortfolioFundPrice;
import com.umtdg.pfo.portfolio.price.PortfolioFundPriceRepository;

@Service
public class PortfolioFundService {
    private static final String NOT_FOUND_CONTEXT = "PortfolioFund";

    private final Logger logger = LoggerFactory.getLogger(PortfolioFundService.class);

    private final FundService fundService;

    private final PortfolioFundRepository portfolioFundRepository;
    private final PortfolioFundPriceRepository portfolioFundPriceRepository;

    public PortfolioFundService(
        FundService fundService,
        PortfolioFundRepository portfolioFundRepository,
        PortfolioFundPriceRepository portfolioFundPriceRepository
    ) {
        this.fundService = fundService;

        this.portfolioFundRepository = portfolioFundRepository;
        this.portfolioFundPriceRepository = portfolioFundPriceRepository;
    }

    public void updateFunds(Portfolio portfolio, Set<PortfolioFundUpdate> updateInfos) {
        if (updateInfos == null || updateInfos.isEmpty()) {
            return;
        }

        UUID portfolioId = portfolio.getId();

        Map<String, PortfolioFund> funds = portfolioFundRepository
            .findAll()
            .stream()
            .collect(Collectors.toMap(PortfolioFund::getFundCode, Function.identity()));

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

        portfolioFundRepository.saveAll(funds.values());
    }

    public void removeFunds(Portfolio portfolio, List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return;
        }

        portfolioFundRepository
            .deleteAllByPortfolioIdAndFundCodeIn(portfolio.getId(), codes);
    }

    public PortfolioFund getFund(Portfolio portfolio, String fundCode)
        throws NotFoundException {
        PortfolioFundId id = new PortfolioFundId(fundCode, portfolio.getId());
        Optional<PortfolioFund> fund = portfolioFundRepository.findById(id);
        if (fund.isEmpty()) {
            throw new NotFoundException(NOT_FOUND_CONTEXT, id.toString());
        }

        return fund.get();
    }

    public List<PortfolioFund> getFunds(
        Portfolio portfolio, SortParameters sortParameters
    )
        throws SortByValidationException {
        List<String> sortBy = sortParameters.getSortBy();
        if (sortBy.isEmpty()) {
            sortBy.add("fundCode");
        }

        Sort sort = sortParameters
            .validate(PortfolioFundController.ALLOWED_PORTFOLIO_FUND_SORT_PROPERTIES);

        return portfolioFundRepository.findAllByPortfolioId(portfolio.getId(), sort);
    }

    public List<PortfolioFundPrice> getPrices(
        Portfolio portfolio, FundFilter filter, SortParameters sortParameters
    )
        throws SortByValidationException {
        List<String> sortBy = sortParameters.getSortBy();
        if (sortBy.isEmpty()) {
            sortBy.add("code");
        }

        Sort sort = sortParameters
            .validate(
                PortfolioFundController.ALLOWED_PORTFOLIO_FUND_PRICE_SORT_PROPERTIES
            );

        UUID portfolioId = portfolio.getId();

        filter = fundService.validateFundFilter(filter);
        LocalDate date = filter.getDate();
        List<String> codes = filter.getCodes();
        if (codes.isEmpty()) {
            codes
                .addAll(
                    portfolioFundRepository.findAllFundCodesByPortfolioId(portfolioId)
                );
        }

        logger
            .debug(
                "[PORTFOLIO:{}][CODES:{}][DATE:{}] Get all portfolio fund prices",
                portfolioId,
                codes,
                date
            );

        return portfolioFundPriceRepository
            .findAllByPortfolioIdAndDateAndCodeIn(portfolioId, date, codes, sort);
    }

    public List<FundInfo> getFundInfos(
        Portfolio portfolio, SortParameters sortParameters
    )
        throws SortByValidationException {
        FundFilter filter = fundService.validateFundFilter(null);
        filter
            .setCodes(
                portfolioFundRepository.findAllFundCodesByPortfolioId(portfolio.getId())
            );

        return fundService.updateAndGetFundInfos(filter, sortParameters);
    }

    public List<FundStats> getFundStats(
        Portfolio portfolio, SortParameters sortParameters, boolean force
    )
        throws SortByValidationException,
            UpdateFundStatsException {
        return fundService
            .updateAndGetFundStats(
                portfolioFundRepository
                    .findAllFundCodesByPortfolioId(portfolio.getId()),
                sortParameters,
                force
            );
    }

    public List<PortfolioFundBuyPred> getPredictions(
        Portfolio portfolio, FundFilter filter, float budget
    ) {
        filter = fundService.validateFundFilter(filter);

        List<String> codes = filter.getCodes();
        LocalDate date = filter.getDate();

        logger
            .debug(
                "[PORTFOLIO:{}][CODES:{}][DATE:{}][FROM:{}] Get portfolio prices",
                portfolio.getId(),
                codes,
                date,
                filter.getFetchFrom()
            );

        fundService.updateTefasFunds(filter);

        return getFundPrices(portfolio, date, codes)
            .stream()
            .map(fundPrice -> calculateFundPrediction(fundPrice, budget))
            .toList();
    }

    private List<PortfolioFundPrice> getFundPrices(
        Portfolio portfolio, LocalDate date, List<String> codes
    ) {
        if (codes == null || codes.isEmpty()) {
            return portfolioFundPriceRepository
                .findAllByPortfolioIdAndDate(portfolio.getId(), date);
        } else {
            return portfolioFundPriceRepository
                .findAllByPortfolioIdAndDateAndCodeIn(
                    portfolio.getId(),
                    date,
                    codes,
                    null
                );
        }
    }

    private PortfolioFundBuyPred calculateFundPrediction(
        PortfolioFundPrice fundPrice, float budget
    ) {
        float unitPrice = fundPrice.getPrice();
        float allocated = budget * fundPrice.getNormalizedWeight();

        int amount = Math
            .max(fundPrice.getMinAmount(), (int) Math.floor(allocated / unitPrice));

        float price = unitPrice * amount;
        float weight = price / budget;

        return new PortfolioFundBuyPred(
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

        Double totalMoneySpent = update.getTotalMoneySpent();
        if (totalMoneySpent != null) {
            fund.setTotalMoneySpent(totalMoneySpent);
        }
    }
}
