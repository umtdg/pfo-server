package com.umtdg.pfo.portfolio.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class PortfolioCreate {
    @NotNull
    @NotEmpty
    public String name;
}
