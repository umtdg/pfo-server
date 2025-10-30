package com.umtdg.pfo.fund;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;

@Entity
@Table(name = "fund")
public class Fund {
    @Id
    @Column(name = "code", length = 3)
    private String code;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "provider", nullable = false)
    private String provider;

    public Fund(String code, String title, String provider) {
        this.code = code;
        this.title = title;
        this.provider = provider;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public String getProvider() {
        return provider;
    }
}
