package com.umtdg.pfo.fund;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FundStatsRepository extends JpaRepository<FundStats, String> {
    @Query(value = "SELECT MAX(fs.updatedAt) FROM FundStats fs")
    LocalDate findLatestUpdateDate();

    List<FundStats> findByCodeIn(List<String> codes);

    List<FundStats> findByCodeIn(List<String> codes, Sort sort);
}
