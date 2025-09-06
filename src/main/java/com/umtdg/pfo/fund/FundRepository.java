package com.umtdg.pfo.fund;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FundRepository extends JpaRepository<Fund, String> {
    @Query(value = "SELECT f FROM Fund f WHERE code in :codes")
    List<Fund> findAllCodes(@Param("codes") List<String> codes);

    @Query(value = "SELECT f.code, f.title, f.provider, fp.date, fp.price, fp.totalValue"
            + " FROM Fund f"
            + " INNER JOIN FundPrice fp ON f.code = fp.code"
            + " WHERE f.code in :codes AND fp.date = :date")
    List<FundInformation> findInformationByCodes(@Param("codes") List<String> codes, @Param("date") LocalDate date);

    @Query(value = "SELECT f.code, f.title, f.provider, fp.date, fp.price, fp.totalValue"
            + " FROM Fund f"
            + " INNER JOIN FundPrice fp ON f.code = fp.code"
            + " WHERE fp.date = :date")
    List<FundInformation> findInformationOfAll(@Param("date") LocalDate date);
}
