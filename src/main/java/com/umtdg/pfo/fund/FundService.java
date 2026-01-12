package com.umtdg.pfo.fund;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.umtdg.pfo.DateRange;
import com.umtdg.pfo.DateUtils;
import com.umtdg.pfo.SortParameters;
import com.umtdg.pfo.exception.SortByValidationException;
import com.umtdg.pfo.exception.UpdateFundsException;
import com.umtdg.pfo.fund.info.FundInfo;
import com.umtdg.pfo.fund.info.FundInfoRepository;
import com.umtdg.pfo.fund.price.FundPrice;
import com.umtdg.pfo.fund.price.FundPriceRepository;
import com.umtdg.pfo.fund.price.FundPriceStats;
import com.umtdg.pfo.fund.price.FundPriceStatsRepository;
import com.umtdg.pfo.fund.stats.FundStats;
import com.umtdg.pfo.fund.stats.FundStatsRepository;
import com.umtdg.pfo.tefas.TefasClient;
import com.umtdg.pfo.tefas.TefasFund;

@Service
public class FundService {
    private final Logger logger = LoggerFactory.getLogger(FundService.class);

    private FundBatchRepository fundBatchRepository;
    private FundInfoRepository infoRepository;
    private FundPriceRepository priceRepository;
    private FundStatsRepository statsRepository;
    private FundPriceStatsRepository priceStatsRepository;

    private TefasClient tefasClient;

    public FundService(
        FundBatchRepository fundBatchRepository, FundInfoRepository infoRepository,
        FundPriceRepository priceRepository, FundStatsRepository statsRepository,
        FundPriceStatsRepository priceStatsRepository,
        TefasClient tefasClient
    ) {
        this.fundBatchRepository = fundBatchRepository;
        this.infoRepository = infoRepository;
        this.priceRepository = priceRepository;
        this.statsRepository = statsRepository;
        this.priceStatsRepository = priceStatsRepository;

        this.tefasClient = tefasClient;
    }

    public List<FundInfo> getInfos(
        FundFilter filter, SortParameters sortParameters
    )
        throws SortByValidationException {
        Set<String> codes = filter.getCodes();
        LocalDate date = filter.getDate();

        Sort sort = null;
        if (sortParameters != null) {
            sort = sortParameters
                .validate(FundStats.ALLOWED_SORT_PROPERTIES, "code");
        }

        logger
            .debug(
                "[CODES:{}][DATE:{}][SORT:{}] Get fund informations",
                codes,
                date,
                sort
            );

        boolean hasCodes = codes != null && !codes.isEmpty();
        boolean hasDate = date != null;

        if (hasCodes && hasDate) {
            return infoRepository.findAllByDateAndCodeIn(date, codes, sort);
        } else if (hasCodes) {
            return infoRepository.findAllLatestByCodeIn(codes, sort);
        } else if (hasDate) {
            return infoRepository.findAllByDate(date, sort);
        } else {
            return infoRepository.findAllLatest(sort);
        }
    }

    public List<FundPriceStats> getPricesWithStats(
        Set<String> codes, SortParameters sortParameters
    )
        throws SortByValidationException {
        Sort sort = null;
        if (sortParameters != null) {
            sort = sortParameters
                .validate(FundPriceStats.ALLOWED_SORT_PROPERTIES, "fiveYearlyReturn");
        }

        logger
            .debug(
                "[CODES:{}][SORT:{}] Get fund prices with stats",
                codes,
                sort
            );

        if (codes != null && !codes.isEmpty()) {
            return priceStatsRepository.findAllById(codes);
        } else {
            return priceStatsRepository.findAll();
        }
    }

    public void updateFunds(DateRange range) throws UpdateFundsException {
        LocalDate start = range.getStart();
        if (start == null) {
            start = priceRepository.findTopDate();
        }

        LocalDate end = range.getEnd();
        if (end == null) {
            end = DateUtils.prevBDay();
        }

        // If we don't have any data in the DB
        if (start == null) {
            start = end;
        }

        if (start.isAfter(end)) {
            throw new UpdateFundsException("Start date cannot be after end date");
        }

        range.setStart(start);
        range.setEnd(end);

        updateFundInfos(range);
        Map<String, Object> updateResult = statsRepository.updateFundStats();
        logger
            .info(
                "[CODES:{}][TIME:{} ms] Updated fund statistics",
                updateResult.get("funds_updated"),
                updateResult.get("execution_time_ms")
            );
    }

    private void updateFundInfos(DateRange range) {
        logger.info("[{}] Updating Tefas funds", range);
        batchInsertTefasFunds(tefasClient.fetchDateRange(range), 2000);
    }

    private void batchInsertTefasFunds(List<TefasFund> tefasFunds, int batchSize) {
        List<Fund> fundBatch = new ArrayList<>(batchSize);
        List<FundPrice> priceBatch = new ArrayList<>(batchSize);
        for (TefasFund tefasFund : tefasFunds) {
            fundBatch.add(tefasFund.toFund());
            priceBatch.add(tefasFund.toFundPrice());

            if (fundBatch.size() >= batchSize) {
                insertBatches(fundBatch, priceBatch);
            }
        }

        if (!fundBatch.isEmpty() || !priceBatch.isEmpty()) {
            insertBatches(fundBatch, priceBatch);
        }
    }

    private void insertBatches(
        List<Fund> fundBatch, List<FundPrice> priceBatch
    ) {
        logger
            .trace(
                "[FUND:{}][PRICE:{}] Save Fund and FundPrice batches",
                fundBatch.size(),
                priceBatch.size()
            );
        fundBatchRepository.batchInsertFunds(fundBatch);
        fundBatchRepository.batchInsertFundPrices(priceBatch);

        fundBatch.clear();
        priceBatch.clear();
    }
}
