package com.umtdg.pfo.fund;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.umtdg.pfo.DateUtils;
import com.umtdg.pfo.tefas.TefasClient;

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
}
