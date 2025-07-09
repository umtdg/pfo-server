package com.umtdg.pfo.fund;

import java.sql.PreparedStatement;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class FundBatchRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void batchInsertFunds(List<Fund> funds) {
        String sql = "INSERT INTO fund (code, title, provider) VALUES (?, ?, ?) ON CONFLICT (code) DO UPDATE SET title = EXCLUDED.title, provider = EXCLUDED.provider";

        Session session = entityManager.unwrap(Session.class);
        session.doWork(connection -> {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
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
        String sql = "INSERT INTO fund_price (code, date, price) VALUES (?, ?, ?) ON CONFLICT (code, date) DO UPDATE SET price = EXCLUDED.price";

        Session session = entityManager.unwrap(Session.class);
        session.doWork(con -> {
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                for (FundPrice price : prices) {
                    ps.setString(1, price.getCode());
                    ps.setDate(2, java.sql.Date.valueOf(price.getDate()));
                    ps.setFloat(3, price.getPrice());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        });
    }
}
