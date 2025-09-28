package com.umtdg.pfo.fund.price;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.umtdg.pfo.fund.FundCodeDatePairId;

public interface FundPriceRepository extends JpaRepository<FundPrice, FundCodeDatePairId> {
    @Query("SELECT MAX(f.date) FROM FundPrice f")
    LocalDate findTopDate();

    List<FundPrice> findAllByDateIn(List<LocalDate> dates);

    List<FundPrice> findAllByDateIn(List<LocalDate> dates, Sort sort);
}
