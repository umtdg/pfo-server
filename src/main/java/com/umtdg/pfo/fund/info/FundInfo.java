package com.umtdg.pfo.fund.info;

import java.time.LocalDate;

import org.hibernate.annotations.View;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

/**
 * FundInformation
 */
@Entity
@Table(name = "fund_info_view")
@IdClass(FundInfoId.class)
@View(
    query = """
        select
            f.code,
            fp.date,
            f.title,
            f.provider,
            fp.price,
            fp.total_value
        from fund f
        inner join fund_price fp on f.code = fp.code
        """
)
public class FundInfo {
    @Id
    @Column(name = "code", length = 3)
    private String code;

    @Id
    @Column(name = "date")
    private LocalDate date;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "price", nullable = false)
    private float price = 0.0f;

    @Column(name = "total_value", nullable = false)
    @JsonProperty("total_value")
    private float totalValue = 0.0f;

    public FundInfo() {
    }

    public FundInfo(
        String code, String title, String provider, LocalDate date, float price,
        float totalValue
    ) {
        this.code = code;
        this.title = title;
        this.provider = provider;
        this.date = date;
        this.price = price;
        this.totalValue = totalValue;
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

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
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

    public float getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(float totalValue) {
        this.totalValue = totalValue;
    }
}
