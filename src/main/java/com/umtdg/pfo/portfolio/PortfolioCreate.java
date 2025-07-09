package com.umtdg.pfo.portfolio;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class PortfolioCreate {
    @NotNull
    @NotEmpty
    public String name;
}
