package com.umtdg.pfo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umtdg.pfo.tefas.TefasFund;
import com.umtdg.pfo.tefas.TefasFetchResponse;

class TefasFundTest {
    @Test
    void givenSingleDataTefasJsonResponse_thenDecodeTefasFund() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        String singleDataTefasJsonResponse = """
                 {
                    "TARIH": "1750982400000",
                    "FONKODU": "FUN",
                    "FONUNVAN": "Test Fund",
                    "FIYAT": 26.523359,
                    "TEDPAYSAYISI": 1328497.0,
                    "KISISAYISI": 778.0,
                    "PORTFOYBUYUKLUK": 35236202.48,
                    "BilFiyat": "-"
                }
            """;

        TefasFund fund = mapper.readValue(singleDataTefasJsonResponse, TefasFund.class);

        assertEquals("FUN", fund.getCode());
        assertEquals(LocalDate.of(2025, 06, 27), fund.getDate());
        assertEquals(26.523359f, fund.getPrice());
        assertEquals(1328497.0f, fund.getNumShares());
        assertEquals(778.0f, fund.getNumInvestors());
        assertEquals(35236202.48f, fund.getMarketCap());
    }

    @Test
    void givenTefasJsonResponse_thenDecodeTefasFetchResponse() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // Using the actual JSON structure from your server response
        String tefasJsonResponse = """
            {
                "draw": 0,
                "recordsTotal": 1902,
                "recordsFiltered": 1902,
                "data": [
                    {
                      "TARIH": "1759795200000",
                      "FONKODU": "AAK",
                      "FONUNVAN": "ATA ASSET MANAGEMENT MULTI-ASSET VARIABLE FUND",
                      "FIYAT": 29.908098,
                      "TEDPAYSAYISI": 1147420.0,
                      "KISISAYISI": 763.0,
                      "PORTFOYBUYUKLUK": 34317150.35,
                      "BilFiyat": "-"
                    },
                    {
                      "TARIH": "1759795200000",
                      "FONKODU": "AAL",
                      "FONUNVAN": "ATA ASSET MANAGEMENT MONEY MARKET (TL) FUND",
                      "FIYAT": 2.568242,
                      "TEDPAYSAYISI": 1033049528.0,
                      "KISISAYISI": 4842.0,
                      "PORTFOYBUYUKLUK": 2653120744.59,
                      "BilFiyat": "-"
                    }
                ]
            }
            """;

        List<TefasFund> tefasResponseData = new ArrayList<>(2);
        {
            TefasFund fund = new TefasFund();
            fund.setCode("AAK");
            fund.setDate(LocalDate.of(2025, 10, 7));
            fund.setPrice(29.908098f);
            fund.setTitle("ATA ASSET MANAGEMENT MULTI-ASSET VARIABLE FUND");
            fund.setMarketCap(34317150.35f);
            fund.setNumShares(1147420.0f);
            fund.setNumInvestors(763.0f);

            tefasResponseData.add(fund);
        }

        {
            TefasFund fund = new TefasFund();
            fund.setCode("AAL");
            fund.setDate(LocalDate.of(2025, 10, 7));
            fund.setPrice(2.568242f);
            fund.setTitle("ATA ASSET MANAGEMENT MONEY MARKET (TL) FUND");
            fund.setMarketCap(2653120744.59f);
            fund.setNumShares(1033049528.0f);
            fund.setNumInvestors(4842.0f);

            tefasResponseData.add(fund);
        }

        TefasFetchResponse response = mapper
            .readValue(tefasJsonResponse, TefasFetchResponse.class);

        assertEquals(0, response.getDraw());
        assertEquals(1902, response.getRecordsTotal());
        assertEquals(1902, response.getRecordsFiltered());

        List<TefasFund> funds = response.getData();
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
}
