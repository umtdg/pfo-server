package com.umtdg.pfo.fund;

import java.util.Objects;

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

    public Fund() {
    }

    public Fund(String code, String title, String provider) {
        this.code = code;
        this.title = title;
        this.provider = provider;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof Fund))
            return false;

        Fund other = (Fund) obj;
        return Objects.equals(code, other.code);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(code);
    }
}
