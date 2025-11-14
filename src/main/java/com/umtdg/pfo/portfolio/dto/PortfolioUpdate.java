package com.umtdg.pfo.portfolio.dto;

import java.util.List;

import com.umtdg.pfo.portfolio.fund.dto.PortfolioFundAdd;

import jakarta.validation.Valid;

public record PortfolioUpdate(
    @Valid List<PortfolioFundAdd> addCodes, List<String> removeCodes
) {
}
