package com.umtdg.pfo.fund;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.umtdg.pfo.DateUtils;
import com.umtdg.pfo.fund.info.FundInfo;
import com.umtdg.pfo.fund.info.FundInfoRepository;
import com.umtdg.pfo.fund.price.FundPriceRepository;
import com.umtdg.pfo.fund.stats.FundStats;
import com.umtdg.pfo.fund.stats.FundStatsRepository;
import com.umtdg.pfo.tefas.TefasClient;

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

    public FundService(
        FundBatchRepository fundBatchRepository, FundInfoRepository infoRepository,
        FundPriceRepository priceRepository, FundStatsRepository statsRepository
    ) {
        this.fundBatchRepository = fundBatchRepository;
        this.infoRepository = infoRepository;
        this.priceRepository = priceRepository;
        this.statsRepository = statsRepository;
    }

    public Optional<ResponseEntity<?>> updateTefasFunds(FundFilter filter) {
        LocalDate fetchFrom = filter.getFetchFrom();
        LocalDate date = filter.getDate();

        if (!fetchFrom.isBefore(date)) return Optional.empty();

        logger
            .debug(
                "[FROM:{}][DATE:{}]Updating Tefas funds for funds between",
                fetchFrom,
                date
            );

        try {
            TefasClient client = new TefasClient();

            fetchFrom = fetchFrom.plusDays(1);
            client.fetchDateRange(fundBatchRepository, fetchFrom, date);
        } catch (
            KeyManagementException | KeyStoreException | NoSuchAlgorithmException exc
        ) {
            String msg = String.format("Error while creating Tefas client: {}", exc);
            logger.error(msg);
            return Optional.of(ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(msg));
        }

        return Optional.empty();
    }

    public ResponseEntity<List<FundInfo>> getFundInfos(FundFilter filter, Sort sort) {
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
            return ResponseEntity
                .status(HttpStatus.OK)
                .body(infoRepository.findAllByDateAndCodeIn(date, codes, sort));
        }

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(infoRepository.findAllByDate(date, sort));
    }

    public Optional<ResponseEntity<?>> updateFundStats(boolean force) {
        LocalDate fundLastUpdated = priceRepository.findTopDate();
        if (fundLastUpdated == null) {
            String msg = "Couldn't find last updated fund date";
            logger.error(msg);
            return Optional
                .of(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg));
        }

        LocalDate statsLastUpdated = statsRepository.findTopUpdatedAt();
        boolean shouldUpdate = statsLastUpdated == null
            || statsLastUpdated.isBefore(fundLastUpdated);

        if (!force && !shouldUpdate) return Optional.empty();

        updateFundStatsUnchecked(fundLastUpdated);

        return Optional.empty();
    }

    public ResponseEntity<List<FundStats>> getFundStats(List<String> codes, Sort sort) {
        logger.debug("[CODES:{}][SORT:{}] Get fund stats", codes, sort);

        if (codes != null && !codes.isEmpty()) {
            return ResponseEntity
                .status(HttpStatus.OK)
                .body(statsRepository.findByCodeIn(codes, sort));
        }

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(statsRepository.findAll(sort));
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
        Map<LocalDate, Integer> historicalPoints = new HashMap<>(
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
}
