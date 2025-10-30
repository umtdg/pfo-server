package com.umtdg.pfo.fund.info;

import java.time.LocalDate;

import org.hibernate.annotations.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.umtdg.pfo.fund.FundCodeDatePairId;

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
@IdClass(FundCodeDatePairId.class)
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
    @JsonProperty
    private String code;

    @Id
    @Column(name = "date")
    @JsonProperty
    private LocalDate date;

    @Column(name = "title", nullable = false)
    @JsonProperty
    private String title;

    @Column(name = "provider", nullable = false)
    @JsonProperty
    private String provider;

    @Column(name = "price", nullable = false)
    @JsonProperty
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

    public String getTitle() {
        return title;
    }

    public LocalDate getDate() {
        return date;
    }

    public float getPrice() {
        return price;
    }

    public float getTotalValue() {
        return totalValue;
    }
}
