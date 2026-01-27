package com.umtdg.pfo.fund.info;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.umtdg.pfo.fund.FundCodeDatePairId;

public interface FundInfoRepository
    extends
        JpaRepository<FundInfo, FundCodeDatePairId> {
    List<FundInfo> findAllByDateIn(Iterable<LocalDate> dates);

    List<FundInfo> findAllByDateInOrderByCodeAscDateDesc(Iterable<LocalDate> dates);

    List<FundInfo> findAllByDate(LocalDate date, Sort sort);

    List<FundInfo> findAllByDateAndCodeIn(
        LocalDate date, Iterable<String> codes, Sort sort
    );

    @Query(
        value = "SELECT fi.* FROM fund_info fi"
            + " INNER JOIN (SELECT code, MAX(date) AS max_date FROM fund_info GROUP BY code) fim"
            + " ON fi.code = fim.code AND fi.date = fim.max_date",
        nativeQuery = true
    )
    List<FundInfo> findAllLatest(Sort sort);

    @Query(
        value = "SELECT fi.* FROM fund_info fi"
            + " INNER JOIN (SELECT code, MAX(date) AS max_date FROM fund_info GROUP BY code) fim"
            + " ON fi.code = fim.code AND fi.date = fim.max_date"
            + " WHERE fi.code IN :codes", nativeQuery = true
    )
    List<FundInfo> findAllLatestByCodeIn(Iterable<String> codes, Sort sort);
}
