package com.umtdg.pfo.fund.price;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FundPriceRepository extends JpaRepository<FundPrice, FundPriceId> {
    @Query("SELECT MAX(f.date) FROM FundPrice f")
    LocalDate findTopDate();

    List<FundPrice> findAllByDateIn(List<LocalDate> dates);
}
