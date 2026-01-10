package com.umtdg.pfo.portfolio.fund;

import java.util.Objects;
import java.util.UUID;

public class PortfolioFundId {
    private String code;
    private UUID portfolioId;

    public PortfolioFundId() {
    }

    public PortfolioFundId(String code, UUID portfolioId) {
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof PortfolioFundId))
            return false;

        PortfolioFundId other = (PortfolioFundId) obj;
        return Objects.equals(code, other.code)
            && Objects.equals(portfolioId, other.portfolioId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, portfolioId);
    }

    @Override
    public String toString() {
        return String
            .format(
                "PortfolioFundId{fundCode=%s, portfolioId=%s}",
                code,
                portfolioId
            );
    }
}
