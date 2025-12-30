package com.umtdg.pfo.portfolio.fund;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.umtdg.pfo.SortParameters;
import com.umtdg.pfo.exception.NotFoundException;
import com.umtdg.pfo.exception.SortByValidationException;
import com.umtdg.pfo.exception.UpdateFundStatsException;
import com.umtdg.pfo.fund.FundFilter;
import com.umtdg.pfo.fund.info.FundInfo;
import com.umtdg.pfo.fund.stats.FundStats;
import com.umtdg.pfo.portfolio.Portfolio;
import com.umtdg.pfo.portfolio.PortfolioService;
import com.umtdg.pfo.portfolio.fund.dto.PortfolioFundBuyPred;
import com.umtdg.pfo.portfolio.price.PortfolioFundPrice;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/p/{id}/f")
@Transactional
public class PortfolioFundController {
    private final PortfolioFundService service;
    private final PortfolioService portfolioService;

    public static final Set<String> ALLOWED_PORTFOLIO_FUND_SORT_PROPERTIES = Set
        .of(
            "fundCode",
            "weight",
            "normWeight",
            "minAmount",
            "ownedAmount",
            "totalMoneySpent"
        );

    public static final Set<String> ALLOWED_PORTFOLIO_FUND_PRICE_SORT_PROPERTIES = Set
        .of("code", "date", "title", "normalizedWeight", "minAmount", "price");

    public PortfolioFundController(
        PortfolioFundService service, PortfolioService portfolioService
    ) {
        this.service = service;
        this.portfolioService = portfolioService;
    }

    @GetMapping()
    public List<PortfolioFund> getFunds(
        @PathVariable UUID id, @Valid SortParameters sortParameters
    )
        throws NotFoundException,
            SortByValidationException {
        Portfolio portfolio = portfolioService.getPortfolio(id);

        return service.getFunds(portfolio, sortParameters);
    }

    @GetMapping("prices")
    public List<PortfolioFundPrice> getPrices(
        @PathVariable UUID id, @Valid FundFilter filter,
        @Valid SortParameters sortParameters
    )
        throws NotFoundException,
            SortByValidationException {
        Portfolio portfolio = portfolioService.getPortfolio(id);

        return service.getPrices(portfolio, filter, sortParameters);
    }

    @GetMapping("infos")
    public List<FundInfo> getInfos(
        @PathVariable UUID id, SortParameters sortParameters
    )
        throws NotFoundException,
            SortByValidationException {
        Portfolio portfolio = portfolioService.getPortfolio(id);

        return service.getFundInfos(portfolio, sortParameters);
    }

    @GetMapping("stats")
    public List<FundStats> getStats(
        @PathVariable UUID id,
        @RequestParam(required = false, defaultValue = "false") boolean force,
        SortParameters sortParameters
    )
        throws NotFoundException,
            SortByValidationException,
            UpdateFundStatsException {
        Portfolio portfolio = portfolioService.getPortfolio(id);

        return service.getFundStats(portfolio, sortParameters, force);
    }

    @GetMapping("predictions")
    public List<PortfolioFundBuyPred> getPredictions(
        @PathVariable UUID id, FundFilter filter, float budget
    )
        throws NotFoundException {
        Portfolio portfolio = portfolioService.getPortfolio(id);

        return service.getPredictions(portfolio, filter, budget);
    }
}
