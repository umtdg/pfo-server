package com.umtdg.pfo.fund;

import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "fund_price")
@IdClass(FundPriceId.class)
public class FundPrice {
    @Id
    @Column(name = "code", length = 3)
    private String code;

    @Id
    @Column(name = "date")
    private LocalDate date;

    @Column(name = "price", nullable = false)
    private float price = 0.0f;

    @Column(name = "total_value", nullable = false)
    private float totalValue = 0.0f;

    public FundPrice() {
    }

    public FundPrice(String code, LocalDate date, float price, float totalValue) {
        this.code = code;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof FundPrice))
            return false;

        FundPrice other = (FundPrice) obj;
        return Objects.equals(code, other.code) && Objects.equals(date, other.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, date);
    }
}
