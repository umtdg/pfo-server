package com.umtdg.pfo.portfolio;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PortfolioFundRepository
    extends
        JpaRepository<PortfolioFund, PortfolioFundId> {
    @Modifying
    @Query(
        "DELETE FROM PortfolioFund pf WHERE pf.portfolioId = :id AND pf.fundCode IN :codes"
    )
    void deleteAllByPortfolioId(
        @Param("id") UUID id,
        @Param("codes") List<String> codes
    );

    @Query(
        "SELECT pf FROM PortfolioFund pf WHERE pf.portfolioId = :id"
    )
    List<PortfolioFund> findAllByPortfolioId(@Param("id") UUID id);

    // @Query
    // List<PortfolioFundPrice> findAllByPortfolioIdWithPrice(
    // @Param("id") UUID id, @Param("date") LocalDate date
    // );
}
