package com.umtdg.pfo.portfolio;

import java.util.Objects;
import java.util.UUID;

import com.umtdg.pfo.portfolio.PortfolioFund;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class PortfolioFundAdd {
    @NotBlank
    @Size(min = 3, max = 3)
    private String fundCode;

    @PositiveOrZero
    private Float weight;

    @PositiveOrZero
    private Integer minAmount;

    public PortfolioFundAdd() {
        weight = 0.0f;
        minAmount = 1;
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

    public PortfolioFund toPortfolioFund(UUID portfolioId) {
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
