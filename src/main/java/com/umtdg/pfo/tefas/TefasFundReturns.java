package com.umtdg.pfo.tefas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.umtdg.pfo.fund.stats.FundStats;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TefasFundReturns {
    @JsonProperty("fonKodu")
    private String code;

    @JsonProperty("fonUnvan")
    private String title;

    @JsonProperty("fonTurAciklama")
    private String fundTypeDesc;

    @JsonProperty("tefasDurum")
    private Boolean tefasStatus;

    @JsonProperty("getiri1a")
    private Double return1m;

    @JsonProperty("getiri3a")
    private Double return3m;

    @JsonProperty("getiri6a")
    private Double return6m;

    @JsonProperty("getiri1y")
    private Double return1y;

    @JsonProperty("getiriyb")
    private Double returnYtd;

    @JsonProperty("getiri3y")
    private Double return3y;

    @JsonProperty("getiri5y")
    private Double return5y;

    @JsonProperty("getiriOrani")
    private Double returnRatio;

    @JsonProperty("riskDegeri")
    private String riskValue;

    // The API delivers returns as percentages (e.g. -9.4002 = -9.4%), but we store
    // them as ratios (e.g. -0.094) to stay compatible with the rest of the app.
    private static Double toRatio(Double pct) {
        return pct == null ? null : pct / 100.0;
    }

    public FundStats toFundStats() {
        FundStats stats = new FundStats();
        stats.setCode(code);
        // The returns API does not provide a daily return; leave it null.
        stats.setMonthlyReturn(toRatio(return1m));
        stats.setThreeMonthlyReturn(toRatio(return3m));
        stats.setSixMonthlyReturn(toRatio(return6m));
        stats.setYearlyReturn(toRatio(return1y));
        stats.setThreeYearlyReturn(toRatio(return3y));
        stats.setFiveYearlyReturn(toRatio(return5y));
        return stats;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFundTypeDesc() {
        return fundTypeDesc;
    }

    public void setFundTypeDesc(String fundTypeDesc) {
        this.fundTypeDesc = fundTypeDesc;
    }

    public Boolean getTefasStatus() {
        return tefasStatus;
    }

    public void setTefasStatus(Boolean tefasStatus) {
        this.tefasStatus = tefasStatus;
    }

    public Double getReturn1m() {
        return return1m;
    }

    public void setReturn1m(Double return1m) {
        this.return1m = return1m;
    }

    public Double getReturn3m() {
        return return3m;
    }

    public void setReturn3m(Double return3m) {
        this.return3m = return3m;
    }

    public Double getReturn6m() {
        return return6m;
    }

    public void setReturn6m(Double return6m) {
        this.return6m = return6m;
    }

    public Double getReturn1y() {
        return return1y;
    }

    public void setReturn1y(Double return1y) {
        this.return1y = return1y;
    }

    public Double getReturnYtd() {
        return returnYtd;
    }

    public void setReturnYtd(Double returnYtd) {
        this.returnYtd = returnYtd;
    }

    public Double getReturn3y() {
        return return3y;
    }

    public void setReturn3y(Double return3y) {
        this.return3y = return3y;
    }

    public Double getReturn5y() {
        return return5y;
    }

    public void setReturn5y(Double return5y) {
        this.return5y = return5y;
    }

    public Double getReturnRatio() {
        return returnRatio;
    }

    public void setReturnRatio(Double returnRatio) {
        this.returnRatio = returnRatio;
    }

    public String getRiskValue() {
        return riskValue;
    }

    public void setRiskValue(String riskValue) {
        this.riskValue = riskValue;
    }
}
