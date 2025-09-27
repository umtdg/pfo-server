package com.umtdg.pfo.portfolio.price;

import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.annotations.View;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "portfolio_fund_price_view")
@IdClass(PortfolioFundPriceId.class)
@View(
    query = """
        select
            pf.portfolio_id,
            fi.code,
            fi.date,
            fi.title,
            pf.normalized_weight,
            pf.min_amount,
            fi.price
        from portfolio_fund pf
        inner join fund_info_view fi on pf.fund_code = fi.code
        """
)
public class PortfolioFundPrice {
    @Id
    @Column(name = "portfolio_id")
    private UUID portfolioId;

    @Id
    @Column(name = "code", length = 3)
    private String code;

    @Id
    @Column(name = "date")
    private LocalDate date;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "normalized_weight", nullable = false)
    private float normalizedWeight;

    @Column(name = "min_amount", nullable = false)
    private int minAmount;

    @Column(name = "price", nullable = false)
    private float price;

    public PortfolioFundPrice() {
    }

    public PortfolioFundPrice(
        String code, String title, float normWeight, int minAmount, float price,
        LocalDate date
    ) {
        this.code = code;
        this.title = title;
        this.normalizedWeight = normWeight;
        this.minAmount = minAmount;
        this.price = price;
        this.date = date;
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

    public float getNormalizedWeight() {
        return normalizedWeight;
    }

    public void setNormalizedWeight(float normalizedWeight) {
        this.normalizedWeight = normalizedWeight;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(int minAmount) {
        this.minAmount = minAmount;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
