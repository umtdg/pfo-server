package com.umtdg.pfo.fund;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.umtdg.pfo.DateRange;
import com.umtdg.pfo.DateUtils;
import com.umtdg.pfo.tefas.TefasClient;
import com.umtdg.pfo.tefas.TefasFetchParams;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/f")
public class FundController {
    private final FundRepository repository;
    private final FundPriceRepository priceRepository;
    private final FundBatchRepository fundBatchRepository;

    private final Logger logger = LoggerFactory.getLogger(FundController.class);

    public FundController(
        FundRepository repository, FundPriceRepository priceRepository,
        FundBatchRepository fundBatchRepository
    ) {
        this.repository = repository;
        this.priceRepository = priceRepository;
        this.fundBatchRepository = fundBatchRepository;
    }

    @GetMapping
    public ResponseEntity<List<FundInformation>> get(@Valid FundFilter filter) {
        LocalDate date = filter.getDate();
        List<String> codes = filter.getCodes();

        if (date == null || date.isAfter(LocalDate.now())) {
            date = DateUtils.prevBDay();
        }
        logger.debug("Get funds price information at {}: {}\n", date, codes);

        LocalDate fetchFrom = filter.getFetchFrom();
        if (fetchFrom == null) {
            logger.trace("Find last fund price update date from DB");
            LocalDate lastUpdated = priceRepository.findLatestDate();

            if (lastUpdated == null) {
                lastUpdated = date.minusDays(1);

                logger
                    .warn(
                        "No fund price data is found, using {} as last update date",
                        lastUpdated
                    );
            }

            fetchFrom = lastUpdated;
        }

        if (fetchFrom.isBefore(date)) {
            logger.debug("Updating out of date fund price information");

            TefasClient tefasClient = null;

            try {
                tefasClient = new TefasClient();
            } catch (Exception exc) {
                logger
                    .error(
                        "Error while fetching fund prices from tefas ({} - {}): {}",
                        fetchFrom,
                        date,
                        exc.getMessage()
                    );

                return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
            }

            fetchFrom = fetchFrom.plusDays(1);
            List<DateRange> ranges = DateUtils
                .splitDateRange(new DateRange(fetchFrom, date));

            for (DateRange range : ranges) {
                logger.trace("Fething fund prices from tefas between {}", range);
                TefasFetchParams fetchParams = new TefasFetchParams(
                    range.getStart(), range.getEnd()
                );

                final int batchSize = 2000;
                List<Fund> fundBatch = new ArrayList<>(batchSize);
                List<FundPrice> priceBatch = new ArrayList<>(batchSize);

                tefasClient.fetchStreaming(fetchParams, fund -> {
                    fundBatch.add(fund.toFund());
                    priceBatch.add(fund.toFundPrice());

                    if (fundBatch.size() > batchSize) {
                        logger.trace("Save Fund batch of {}", fundBatch.size());
                        fundBatchRepository.batchInsertFunds(fundBatch);

                        logger.trace("Save FundPrice batch of {}", priceBatch.size());
                        fundBatchRepository.batchInsertFundPrices(priceBatch);

                        fundBatch.clear();
                        priceBatch.clear();
                    }
                });

                if (!fundBatch.isEmpty()) {
                    logger.trace("Save remaining Fund batch of {}", fundBatch.size());
                    fundBatchRepository.batchInsertFunds(fundBatch);
                }

                if (!priceBatch.isEmpty()) {
                    logger
                        .trace(
                            "Save remaining FundPrice batch of {}",
                            priceBatch.size()
                        );
                    fundBatchRepository.batchInsertFundPrices(priceBatch);
                }

                fundBatch.clear();
                priceBatch.clear();
            }
        }

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
}
