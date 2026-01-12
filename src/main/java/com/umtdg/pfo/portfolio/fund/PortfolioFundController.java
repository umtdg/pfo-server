package com.umtdg.pfo.portfolio.fund;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.umtdg.pfo.SortParameters;
import com.umtdg.pfo.exception.NotFoundException;
import com.umtdg.pfo.exception.SortByValidationException;
import com.umtdg.pfo.fund.price.FundPriceStats;
import com.umtdg.pfo.portfolio.Portfolio;
import com.umtdg.pfo.portfolio.PortfolioService;
import com.umtdg.pfo.portfolio.fund.dto.PortfolioFundPred;
import com.umtdg.pfo.portfolio.price.PortfolioFundPrice;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/p/{id}/f")
@Transactional
public class PortfolioFundController {
    private final PortfolioFundService service;
    private final PortfolioService portfolioService;

    public PortfolioFundController(
        PortfolioFundService service, PortfolioService portfolioService
    ) {
        this.service = service;
        this.portfolioService = portfolioService;
    }

    @GetMapping()
    public List<PortfolioFundPrice> getPrices(
        @PathVariable UUID id,
        @RequestParam(required = false) @DateTimeFormat(
            pattern = "MM.dd.yyyy"
        ) @Valid LocalDate date,
        @Valid SortParameters sortParameters
    )
        throws NotFoundException,
            SortByValidationException {
        Portfolio portfolio = portfolioService.getPortfolio(id);

        return service.getPrices(portfolio, date, sortParameters);
    }

    @GetMapping("stats")
    public List<FundPriceStats> getStats(
        @PathVariable UUID id, SortParameters sortParameters
    )
        throws NotFoundException,
            SortByValidationException {
        Portfolio portfolio = portfolioService.getPortfolio(id);

        return service.getPricesWithStats(portfolio, sortParameters);
    }

    @GetMapping("predictions")
    public List<PortfolioFundPred> getPredictions(
        @PathVariable UUID id,
        @RequestParam(required = true) @Valid @NotNull double budget
    )
        throws NotFoundException {
        Portfolio portfolio = portfolioService.getPortfolio(id);

        return service.getPredictions(portfolio, budget);
    }
}
