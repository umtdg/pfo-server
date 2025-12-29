package com.umtdg.pfo.portfolio.fund.dto;

public record PortfolioFundBuyPred(
    String code, String title, float price, int amount, float weight
) {
}
