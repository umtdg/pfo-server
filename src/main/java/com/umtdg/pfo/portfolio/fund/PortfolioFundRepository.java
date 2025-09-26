package com.umtdg.pfo.portfolio.fund;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface PortfolioFundRepository
    extends
        JpaRepository<PortfolioFund, PortfolioFundId> {
    @Modifying
    void deleteAllByPortfolioIdAndFundCodeIn(UUID portfolioId, List<String> codes);

    List<PortfolioFund> findAllByPortfolioId(UUID portfolioId);
}
