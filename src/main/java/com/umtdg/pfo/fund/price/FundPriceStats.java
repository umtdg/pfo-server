package com.umtdg.pfo.fund.price;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.umtdg.pfo.fund.stats.FundStatsBase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "fund_price_stats_view")
@View(query = """
    SELECT
        fp.code,
        fp.date,
        fp.price,
        fp.total_value,
        fs.daily_return,
        fs.monthly_return,
        fs.three_monthly_return,
        fs.six_monthly_return,
        fs.yearly_return,
        fs.three_yearly_return,
        fs.five_yearly_return
    FROM fund_price fp
    INNER JOIN (
        SELECT code, MAX(date) AS max_date
        FROM fund_price
        GROUP BY code
    ) fpm ON fp.code = fpm.code AND fp.date = fpm.max_date
    INNER JOIN fund_stats fs ON fp.code = fs.code
    """)
public class FundPriceStats extends FundStatsBase {
    public static final Set<String> ALLOWED_SORT_PROPERTIES;

    static {
        ALLOWED_SORT_PROPERTIES = new HashSet<>(FundStatsBase.BASE_SORT_PROPERTIES);
        ALLOWED_SORT_PROPERTIES.add("date");
        ALLOWED_SORT_PROPERTIES.add("price");
        ALLOWED_SORT_PROPERTIES.add("totalValue");
    }

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "price")
    private double price;

    @Column(name = "total_value")
    @JsonProperty("total_value")
    private double totalValue;

    public FundPriceStats() {
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }
}
