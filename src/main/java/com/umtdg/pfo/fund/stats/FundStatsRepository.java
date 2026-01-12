package com.umtdg.pfo.fund.stats;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FundStatsRepository
    extends
        JpaRepository<FundStats, String> {
    List<FundStats> findByCodeIn(List<String> codes);

    List<FundStats> findByCodeIn(List<String> codes, Sort sort);

    @Query(value = "SELECT * FROM update_fund_stats()", nativeQuery = true)
    Map<String, Object> updateFundStats();
}
