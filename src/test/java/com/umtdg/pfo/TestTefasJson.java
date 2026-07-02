package com.umtdg.pfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.ObjectContent;

import com.umtdg.pfo.tefas.TefasFund;
import com.umtdg.pfo.tefas.TefasFundListResponse;
import com.umtdg.pfo.tefas.TefasFundReturns;
import com.umtdg.pfo.tefas.TefasFundReturnsResponse;

@JsonTest
class TestTefasJson {
    @Autowired
    JacksonTester<TefasFund> tefasFundJson;

    @Autowired
    JacksonTester<TefasFundListResponse> tefasJson;

    @Autowired
    JacksonTester<TefasFundReturnsResponse> tefasReturnsJson;

    @Test
    void givenSingleDataTefasJsonResponse_thenDecodeTefasFund() throws Exception {
        String tefasFetchJsonResponse = """
                 {
                    "fonKodu": "AAL",
                    "fonUnvan": "ATA PORTFÖY PARA PİYASASI (TL) FONU",
                    "tarih": "2026-07-01",
                    "fiyat": 3.355445,
                    "tedPaySayisi": 753633122,
                    "kisiSayisi": 4537,
                    "portfoyBuyukluk": 2528774708.26,
                    "borsaBultenFiyat": null,
                    "rn": 1
                }
            """;

        TefasFund fund = new TefasFund();
        fund.setCode("AAL");
        fund.setDate(LocalDate.of(2026, 7, 1));
        fund.setPrice(3.355445);
        fund.setNumShares(753633122L);
        fund.setNumInvestors(4537L);
        fund.setMarketCap(2528774708.26);

        assertNotNull(tefasFundJson);
        assertNotNull(tefasFetchJsonResponse);
        ObjectContent<TefasFund> jsonObject = tefasFundJson.parse(tefasFetchJsonResponse);
        TefasFund parsed = jsonObject.getObject();

        assertEquals(fund.getCode(), parsed.getCode());
        assertEquals(fund.getDate(), parsed.getDate());
        assertEquals(fund.getPrice(), parsed.getPrice());
        assertEquals(fund.getNumShares(), parsed.getNumShares());
        assertEquals(fund.getNumInvestors(), parsed.getNumInvestors());
        assertEquals(fund.getMarketCap(), parsed.getMarketCap());
    }

    @Test
    void givenTefasJsonResponse_thenDecodeTefasFetchResponse() throws Exception {
        // Using the actual JSON structure from your server response
        String tefasJsonResponse = """
            {
                "errorCode": null,
                "errorMessage": null,
                "toplamSayi": 46404,
                "toplamSayfa": 465,
                "resultList": [
                    {
                        "fonKodu": "AAV",
                        "fonUnvan": "ATA PORTFÖY İKİNCİ HİSSE SENEDİ (TL) FONU (HİSSE SENEDİ YOĞUN FON)",
                        "tarih": "2026-07-01",
                        "fiyat": 60.387413,
                        "tedPaySayisi": 3921349,
                        "kisiSayisi": 2124,
                        "portfoyBuyukluk": 236800119.74,
                        "borsaBultenFiyat": null,
                        "rn": 3
                    },
                    {
                        "fonKodu": "AAL",
                        "fonUnvan": "ATA PORTFÖY PARA PİYASASI (TL) FONU",
                        "tarih": "2026-07-01",
                        "fiyat": 3.355445,
                        "tedPaySayisi": 753633122,
                        "kisiSayisi": 4537,
                        "portfoyBuyukluk": 2528774708.26,
                        "borsaBultenFiyat": null,
                        "rn": 1
                    }
                ]
            }
            """;

        List<TefasFund> tefasResponseData = new ArrayList<>(2);
        {
            TefasFund fund = new TefasFund();
            fund.setCode("AAV");
            fund.setDate(LocalDate.of(2026, 7, 1));
            fund.setPrice(60.387413);
            fund.setTitle("ATA PORTFÖY İKİNCİ HİSSE SENEDİ (TL) FONU (HİSSE SENEDİ YOĞUN FON)");
            fund.setMarketCap(236800119.74);
            fund.setNumShares(3921349L);
            fund.setNumInvestors(2124L);

            tefasResponseData.add(fund);
        }

        {
            TefasFund fund = new TefasFund();
            fund.setCode("AAL");
            fund.setDate(LocalDate.of(2026, 7, 1));
            fund.setPrice(3.355445);
            fund.setTitle("ATA PORTFÖY PARA PİYASASI (TL) FONU");
            fund.setMarketCap(2528774708.26);
            fund.setNumShares(753633122L);
            fund.setNumInvestors(4537L);

            tefasResponseData.add(fund);
        }

        TefasFundListResponse parsed = tefasJson.parse(tefasJsonResponse).getObject();

        assertNull(parsed.getErrorCode());
        assertNull(parsed.getErrorMessage());

        assertEquals(46404, parsed.getTotalCount());
        assertEquals(465, parsed.getPageCount());

        List<TefasFund> funds = parsed.getResultList();
        assertEquals(tefasResponseData.size(), funds.size());
        for (int i = 0; i < funds.size(); i++) {
            TefasFund expected = tefasResponseData.get(i);
            TefasFund actual = funds.get(i);

            assertEquals(expected.getCode(), actual.getCode());
            assertEquals(expected.getDate(), actual.getDate());
            assertEquals(expected.getPrice(), actual.getPrice());
            assertEquals(expected.getTitle(), actual.getTitle());
            assertEquals(expected.getMarketCap(), actual.getMarketCap());
            assertEquals(expected.getNumShares(), actual.getNumShares());
            assertEquals(expected.getNumInvestors(), actual.getNumInvestors());
        }
    }

    @Test
    void givenTefasReturnsJsonResponse_thenDecodeReturnsResponse() throws Exception {
        String tefasReturnsJsonResponse = """
            {
                "errorCode": null,
                "errorMessage": null,
                "resultList": [
                    {
                        "fonKodu": "AU1",
                        "fonUnvan": "A1 CAPİTAL PORTFÖY ALTIN FONU",
                        "fonTurAciklama": "Kıymetli Madenler Şemsiye Fonu",
                        "tefasDurum": true,
                        "getiri1a": -9.4002,
                        "getiri3a": -8.0996,
                        "getiri6a": -0.3011,
                        "getiri1y": null,
                        "getiriyb": null,
                        "getiri3y": null,
                        "getiri5y": null,
                        "getiriOrani": null,
                        "riskDegeri": "6"
                    },
                    {
                        "fonKodu": "AJK",
                        "fonUnvan": "AK PORTFÖY 0-5 YIL VADELİ SERBEST (DÖVİZ) FON",
                        "fonTurAciklama": "Serbest Şemsiye Fonu",
                        "tefasDurum": true,
                        "getiri1a": 2.451,
                        "getiri3a": 6.099,
                        "getiri6a": 10.5705,
                        "getiri1y": 21.8585,
                        "getiriyb": null,
                        "getiri3y": 88.9042,
                        "getiri5y": 489.1516,
                        "getiriOrani": null,
                        "riskDegeri": null
                    }
                ]
            }
            """;

        TefasFundReturnsResponse parsed = tefasReturnsJson
            .parse(tefasReturnsJsonResponse)
            .getObject();

        assertNull(parsed.getErrorCode());
        assertNull(parsed.getErrorMessage());

        List<TefasFundReturns> returns = parsed.getResultList();
        assertEquals(2, returns.size());

        TefasFundReturns first = returns.get(0);
        assertEquals("AU1", first.getCode());
        assertEquals("A1 CAPİTAL PORTFÖY ALTIN FONU", first.getTitle());
        assertEquals(-9.4002, first.getReturn1m());
        assertEquals(-0.3011, first.getReturn6m());
        assertNull(first.getReturn1y());
        assertEquals("6", first.getRiskValue());

        TefasFundReturns second = returns.get(1);
        assertEquals("AJK", second.getCode());
        assertEquals(21.8585, second.getReturn1y());
        assertEquals(489.1516, second.getReturn5y());
        assertNull(second.getRiskValue());
    }
}
