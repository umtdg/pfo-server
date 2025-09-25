package com.umtdg.pfo.fund;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.umtdg.pfo.portfolio.PortfolioFundPrice;

public interface FundPriceRepository extends JpaRepository<FundPrice, FundPriceId> {
    @Query(value = "SELECT MAX(fp.date) FROM FundPrice fp")
    LocalDate findLatestDate();

    @Query(
        value = "SELECT * FROM fund_price"
            + " fp INNER JOIN portfolio_fund pf ON pf.fund_code = fp.code"
            + " WHERE pf.portfolio_id = :id AND fp.date = :date", nativeQuery = true
    )
    Set<FundPrice> findAllByDateNaive(
        @Param("date") LocalDate date, @Param("id") UUID id
    );

    @Query(
        value = "SELECT f.code as code, f.title as title,"
            + " pf.normalized_weight as normWeight, pf.min_amount as minAmount,"
            + " fp.price as price"
            + " FROM fund f"
            + " INNER JOIN portfolio_fund pf ON pf.fund_code = f.code"
            + " INNER JOIN fund_price fp ON fp.code = f.code"
            + " WHERE pf.portfolio_id = :id AND fp.date = :date", nativeQuery = true
    )
    List<PortfolioFundPrice> findAllByDate(
        @Param("id") UUID id, @Param("date") LocalDate date
    );

    @Query(
        value = "SELECT f.code as code, f.title as title,"
            + " pf.normalized_weight as normWeight, pf.min_amount as minAmount,"
            + " fp.price as price"
            + " FROM fund f"
            + " INNER JOIN portfolio_fund pf ON pf.fund_code = f.code"
            + " INNER JOIN fund_price fp ON fp.code = f.code"
            + " WHERE pf.portfolio_id = :id AND f.code IN :codes AND fp.date = :date", nativeQuery = true
    )
    List<PortfolioFundPrice> findAllByDateAndCode(
        @Param("id") UUID id, @Param("date") LocalDate date,
        @Param("codes") List<String> codes
    );

    List<FundPrice> findAllByDateIn(List<LocalDate> dates);
}
