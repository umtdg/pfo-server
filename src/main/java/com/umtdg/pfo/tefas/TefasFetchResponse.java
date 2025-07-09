package com.umtdg.pfo.tefas;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.umtdg.pfo.tefas.TefasFund;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TefasFetchResponse {
    private long draw = 0;
    private long recordsTotal = 0;
    private long recordsFiltered = 0;
    private List<TefasFund> data = new ArrayList<>();

    public TefasFetchResponse() {
    }

    @Override
    public String toString() {
        return String
            .format("%ul/%ul (draw=%ul) records", recordsFiltered, recordsTotal, draw);
    }

    public long getDraw() {
        return draw;
    }

    public void setDraw(long draw) {
        this.draw = draw;
    }

    public long getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(long recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public long getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(long recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    public List<TefasFund> getData() {
        return data;
    }

    public void setData(List<TefasFund> data) {
        this.data = data;
    }
}
