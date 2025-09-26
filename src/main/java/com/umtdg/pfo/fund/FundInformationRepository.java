package com.umtdg.pfo.fund;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;

import com.umtdg.pfo.ViewRepository;

public interface FundInformationRepository
    extends
        ViewRepository<FundInformation, FundPriceId> {
    List<FundInformation> findAllByDate(LocalDate date, Sort sort);

    List<FundInformation> findAllByDateAndCodeIn(
        LocalDate date, List<String> codes, Sort sort
    );
}
