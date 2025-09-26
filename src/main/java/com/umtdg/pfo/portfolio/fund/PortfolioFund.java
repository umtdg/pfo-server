package com.umtdg.pfo.portfolio.fund;

import java.util.UUID;

import com.umtdg.pfo.fund.Fund;
import com.umtdg.pfo.portfolio.Portfolio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "portfolio_fund")
@IdClass(PortfolioFundId.class)
public class PortfolioFund {
    @Id
    @Column(name = "fund_code", length = 3)
    private String fundCode;

    @Id
    @Column(name = "portfolio_id", length = 36)
    private UUID portfolioId;

    @Column(name = "weight", nullable = false)
    private float weight;

    @Column(name = "normalized_weight", nullable = false)
    private float normWeight;

    @Column(name = "min_amount", nullable = false)
    private int minAmount = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fund_code", insertable = false, updatable = false)
    private Fund fund;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", insertable = false, updatable = false)
    private Portfolio portfolio;

    public PortfolioFund() {
    }

    public PortfolioFund(String fundCode, UUID portfolioId, float weight) {
        this(fundCode, portfolioId, weight, 1);
    }

    public PortfolioFund(
        String fundCode, UUID portfolioId, float weight, int minAmount
    ) {
        this.fundCode = fundCode;
        this.portfolioId = portfolioId;
        this.weight = weight;
        this.minAmount = minAmount;
    }

    public String getFundCode() {
        return fundCode;
    }

    public void setFundCode(String fundCode) {
        this.fundCode = fundCode;
    }

    public UUID getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(UUID portfolioId) {
        this.portfolioId = portfolioId;
    }

    public Fund getFund() {
        return fund;
    }

    public void setFund(Fund fund) {
        this.fund = fund;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getNormWeight() {
        return normWeight;
    }

    public void setNormWeight(float normWeight) {
        this.normWeight = normWeight;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(int minAmount) {
        this.minAmount = minAmount;
    }

    @Override
    public String toString() {
        return String
            .format(
                "PortfolioFund{fundCode=%s, portfolioId=%s, weight=%.2f, normWeight=%.2f, minAmount=%d}",
                fundCode,
                portfolioId,
                weight,
                normWeight,
                minAmount
            );
    }
}
