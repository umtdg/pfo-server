package com.umtdg.pfo.tefas;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TefasFundReturnsResponse extends TefasResponse {
    private List<TefasFundReturns> resultList = new ArrayList<>();

    public List<TefasFundReturns> getResultList() {
        return resultList;
    }

    public void setResultList(List<TefasFundReturns> resultList) {
        this.resultList = resultList;
    }
}
