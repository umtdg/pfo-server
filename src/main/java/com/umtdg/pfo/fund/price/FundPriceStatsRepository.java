package com.umtdg.pfo.fund.price;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FundPriceStatsRepository
    extends
        JpaRepository<FundPriceStats, String> {
    List<FundPriceStats> findAllByCodeIn(Iterable<String> codes, Sort sort);
}
