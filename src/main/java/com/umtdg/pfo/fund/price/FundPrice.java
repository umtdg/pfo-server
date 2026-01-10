package com.umtdg.pfo.fund.price;

import java.time.LocalDate;

import com.umtdg.pfo.fund.FundCodeDatePairId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "fund_price")
@IdClass(FundCodeDatePairId.class)
public class FundPrice {
    @Id
    @Column(name = "code", length = 3)
    private String code;

    @Id
    @Column(name = "date")
    private LocalDate date;

    @Column(name = "price", nullable = false)
    private double price = 0.0f;

    @Column(name = "total_value", nullable = false)
    private double totalValue = 0.0f;

    public FundPrice(String code, LocalDate date, double price, double totalValue) {
        this.code = code;
        this.date = date;
        this.price = price;
        this.totalValue = totalValue;
    }

    public String getCode() {
        return code;
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
