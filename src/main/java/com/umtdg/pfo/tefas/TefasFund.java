package com.umtdg.pfo.tefas;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.umtdg.pfo.fund.Fund;
import com.umtdg.pfo.fund.price.FundPrice;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TefasFund {
    @JsonProperty("FONKODU")
    private String code;

    @JsonProperty("TARIH")
    @JsonDeserialize(using = EpochDeserializer.class)
    private LocalDate date;

    @JsonProperty("FIYAT")
    private float price;

    @JsonProperty("FONUNVAN")
    private String title;

    @JsonProperty("PORTFOYBUYUKLUK")
    private float marketCap = 0.0f;

    @JsonProperty("TEDPAYSAYISI")
    private float numShares = 0.0f;

    @JsonProperty("KISISAYISI")
    private float numInvestors = 0.0f;

    public Fund toFund() {
        return new Fund(code, title, "TEFAS");
    }

    public FundPrice toFundPrice() {
        return new FundPrice(code, date, price, marketCap);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(float marketCap) {
        this.marketCap = marketCap;
    }

    public float getNumShares() {
        return numShares;
    }

    public void setNumShares(float numShares) {
        this.numShares = numShares;
    }

    public float getNumInvestors() {
        return numInvestors;
    }

    public void setNumInvestors(float numInvestors) {
        this.numInvestors = numInvestors;
    }
}

class EpochDeserializer extends StdDeserializer<LocalDate> {
    private static final long serialVersionUID = 1;

    public EpochDeserializer() {
        this(null);
    }

    public EpochDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException {
        return Instant
            .ofEpochMilli(Long.parseLong(p.readValueAs(String.class)))
            .atZone(ZoneId.systemDefault())
            .toLocalDate();

    }
}
