package com.umtdg.pfo.fund;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.umtdg.pfo.DateRange;
import com.umtdg.pfo.DateUtils;
import com.umtdg.pfo.SortParameters;
import com.umtdg.pfo.exception.SortByValidationException;
import com.umtdg.pfo.exception.UpdateFundStatsException;
import com.umtdg.pfo.fund.info.FundInfo;
import com.umtdg.pfo.fund.info.FundInfoRepository;
import com.umtdg.pfo.fund.price.FundPrice;
import com.umtdg.pfo.fund.price.FundPriceRepository;
import com.umtdg.pfo.fund.stats.FundStats;
import com.umtdg.pfo.fund.stats.FundStatsRepository;
import com.umtdg.pfo.tefas.TefasClient;
import com.umtdg.pfo.tefas.TefasFund;

@Service
public class FundService {
    protected static final List<Period> HISTORICAL_PERIODS = Arrays
        .asList(
            Period.ofDays(0),
            Period.ofDays(1),
            Period.ofMonths(1),
            Period.ofMonths(3),
            Period.ofMonths(6),
            Period.ofYears(1),
            Period.ofYears(3),
            Period.ofYears(5)
        );

    private final Logger logger = LoggerFactory.getLogger(FundService.class);

    private FundBatchRepository fundBatchRepository;
    private FundInfoRepository infoRepository;
    private FundPriceRepository priceRepository;
    private FundStatsRepository statsRepository;

    private TefasClient tefasClient;

    public FundService(
        FundBatchRepository fundBatchRepository, FundInfoRepository infoRepository,
        FundPriceRepository priceRepository, FundStatsRepository statsRepository,
        TefasClient tefasClient
    ) {
        this.fundBatchRepository = fundBatchRepository;
        this.infoRepository = infoRepository;
        this.priceRepository = priceRepository;
        this.statsRepository = statsRepository;

        this.tefasClient = tefasClient;
    }

    public void updateTefasFunds(FundFilter filter) {
        filter = validateFundFilter(filter);

        updateTefasFundsUnchecked(filter);
    }

    public void batchInsertTefasFunds(List<TefasFund> tefasFunds, int batchSize) {
        List<Fund> fundBatch = new ArrayList<>(batchSize);
        List<FundPrice> priceBatch = new ArrayList<>(batchSize);
        for (TefasFund tefasFund : tefasFunds) {
            fundBatch.add(tefasFund.toFund());
            priceBatch.add(tefasFund.toFundPrice());

            if (fundBatch.size() >= batchSize) {
                batchInsertFundsAndPrices(fundBatch, priceBatch);
            }
        }

        if (!fundBatch.isEmpty() || !priceBatch.isEmpty()) {
            batchInsertFundsAndPrices(fundBatch, priceBatch);
        }
    }

    public List<FundInfo> getFundInfos(FundFilter filter, Sort sort) {
        List<String> codes = filter.getCodes();
        LocalDate date = filter.getDate();

        logger
            .debug(
                "[CODES:{}][DATE:{}][SORT:{}] Get fund informations",
                codes,
                date,
                sort
            );

        if (codes != null && !codes.isEmpty()) {
            return infoRepository.findAllByDateAndCodeIn(date, codes, sort);
        }

        return infoRepository.findAllByDate(date, sort);
    }

    public void updateFundStats(boolean force) throws UpdateFundStatsException {
        LocalDate fundLastUpdated = priceRepository.findTopDate();
        if (fundLastUpdated == null) {
            throw new UpdateFundStatsException("Couldn't find last updated fund date");
        }

        LocalDate statsLastUpdated = statsRepository.findTopUpdatedAt();
        boolean shouldUpdate = statsLastUpdated == null
            || statsLastUpdated.isBefore(fundLastUpdated);

        if (!force && !shouldUpdate) return;

        updateFundStatsUnchecked(fundLastUpdated);
    }

    public List<FundStats> getFundStats(List<String> codes, Sort sort) {
        logger.debug("[CODES:{}][SORT:{}] Get fund stats", codes, sort);

        if (codes != null && !codes.isEmpty()) {
            return statsRepository.findByCodeIn(codes, sort);
        }

        return statsRepository.findAll(sort);
    }

    public List<FundInfo> updateAndGetFundInfos(
        FundFilter filter, SortParameters sortParameters
    )
        throws SortByValidationException {
        List<String> sortBy = sortParameters.getSortBy();
        if (sortParameters.getSortBy().isEmpty()) {
            sortBy.add("code");
        }

        Sort sort = sortParameters
            .validate(FundController.ALLOWED_FUND_INFO_SORT_PROPERTIES);

        filter = validateFundFilter(filter);
        updateTefasFunds(filter);

        return getFundInfos(filter, sort);
    }

    public List<FundStats> updateAndGetFundStats(
        List<String> codes, SortParameters sortParameters, boolean force
    )
        throws SortByValidationException,
            UpdateFundStatsException {
        List<String> sortBy = sortParameters.getSortBy();
        if (sortParameters.getSortBy().isEmpty()) {
            sortBy.add("fiveYearlyReturn");
        }

        Sort sort = sortParameters
            .validate(FundController.ALLOWED_FUND_STAT_SORT_PROPERTIES);

        updateFundStats(force);

        return getFundStats(codes, sort);
    }

    public FundFilter validateFundFilter(FundFilter filter) {
        if (filter == null) {
            filter = new FundFilter();
        }

        LocalDate date = filter.getDate();
        if (date == null || date.isAfter(LocalDate.now())) {
            date = DateUtils.prevBDay();
        }

        LocalDate fetchFrom = filter.getFetchFrom();
        if (fetchFrom == null) {
            fetchFrom = priceRepository.findTopDate();
            if (fetchFrom == null) {
                fetchFrom = date.minusDays(1);
            }
        }

        filter.setDate(date);
        filter.setFetchFrom(fetchFrom);
        return filter;
    }

    private void updateTefasFundsUnchecked(FundFilter filter) {
        LocalDate fetchFrom = filter.getFetchFrom();
        LocalDate date = filter.getDate();

        if (!fetchFrom.isBefore(date)) return;

        logger
            .debug(
                "[FROM:{}][DATE:{}]Updating Tefas funds for funds between",
                fetchFrom,
                date
            );

        fetchFrom = fetchFrom.plusDays(1);
        batchInsertTefasFunds(
            this.tefasClient
                .fetchDateRange(new DateRange(fetchFrom, date)),
            2000
        );
    }

    private void updateFundStatsUnchecked(LocalDate fundLastUpdated) {
        Map<LocalDate, Integer> historicalPoints = getHistoricalPoints(
            fundLastUpdated
        );

        List<FundInfo> fundInfos = infoRepository
            .findAllByDateInOrderByCodeAscDateDesc(historicalPoints.keySet());
        List<FundStats> statList = new ArrayList<>(fundInfos.size());

        String prevCode = "";
        FundStats stats = new FundStats();

        for (FundInfo fundInfo : fundInfos) {
            final String code = fundInfo.getCode();

            stats = checkPrevCode(
                fundInfo,
                code,
                prevCode,
                stats,
                statList,
                fundLastUpdated
            );
            stats
                .setReturnByIndex(
                    historicalPoints.get(fundInfo.getDate()),
                    calculateReturn(stats.getTotalValue(), fundInfo.getTotalValue())
                );

            prevCode = code;
        }

        statList.add(stats);

        statsRepository.saveAll(statList);
    }

    private Map<LocalDate, Integer> getHistoricalPoints(LocalDate fundLastUpdated) {
        Map<LocalDate, Integer> historicalPoints = HashMap
            .newHashMap(
                HISTORICAL_PERIODS.size()
            );

        for (int i = 0; i < HISTORICAL_PERIODS.size(); i++) {
            LocalDate date = DateUtils
                .prevBDay(fundLastUpdated.minus(HISTORICAL_PERIODS.get(i)));
            historicalPoints.put(date, i);
        }

        return historicalPoints;
    }

    private FundStats checkPrevCode(
        FundInfo fundInfo, String code, String prevCode, FundStats stats,
        List<FundStats> statList, LocalDate fundLastUpdated
    ) {
        if (code.equals(prevCode)) return stats;

        if (!prevCode.isEmpty()) {
            logger.trace("Adding {} to update list", prevCode);
            statList.add(stats);
        }

        FundStats newStats = new FundStats();
        newStats.setCode(code);
        newStats.setTitle(fundInfo.getTitle());
        newStats.setLastPrice(fundInfo.getPrice());
        newStats.setTotalValue(fundInfo.getTotalValue());
        newStats.setUpdatedAt(fundLastUpdated);

        return newStats;
    }

    private float calculateReturn(float current, float historical) {
        if (historical == 0.0f) return 0.0f;

        return 100 * (current - historical) / historical;
    }

    private void batchInsertFundsAndPrices(
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
