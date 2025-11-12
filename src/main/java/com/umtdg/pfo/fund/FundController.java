package com.umtdg.pfo.fund;

import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.umtdg.pfo.SortParameters;
import com.umtdg.pfo.exception.SortByValidationException;
import com.umtdg.pfo.exception.UpdateFundStatsException;
import com.umtdg.pfo.fund.stats.FundStats;
import com.umtdg.pfo.fund.info.FundInfo;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/f")
public class FundController {
    private final FundService service;

    public FundController(FundService service) {
        this.service = service;
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
    public List<FundInfo> get(
        @Valid FundFilter filter, @Valid SortParameters sortParameters
    )
        throws SortByValidationException {
        return service.updateAndGetFundInfos(filter, sortParameters);
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
        throws SortByValidationException,
            UpdateFundStatsException {
        return service.updateAndGetFundStats(codes, sortParameters, force);
    }
}
