package com.umtdg.pfo.fund.info;

import java.time.LocalDate;
import java.util.Set;

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
        SELECT
            f.code,
            f.title,
            f.provider,
            fp.date,
            fp.price,
            fp.total_value
        FROM fund f
        INNER JOIN fund_price fp ON f.code = fp.code
        """
)
public class FundInfo {
    public static Set<String> ALLOWED_SORT_PROPERTIES = Set
        .of(
            "code",
            "date",
            "title",
            "provider",
            "price",
            "total_value"
        );

    @Id
    @Column(name = "code", length = 3)
    @JsonProperty
    private String code;

    @Column(name = "title", nullable = false)
    @JsonProperty
    private String title;

    @Column(name = "provider", nullable = false)
    @JsonProperty
    private String provider;

    @Id
    @Column(name = "date")
    @JsonProperty
    private LocalDate date;

    @Column(name = "price", nullable = false)
    @JsonProperty
    private double price = 0.0f;

    @Column(name = "total_value", nullable = false)
    @JsonProperty("total_value")
    private double totalValue = 0.0f;

    public FundInfo() {
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

    public double getPrice() {
        return price;
    }

    public double getTotalValue() {
        return totalValue;
    }
}
