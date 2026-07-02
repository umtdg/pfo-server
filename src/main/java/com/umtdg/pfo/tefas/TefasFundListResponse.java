package com.umtdg.pfo.tefas;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TefasFundListResponse extends TefasResponse {
    @JsonProperty("toplamSayi")
    private Integer totalCount;

    @JsonProperty("toplamSayfa")
    private Integer pageCount;

    private List<TefasFund> resultList = new ArrayList<>();

    public Integer getTotalCount() {
        return totalCount;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public List<TefasFund> getResultList() {
        return resultList;
    }

    public void setResultList(List<TefasFund> resultList) {
        this.resultList = resultList;
    }
}
