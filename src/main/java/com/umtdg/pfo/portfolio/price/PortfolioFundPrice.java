package com.umtdg.pfo.portfolio.price;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.View;

import com.fasterxml.jackson.annotation.JsonProperty;

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
        SELECT
            pf.portfolio_id,
            fi.code,
            fi.date,
            fi.price,
            fi.title,
            pf.normalized_weight,
            pf.min_amount,
            pf.owned_amount,
            pf.money_spent
        FROM portfolio_fund pf
        INNER JOIN fund_info_view fi ON pf.fund_code = fi.code
        """
)
public class PortfolioFundPrice {
    public static final Set<String> ALLOWED_SORT_PROPERTIES = Set
        .of("code", "date", "title", "normalizedWeight", "minAmount", "price");

    @Id
    @Column(name = "portfolio_id")
    @JsonProperty("portfolio_id")
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
    @JsonProperty("normalized_weight")
    private float normalizedWeight;

    @Column(name = "min_amount", nullable = false)
    @JsonProperty("min_amount")
    private int minAmount;

    @Column(name = "owned_amount", nullable = false)
    @JsonProperty("owned_amount")
    private int ownedAmount = 0;

    @Column(name = "money_spent", nullable = false)
    @JsonProperty("money_spent")
    private double moneySpent = 0.0;

    @Column(name = "price", nullable = false)
    private double price = 0.0;

    public PortfolioFundPrice() {
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getOwnedAmount() {
        return ownedAmount;
    }

    public void setOwnedAmount(int ownedAmount) {
        this.ownedAmount = ownedAmount;
    }

    public double getMoneySpent() {
        return moneySpent;
    }

    public void setMoneySpent(double totalMoneySpent) {
        this.moneySpent = totalMoneySpent;
    }
}
