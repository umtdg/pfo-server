package com.umtdg.pfo.portfolio.fund.dto;

public record PortfolioFundPred(
    String code, String title, double price, int amount, double weight
) {
}
