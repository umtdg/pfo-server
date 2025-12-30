package com.umtdg.pfo.portfolio.price;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Sort;

import com.umtdg.pfo.ViewRepository;

public interface PortfolioFundPriceRepository
    extends
        ViewRepository<PortfolioFundPrice, PortfolioFundPriceId> {
    List<PortfolioFundPrice> findAllByPortfolioId(UUID portfolioId, Sort sort);

    List<PortfolioFundPrice> findAllByPortfolioIdAndDate(
        UUID portfolioId, LocalDate date
    );

    List<PortfolioFundPrice> findAllByPortfolioIdAndDateAndCodeIn(
        UUID portfolioId, LocalDate date, List<String> codes
    );
}
