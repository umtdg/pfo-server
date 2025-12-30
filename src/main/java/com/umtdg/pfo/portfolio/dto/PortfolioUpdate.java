package com.umtdg.pfo.portfolio.dto;

import java.util.List;
import java.util.Set;

import com.umtdg.pfo.portfolio.fund.dto.PortfolioFundUpdate;

import jakarta.validation.Valid;

public record PortfolioUpdate(
    @Valid Set<PortfolioFundUpdate> updateInfos, List<String> removeCodes
) {
}
