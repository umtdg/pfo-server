package com.umtdg.pfo.portfolio.fund.dto;

public record PortfolioFundPred(
    String code, String title, float price, int amount, float weight
) {
}
