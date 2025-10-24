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

import com.umtdg.pfo.DateUtils;
import com.umtdg.pfo.SortParameters;
import com.umtdg.pfo.exception.SortByValidationException;
import com.umtdg.pfo.fund.price.FundPriceRepository;
import com.umtdg.pfo.fund.stats.FundStats;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/f")
public class FundController {
    private final FundService service;

    private final FundPriceRepository priceRepository;

    public FundController(
        FundService service,
        FundPriceRepository priceRepository
    ) {
        this.service = service;
        this.priceRepository = priceRepository;
    }

    public static final Set<String> ALLOWED_FUND_INFO_SORT_PROPERTIES = Set
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
        @Valid FundFilter filter, @Valid SortParameters sortParameters
    )
        throws SortByValidationException {
        Sort sort = sortParameters.validate(ALLOWED_FUND_INFO_SORT_PROPERTIES);

        filter = DateUtils.checkFundDateFilters(filter, priceRepository);

        service.updateTefasFunds(filter);

        return service.getFundInfos(filter, sort);
    }

    public static final Set<String> ALLOWED_FUND_STAT_SORT_PROPERTIES = Set
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
    List<FundStats> getStats(
        @RequestParam(required = false) List<String> codes,
        @RequestParam(required = false, defaultValue = "false") boolean force,
        @Valid SortParameters sortParameters
    )
        throws SortByValidationException {
        return service.updateAndGetFundStats(codes, sortParameters, force);
    }
}
