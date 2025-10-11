package com.umtdg.pfo.fund;

import java.sql.PreparedStatement;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.umtdg.pfo.fund.price.FundPrice;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class FundBatchRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public void batchInsertFunds(List<Fund> funds) {
        if (funds.isEmpty()) {
            return;
        }

        String query = "INSERT INTO fund (code, title, provider) VALUES (?, ?, ?) ON CONFLICT (code) DO UPDATE SET title = EXCLUDED.title, provider = EXCLUDED.provider";

        Session session = entityManager.unwrap(Session.class);
        session.doWork(connection -> {
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                for (Fund fund : funds) {
                    ps.setString(1, fund.getCode());
                    ps.setString(2, fund.getTitle());
                    ps.setString(3, fund.getProvider());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        });
    }

    public void batchInsertFundPrices(List<FundPrice> prices) {
        if (prices.isEmpty()) {
            return;
        }

        String query = "INSERT INTO fund_price (code, date, price, total_value) VALUES (?, ?, ?, ?) ON CONFLICT (code, date) DO UPDATE SET price = EXCLUDED.price, total_value = EXCLUDED.total_value";

        Session session = entityManager.unwrap(Session.class);
        session.doWork(con -> {
            try (PreparedStatement ps = con.prepareStatement(query)) {
                for (FundPrice price : prices) {
                    ps.setString(1, price.getCode());
                    ps.setDate(2, java.sql.Date.valueOf(price.getDate()));
                    ps.setFloat(3, price.getPrice());
                    ps.setFloat(4, price.getTotalValue());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        });
    }
}
