package com.umtdg.pfo.fund;

import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.umtdg.pfo.DateRange;
import com.umtdg.pfo.SortParameters;
import com.umtdg.pfo.exception.SortByValidationException;
import com.umtdg.pfo.exception.UpdateFundsException;
import com.umtdg.pfo.fund.stats.FundStats;
import com.umtdg.pfo.fund.info.FundInfo;
import com.umtdg.pfo.fund.price.FundPriceStats;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/f")
public class FundController {
    private final FundService service;

    public FundController(FundService service) {
        this.service = service;
    }

    @GetMapping
    @Transactional
    List<FundInfo> get(
        @ModelAttribute @Valid FundFilter filter,
        @ModelAttribute @Valid SortParameters sortParameters
    )
        throws SortByValidationException {
        return service.getInfos(filter, sortParameters);
    }

    @GetMapping("stats")
    @Transactional
    List<FundStats> getStats(
        @Valid Set<@Size(
            min = 3, max = 3, message = "Fund codes must have length 3"
        ) String> codes,
        @ModelAttribute @Valid SortParameters sortParameters
    )
        throws SortByValidationException {
        return service.getStats(codes, sortParameters);
    }

    @GetMapping("priceStats")
    @Transactional
    List<FundPriceStats> getPriceStats(
        @Valid Set<@Size(
            min = 3, max = 3, message = "Fund codes must have length 3"
        ) String> codes,
        @ModelAttribute @Valid SortParameters sortParameters
    )
        throws SortByValidationException {
        return service.getPricesWithStats(codes, sortParameters);
    }

    @PostMapping("update")
    @Transactional
    void update(@ModelAttribute @Valid DateRange range) throws UpdateFundsException {
        service.updateFunds(range);
    }
}
