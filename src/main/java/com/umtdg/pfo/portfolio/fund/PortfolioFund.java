package com.umtdg.pfo.portfolio.fund;

import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "portfolio_fund")
@IdClass(PortfolioFundId.class)
public class PortfolioFund {
    public static final Set<String> ALLOWED_SORT_PROPERTIES = Set
        .of(
            "code",
            "weight",
            "normWeight",
            "minAmount",
            "ownedAmount",
            "moneySpent"
        );

    @Id
    @Column(name = "code", length = 3)
    @JsonProperty
    private String code;

    @Id
    @Column(name = "portfolio_id", length = 36)
    @JsonProperty("portfolio_id")
    private UUID portfolioId;

    @Column(name = "weight", nullable = false)
    private double weight = 50;

    @Column(name = "normalized_weight", nullable = false)
    @JsonProperty("normalized_weight")
    private double normWeight = 0;

    @Column(name = "min_amount", nullable = false)
    @JsonProperty("min_amount")
    private int minAmount = 1;

    @Column(name = "owned_amount", nullable = false)
    @JsonProperty("owned_amount")
    private int ownedAmount = 0;

    @Column(name = "money_spent", nullable = false)
    @JsonProperty("money_spent")
    private double moneySpent = 0;

    public PortfolioFund() {
    }

    public PortfolioFund(String code, UUID portfolioId) {
        this.code = code;
        this.portfolioId = portfolioId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public UUID getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(UUID portfolioId) {
        this.portfolioId = portfolioId;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getNormWeight() {
        return normWeight;
    }

    public void setNormWeight(double normWeight) {
        this.normWeight = normWeight;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(int minAmount) {
        this.minAmount = minAmount;
    }

    public int getOwnedAmount() {
        return ownedAmount;
    }

    public void setOwnedAmount(int ownedAmount) {
        this.ownedAmount = ownedAmount;
    }

    public double getMoneySpent() {
        return moneySpent;
    }

    public void setMoneySpent(double moneySpent) {
        this.moneySpent = moneySpent;
    }

    @Override
    public String toString() {
        return String
            .format(
                "PortfolioFund{fundCode=%s, portfolioId=%s, weight=%.2f, normWeight=%.2f, minAmount=%d}",
                code,
                portfolioId,
                weight,
                normWeight,
                minAmount
            );
    }
}
