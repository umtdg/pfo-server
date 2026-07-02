package com.umtdg.pfo.fund.stats;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FundStatsRepository
    extends
        JpaRepository<FundStats, String> {
    List<FundStats> findByCodeIn(List<String> codes);

    List<FundStats> findByCodeIn(List<String> codes, Sort sort);
}
