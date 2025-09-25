package com.umtdg.pfo.fund;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.umtdg.pfo.DateUtils;
import com.umtdg.pfo.tefas.TefasClient;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/f")
public class FundController {
    private final FundRepository repository;
    private final FundPriceRepository priceRepository;
    private final FundBatchRepository fundBatchRepository;
    private final FundStatsRepository statsRepository;

    private final Logger logger = LoggerFactory.getLogger(FundController.class);

    public FundController(
        FundRepository repository, FundPriceRepository priceRepository,
        FundBatchRepository fundBatchRepository, FundStatsRepository statsRepository
    ) {
        this.repository = repository;
        this.priceRepository = priceRepository;
        this.fundBatchRepository = fundBatchRepository;
        this.statsRepository = statsRepository;
    }

    @GetMapping
    @Transactional
    public ResponseEntity<List<FundInformation>> get(@Valid FundFilter filter) {
        filter = DateUtils.checkFundDateFilters(filter, priceRepository);
        LocalDate date = filter.getDate();
        LocalDate fetchFrom = filter.getFetchFrom();

        logger.info("Getting fund information for {}", filter);

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
                repository.findInformationOfAll(date), HttpStatus.OK
            );
        } else {
            return new ResponseEntity<>(
                repository.findInformationByCodes(codes, date), HttpStatus.OK
            );
        }
    }

    @GetMapping("stats")
    @Transactional
    ResponseEntity<List<FundStats>> getStats(
        @RequestParam(required = false) List<String> codes
    ) {
        LocalDate fundLastUpdated = priceRepository.findLatestDate();
        if (fundLastUpdated == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        LocalDate statsLastUpdated = statsRepository.findLatestUpdateDate();
        if (statsLastUpdated == null || statsLastUpdated.isBefore(fundLastUpdated)) {
            logger.debug("Should update stats here");

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
            LocalDate updatedAt = LocalDate.now();

            for (Fund fund : allFunds) {
                final String code = fund.getCode();

                FundPrice historicalPrices[] = new FundPrice[numberOfHistoricalPoints];
                for (int i = 0; i < numberOfHistoricalPoints; i++) {
                    historicalPrices[i] = priceRepository
                        .findById(new FundPriceId(code, historicalPoints[i]))
                        .orElse(null);
                }

                Float historicalReturns[] = new Float[numberOfHistoricalPoints];
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
                stats.setUpdatedAt(updatedAt);

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
                statsRepository.findAll(), HttpStatus.OK
            );
        }

        return new ResponseEntity<>(
            statsRepository.findByCodeIn(codes), HttpStatus.OK
        );
    }

    private Float calculateReturn(FundPrice currentPrice, FundPrice historicalPrice) {
        if (currentPrice == null || historicalPrice == null) {
            return null;
        }

        float currentValue = currentPrice.getTotalValue();
        float historicalValue = historicalPrice.getTotalValue();
        if (currentValue == 0.0f || historicalValue == 0.0f) {
            return null;
        }

        return 100 * ((currentValue - historicalValue) / historicalValue);
    }
}
