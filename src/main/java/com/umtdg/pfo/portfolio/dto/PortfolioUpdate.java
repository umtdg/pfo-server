package com.umtdg.pfo.portfolio.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.umtdg.pfo.portfolio.fund.dto.PortfolioFundAdd;

import jakarta.validation.Valid;

public class PortfolioUpdate {
    @Valid
    @JsonProperty("add_codes")
    private List<PortfolioFundAdd> addCodes = new ArrayList<>();

    @JsonProperty("remove_codes")
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
