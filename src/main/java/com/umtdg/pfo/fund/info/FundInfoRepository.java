package com.umtdg.pfo.fund.info;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Sort;

import com.umtdg.pfo.ViewRepository;
import com.umtdg.pfo.fund.FundCodeDatePairId;

public interface FundInfoRepository extends ViewRepository<FundInfo, FundCodeDatePairId> {
    List<FundInfo> findAllByDate(LocalDate date, Sort sort);

    List<FundInfo> findAllByDateAndCodeIn(
        LocalDate date, List<String> codes, Sort sort
    );

    List<FundInfo> findAllByDateInOrderByCodeAscDateDesc(Set<LocalDate> dates);
}
