package com.umtdg.pfo.fund;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.umtdg.pfo.fund.price.FundPrice;
import com.umtdg.pfo.fund.stats.FundStats;

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
                    ps.setDouble(3, price.getPrice());
                    ps.setDouble(4, price.getTotalValue());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        });
    }

    public void batchInsertFundStats(List<FundStats> stats) {
        if (stats.isEmpty()) {
            return;
        }

        // The returns endpoint is not one-to-one with the list endpoint; it can return
        // fund codes we've never ingested. The WHERE EXISTS guard silently skips any
        // code not already present in the fund table (the SELECT yields no row).
        String query = "INSERT INTO fund_stats (code, daily_return, monthly_return, three_monthly_return, six_monthly_return, yearly_return, three_yearly_return, five_yearly_return) SELECT ?, ?, ?, ?, ?, ?, ?, ? WHERE EXISTS (SELECT 1 FROM fund WHERE fund.code = ?) ON CONFLICT (code) DO UPDATE SET daily_return = EXCLUDED.daily_return, monthly_return = EXCLUDED.monthly_return, three_monthly_return = EXCLUDED.three_monthly_return, six_monthly_return = EXCLUDED.six_monthly_return, yearly_return = EXCLUDED.yearly_return, three_yearly_return = EXCLUDED.three_yearly_return, five_yearly_return = EXCLUDED.five_yearly_return";

        Session session = entityManager.unwrap(Session.class);
        session.doWork(con -> {
            try (PreparedStatement ps = con.prepareStatement(query)) {
                for (FundStats stat : stats) {
                    ps.setString(1, stat.getCode());
                    ps.setObject(2, stat.getDailyReturn(), Types.DOUBLE);
                    ps.setObject(3, stat.getMonthlyReturn(), Types.DOUBLE);
                    ps.setObject(4, stat.getThreeMonthlyReturn(), Types.DOUBLE);
                    ps.setObject(5, stat.getSixMonthlyReturn(), Types.DOUBLE);
                    ps.setObject(6, stat.getYearlyReturn(), Types.DOUBLE);
                    ps.setObject(7, stat.getThreeYearlyReturn(), Types.DOUBLE);
                    ps.setObject(8, stat.getFiveYearlyReturn(), Types.DOUBLE);
                    ps.setString(9, stat.getCode());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        });
    }
}
