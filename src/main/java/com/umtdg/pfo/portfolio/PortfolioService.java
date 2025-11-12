package com.umtdg.pfo.portfolio;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.umtdg.pfo.fund.FundFilter;
import com.umtdg.pfo.fund.FundService;
import com.umtdg.pfo.fund.info.FundInfo;
import com.umtdg.pfo.fund.stats.FundStats;
import com.umtdg.pfo.portfolio.dto.FundToBuy;
import com.umtdg.pfo.portfolio.dto.PortfolioCreate;
import com.umtdg.pfo.SortParameters;
import com.umtdg.pfo.exception.NotFoundException;
import com.umtdg.pfo.exception.SortByValidationException;
import com.umtdg.pfo.exception.UpdateFundStatsException;
import com.umtdg.pfo.portfolio.fund.PortfolioFund;
import com.umtdg.pfo.portfolio.fund.PortfolioFundRepository;
import com.umtdg.pfo.portfolio.fund.dto.PortfolioFundAdd;
import com.umtdg.pfo.portfolio.price.PortfolioFundPrice;
import com.umtdg.pfo.portfolio.price.PortfolioFundPriceRepository;

@Service
public class PortfolioService {
    private static final String NOT_FOUND_CONTEXT = "Portfolio";

    private final Logger logger = LoggerFactory.getLogger(PortfolioService.class);

    private final FundService fundService;

    private final PortfolioRepository repository;
    private final PortfolioFundRepository portfolioFundRepository;
    private final PortfolioFundPriceRepository portfolioFundPriceRepository;

    public PortfolioService(
        FundService fundService, PortfolioRepository repository,
        PortfolioFundRepository portfolioFundRepository,
        PortfolioFundPriceRepository portfolioFundPriceRepository
    ) {
        this.fundService = fundService;

        this.repository = repository;
        this.portfolioFundRepository = portfolioFundRepository;
        this.portfolioFundPriceRepository = portfolioFundPriceRepository;
    }

    public Portfolio createPortfolio(PortfolioCreate createInfo) {
        Portfolio portfolio = new Portfolio(createInfo.name());

        return repository.save(portfolio);
    }

    public List<Portfolio> getAllPortfolios() {
        return repository.findAll();
    }

    public Portfolio getPortfolio(UUID id) throws NotFoundException {
        return repository
            .findById(id)
            .orElseThrow(() -> new NotFoundException(NOT_FOUND_CONTEXT, id.toString()));
    }

    public void deletePortfolio(UUID id) {
        repository.deleteById(id);
    }

    public void addFunds(Portfolio portfolio, List<PortfolioFundAdd> addInfos) {
        if (addInfos.isEmpty()) {
            return;
        }

        UUID portfolioId = portfolio.getId();

        List<PortfolioFund> addList = addInfos
            .stream()
            .map(addInfo -> addInfo.toPortfolioFund(portfolioId))
            .toList();

        List<PortfolioFund> funds = portfolioFundRepository
            .findAllByPortfolioId(portfolioId);
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

    public void removeFunds(Portfolio portfolio, List<String> codes) {
        if (codes.isEmpty()) {
            return;
        }

        portfolioFundRepository
            .deleteAllByPortfolioIdAndFundCodeIn(portfolio.getId(), codes);
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

    public List<FundToBuy> getFundBuyPrices(
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
            .map(fundPrice -> calculateBuyPrice(fundPrice, budget))
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
                .findAllByPortfolioIdAndDateAndCodeIn(portfolio.getId(), date, codes);
        }
    }

    private FundToBuy calculateBuyPrice(PortfolioFundPrice fundPrice, float budget) {
        float unitPrice = fundPrice.getPrice();
        float allocated = budget * fundPrice.getNormalizedWeight();

        int amount = Math
            .max(fundPrice.getMinAmount(), (int) Math.floor(allocated / unitPrice));

        float price = unitPrice * amount;
        float weight = price / budget;

        return new FundToBuy(
            fundPrice.getCode(), fundPrice.getTitle(), price, amount, weight
        );
    }
}
