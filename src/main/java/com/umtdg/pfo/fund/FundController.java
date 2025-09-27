package com.umtdg.pfo.fund;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import com.umtdg.pfo.DateUtils;
import com.umtdg.pfo.SortParameters;
import com.umtdg.pfo.fund.info.FundInfoRepository;
import com.umtdg.pfo.fund.price.FundPriceRepository;
import com.umtdg.pfo.fund.stats.FundStatsRepository;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/f")
public class FundController {
    private final FundService service;

    private final FundPriceRepository priceRepository;

    public FundController(
        FundService service,
        FundPriceRepository priceRepository, FundStatsRepository statsRepository,
        FundInfoRepository infoRepository
    ) {
        this.service = service;
        this.priceRepository = priceRepository;
    }

    private static final Set<String> ALLOWED_FUND_INFO_SORT_PROPERTIES = Set
        .of(
            "code",
            "title",
            "provider",
            "price",
            "totalValue"
        );

    @GetMapping
    @Transactional
    public ResponseEntity<?> get(
        FundFilter filter, SortParameters sortParameters
    ) {
        Sort sort = sortParameters
            .validate(
                ALLOWED_FUND_INFO_SORT_PROPERTIES,
                Sort.by(Sort.Direction.ASC, "code")
            );

        filter = DateUtils.checkFundDateFilters(filter, priceRepository);

        Optional<ResponseEntity<?>> response = service.updateTefasFunds(filter);
        if (response.isPresent()) {
            return response.get();
        }

        return service.getFundInfos(filter, sort);
    }

    private static final Set<String> ALLOWED_FUND_STAT_SORT_PROPERTIES = Set
        .of(
            "code",
            "title",
            "lastPrice",
            "totalValue",
            "dailyReturn",
            "monthlyReturn",
            "threeMonthlyReturn",
            "sixMonthlyReturn",
            "yearlyReturn",
            "threeYearlyReturn",
            "fiveYearlyReturn"
        );

    @GetMapping("stats")
    @Transactional
    @Validated
    ResponseEntity<?> getStats(
        @RequestParam(required = false) List<String> codes,
        @RequestParam(required = false, defaultValue = "false") boolean force,
        SortParameters sortParameters
    ) {
        Sort sort = sortParameters
            .validate(
                ALLOWED_FUND_STAT_SORT_PROPERTIES,
                Sort.by(Sort.Direction.ASC, "code")
            );

        Optional<ResponseEntity<?>> response = service.updateFundStats(force);
        if (response.isPresent()) {
            return response.get();
        }

        return service.getFundStats(codes, sort);
    }
}
