package com.umtdg.pfo.fund.info;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;

import com.umtdg.pfo.ViewRepository;

public interface FundInfoRepository
    extends
        ViewRepository<FundInfo, FundInfoId> {
    List<FundInfo> findAllByDate(LocalDate date, Sort sort);

    List<FundInfo> findAllByDateAndCodeIn(
        LocalDate date, List<String> codes, Sort sort
    );
}
