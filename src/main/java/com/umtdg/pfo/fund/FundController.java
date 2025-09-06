package com.umtdg.pfo.fund;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    public ResponseEntity<List<FundInformation>> get(
        @Valid FundFilter filter,
        @RequestParam(required = false) Boolean force
    ) {
        LocalDate date = filter.getDate();
        List<String> codes = filter.getCodes();

        if (force == null) {
            force = false;
        }

        if (date == null) {
            LocalDateTime now = LocalDateTime.now();
            if (now.getHour() < 18) {
                now = now.minusDays(1);
            }

            DayOfWeek dayOfWeek = now.getDayOfWeek();
            if (dayOfWeek == DayOfWeek.SATURDAY) {
                now = now.minusDays(1);
            } else if (dayOfWeek == DayOfWeek.SUNDAY) {
                now = now.minusDays(2);
            }

            date = now.toLocalDate();
        }
        logger.debug("Funds at {} (force = {})\n{}", date, force, codes);

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

        if (force || lastUpdated.isBefore(date)) {
            logger.debug("Updating out of date fund price information");

            TefasFetchParams fetchParams = new TefasFetchParams(
                lastUpdated.plusDays(1), date
            );
            TefasClient tefasClient = null;

            try {
                tefasClient = new TefasClient();
            } catch (Exception exc) {
                logger
                    .error(
                        "Error while fetching fund prices from tefas ({} - {}): {}",
                        fetchParams.getStart(),
                        fetchParams.getEnd(),
                        exc.getMessage()
                    );

                return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
            }

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
                logger.trace("Save remaining FundPrice batch of {}", priceBatch.size());
                fundBatchRepository.batchInsertFundPrices(priceBatch);
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
