package com.umtdg.pfo.portfolio.price;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PortfolioFundPriceRepository
    extends
        JpaRepository<PortfolioFundPrice, PortfolioFundPriceId> {
    List<PortfolioFundPrice> findAllByPortfolioId(UUID portfolioId, Sort sort);

    List<PortfolioFundPrice> findAllByPortfolioIdAndDate(
        UUID portfolioId, LocalDate date
    );

    @Query(
        value = "SELECT pfp.* FROM portfolio_fund_price_view pfp"
            + " INNER JOIN ("
            + "SELECT code, MAX(date) AS max_date FROM portfolio_fund_price_view GROUP BY code"
            + ") pfpm ON pfp.code = pfpm.code AND pfp.date = pfpm.max_date", nativeQuery = true
    )
    List<PortfolioFundPrice> findAllLatestByPortfolioId(UUID portfolioId, Sort sort);
}
