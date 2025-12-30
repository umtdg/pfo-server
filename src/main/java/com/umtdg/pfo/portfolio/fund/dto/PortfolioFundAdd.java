package com.umtdg.pfo.portfolio.fund.dto;

import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.umtdg.pfo.portfolio.fund.PortfolioFund;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class PortfolioFundAdd {
    @NotBlank
    @Size(min = 3, max = 3)
    @JsonProperty("fund_code")
    private String fundCode;

    @PositiveOrZero
    private Float weight;

    @PositiveOrZero
    @JsonProperty("min_amount")
    private Integer minAmount;

    @PositiveOrZero
    @JsonProperty("owned_amount")
    private Integer ownedAmount;

    @PositiveOrZero
    @JsonProperty("total_money_spent")
    private Double totalMoneySpent;

    public PortfolioFundAdd() {
    }

    public String getFundCode() {
        return fundCode;
    }

    public void setFundCode(String fundCode) {
        this.fundCode = fundCode;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Integer getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(Integer minAmount) {
        this.minAmount = minAmount;
    }

    public Integer getOwnedAmount() {
        return ownedAmount;
    }

    public void setOwnedAmount(Integer ownedAmount) {
        this.ownedAmount = ownedAmount;
    }

    public Double getTotalMoneySpent() {
        return totalMoneySpent;
    }

    public void setTotalMoneySpent(Double totalMoneySpent) {
        this.totalMoneySpent = totalMoneySpent;
    }

    public PortfolioFund toPortfolioFund(UUID portfolioId) {
        float weight = this.weight == null ? 50 : this.weight;
        int minAmount = this.minAmount == null ? 1 : this.minAmount;
        return new PortfolioFund(fundCode, portfolioId, weight, minAmount);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof PortfolioFundAdd))
            return false;

        PortfolioFundAdd other = (PortfolioFundAdd) obj;
        return Objects.equals(fundCode, other.fundCode);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fundCode);
    }
}
