package com.umtdg.pfo.fund;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import com.umtdg.pfo.DateUtils;
import com.umtdg.pfo.SortParameters;
import com.umtdg.pfo.fund.info.FundInfo;
import com.umtdg.pfo.fund.info.FundInfoRepository;
import com.umtdg.pfo.fund.price.FundPrice;
import com.umtdg.pfo.fund.price.FundPriceId;
import com.umtdg.pfo.fund.price.FundPriceRepository;
import com.umtdg.pfo.fund.stats.FundStats;
import com.umtdg.pfo.fund.stats.FundStatsRepository;
import com.umtdg.pfo.tefas.TefasClient;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/f")
public class FundController {
    private final FundRepository repository;
    private final FundPriceRepository priceRepository;
    private final FundBatchRepository fundBatchRepository;
    private final FundStatsRepository statsRepository;
    private final FundInfoRepository infoRepository;

    private final Logger logger = LoggerFactory.getLogger(FundController.class);

    public FundController(
        FundRepository repository, FundPriceRepository priceRepository,
        FundBatchRepository fundBatchRepository, FundStatsRepository statsRepository,
        FundInfoRepository infoRepository
    ) {
        this.repository = repository;
        this.priceRepository = priceRepository;
        this.fundBatchRepository = fundBatchRepository;
        this.statsRepository = statsRepository;
        this.infoRepository = infoRepository;
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
    public ResponseEntity<List<FundInfo>> get(
        FundFilter filter, SortParameters sortParameters
    ) {
        Sort sort = sortParameters != null
            ? sortParameters.validate(ALLOWED_FUND_INFO_SORT_PROPERTIES)
            : Sort.by(Sort.Direction.ASC, "code");

        filter = DateUtils.checkFundDateFilters(filter, priceRepository);
        LocalDate date = filter.getDate();
        LocalDate fetchFrom = filter.getFetchFrom();

        if (fetchFrom.isBefore(date)) {
            try {
                TefasClient tefasClient = new TefasClient();

                fetchFrom = fetchFrom.plusDays(1);
                tefasClient.fetchDateRange(fundBatchRepository, fetchFrom, date);
            } catch (KeyManagementException keyMgmtExc) {
                logger
                    .error(
                        "Error while creating Tefas client: KeyManagementException: {}",
                        keyMgmtExc.getMessage()
                    );

                return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
            } catch (KeyStoreException keyStoreExc) {
                logger
                    .error(
                        "Error while creating Tefas client: KeyStoreException: {}",
                        keyStoreExc.getMessage()
                    );

                return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
            } catch (NoSuchAlgorithmException noAlgoExc) {
                logger
                    .error(
                        "Error while creating Tefas client: NoSuchAlgorithmException: {}",
                        noAlgoExc.getMessage()
                    );

                return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
            } catch (IllegalArgumentException illegalArgExc) {
                logger
                    .error("Error while fetching Fund information: {}", illegalArgExc);

                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        List<String> codes = filter.getCodes();
        if (codes == null || codes.isEmpty()) {
            return new ResponseEntity<>(
                infoRepository.findAllByDate(date, sort), HttpStatus.OK
            );
        }

        return new ResponseEntity<>(
            infoRepository.findAllByDateAndCodeIn(date, codes, sort),
            HttpStatus.OK
        );
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
        Sort sort = sortParameters != null
            ? sortParameters.validate(ALLOWED_FUND_STAT_SORT_PROPERTIES)
            : Sort.by(Sort.Direction.ASC, "code");

        LocalDate fundLastUpdated = priceRepository.findLatestDate();
        if (fundLastUpdated == null) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Couldn't find last updated fund date");
        }

        LocalDate statsLastUpdated = statsRepository.findLatestUpdateDate();
        boolean shouldUpdate = statsLastUpdated == null
            || statsLastUpdated.isBefore(fundLastUpdated);
        if (force || shouldUpdate) {
            final int numberOfHistoricalPoints = 8;
            final long historicalPointDistances[] = {
                0, 1,
                1, 3, 6,
                1, 3, 5
            };
            final ChronoUnit historicalPointUnits[] = {
                ChronoUnit.DAYS, ChronoUnit.DAYS,
                ChronoUnit.MONTHS, ChronoUnit.MONTHS, ChronoUnit.MONTHS,
                ChronoUnit.YEARS, ChronoUnit.YEARS, ChronoUnit.YEARS
            };

            LocalDate historicalPoints[] = new LocalDate[numberOfHistoricalPoints];
            for (int i = 0; i < numberOfHistoricalPoints; i++) {
                historicalPoints[i] = DateUtils
                    .prevBDay(
                        fundLastUpdated
                            .minus(
                                historicalPointDistances[i],
                                historicalPointUnits[i]
                            )
                    );
            }

            logger.debug("Updating stats of all funds");
            List<Fund> allFunds = repository.findAll();
            List<FundStats> updatedStats = new ArrayList<>(allFunds.size());

            for (Fund fund : allFunds) {
                final String code = fund.getCode();

                FundPrice historicalPrices[] = new FundPrice[numberOfHistoricalPoints];
                for (int i = 0; i < numberOfHistoricalPoints; i++) {
                    historicalPrices[i] = priceRepository
                        .findById(new FundPriceId(code, historicalPoints[i]))
                        .orElse(null);
                }

                float historicalReturns[] = new float[numberOfHistoricalPoints];
                historicalReturns[0] = 0.0f;
                for (int i = 1; i < numberOfHistoricalPoints; i++) {
                    historicalReturns[i] = calculateReturn(
                        historicalPrices[0],
                        historicalPrices[i]
                    );
                }

                FundStats stats = new FundStats();
                stats.setCode(code);
                stats.setTitle(fund.getTitle());
                stats
                    .setLastPrice(
                        historicalPrices[0] == null
                            ? 0.0f
                            : historicalPrices[0].getPrice()
                    );
                stats
                    .setTotalValue(
                        historicalPrices[0] == null
                            ? 0.0f
                            : historicalPrices[0].getTotalValue()
                    );

                stats.setDailyReturn(historicalReturns[1]);
                stats.setMonthlyReturn(historicalReturns[2]);
                stats.setThreeMonthlyReturn(historicalReturns[3]);
                stats.setSixMonthlyReturn(historicalReturns[4]);
                stats.setYearlyReturn(historicalReturns[5]);
                stats.setThreeYearlyReturn(historicalReturns[6]);
                stats.setFiveYearlyReturn(historicalReturns[7]);

                updatedStats.add(stats);
            }

            statsRepository.saveAll(updatedStats);
        }

        if (codes == null || codes.isEmpty()) {
            return new ResponseEntity<>(
                statsRepository.findAll(sort),
                HttpStatus.OK
            );
        }

        return new ResponseEntity<>(
            statsRepository.findByCodeIn(codes, sort), HttpStatus.OK
        );
    }

    private float calculateReturn(FundPrice currentPrice, FundPrice historicalPrice) {
        if (currentPrice == null || historicalPrice == null) {
            return 0.0f;
        }

        float currentValue = currentPrice.getTotalValue();
        float historicalValue = historicalPrice.getTotalValue();
        if (currentValue == 0.0f || historicalValue == 0.0f) {
            return 0.0f;
        }

        return 100 * ((currentValue - historicalValue) / historicalValue);
    }
}
