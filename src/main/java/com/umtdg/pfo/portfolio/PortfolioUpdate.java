package com.umtdg.pfo.portfolio;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;

public class PortfolioUpdate {
    @Valid
    private List<PortfolioFundAdd> addCodes = new ArrayList<>();

    private List<String> removeCodes = new ArrayList<>();

    public PortfolioUpdate() {
    }

    public List<String> getRemoveCodes() {
        return removeCodes;
    }

    public void setRemoveCodes(List<String> fundRemoveList) {
        this.removeCodes = fundRemoveList;
    }

    public List<PortfolioFundAdd> getAddCodes() {
        return addCodes;
    }

    public void setAddCodes(List<PortfolioFundAdd> addCodes) {
        this.addCodes = addCodes;
    }
}
