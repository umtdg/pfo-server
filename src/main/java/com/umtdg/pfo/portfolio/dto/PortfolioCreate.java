package com.umtdg.pfo.portfolio.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PortfolioCreate(@NotNull @NotEmpty String name) {
}
