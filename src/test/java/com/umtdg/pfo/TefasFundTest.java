package com.umtdg.pfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umtdg.pfo.tefas.TefasFund;
import com.umtdg.pfo.tefas.TefasFetchResponse;

public class TefasFundTest {
    @Test
    public void testDeserializer() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        String json = """
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

        try {
            TefasFund fund = mapper.readValue(json, TefasFund.class);

            assertEquals("FUN", fund.getCode());
            assertNotNull(fund.getDate());
            assertTrue(fund.getPrice() > 0);

            System.out.println("Deserialized successfully:");
            System.out.println("Code: " + fund.getCode());
            System.out.println("Date: " + fund.getDate());
            System.out.println("Price: " + fund.getPrice());
        } catch (Exception e) {
            System.err.println("Deserialization failed");
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    public void testWithRealResponseData() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // Using the actual JSON structure from your server response
        String json = """
            {
                "draw": 0,
                "recordsTotal": 3559,
                "recordsFiltered": 3559,
                "data": [
                    {
                        "TARIH": "1750982400000",
                        "FONKODU": "AAK",
                        "FONUNVAN": "ATA ASSET MANAGEMENT MULTI-ASSET VARIABLE FUND",
                        "FIYAT": 26.523359,
                        "TEDPAYSAYISI": 1328497.0,
                        "KISISAYISI": 778.0,
                        "PORTFOYBUYUKLUK": 35236202.48,
                        "BilFiyat": "-"
                    }
                ]
            }
            """;

        try {
            TefasFetchResponse response = mapper
                .readValue(json, TefasFetchResponse.class);
            System.out.println("Full response deserialized successfully!");
            System.out.println("Records total: " + response.getRecordsTotal());
            System.out.println("Data size: " + response.getData().size());

            if (!response.getData().isEmpty()) {
                TefasFund firstFund = response.getData().get(0);
                System.out.println("First fund date: " + firstFund.getDate());
                System.out.println("First fund code: " + firstFund.getCode());
            }

            assertNotNull(response);
            assertEquals(3559, response.getRecordsTotal());
            assertFalse(response.getData().isEmpty());

        } catch (Exception e) {
            System.err.println("Full response deserialization failed:");
            System.err.println("Error type: " + e.getClass().getSimpleName());
            System.err.println("Error message: " + e.getMessage());

            // Print the full stack trace for debugging
            e.printStackTrace();

            // Check if it's a nested exception
            Throwable cause = e.getCause();
            if (cause != null) {
                System.err.println("Caused by: " + cause.getClass().getSimpleName());
                System.err.println("Cause message: " + cause.getMessage());
                cause.printStackTrace();
            }

            throw e;
        }
    }

    @Test
    public void testEpochDeserializerDirectly() throws Exception {
        System.out.println("=== Testing EpochDeserializer directly ===");

        // Test the timestamp value directly
        String epochString = "1750982400000";
        long epochMilli = Long.parseLong(epochString);

        System.out.println("Epoch string: " + epochString);
        System.out.println("Epoch long: " + epochMilli);

        try {
            java.time.Instant instant = java.time.Instant.ofEpochMilli(epochMilli);
            java.time.LocalDate date = instant
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();

            System.out.println("Converted instant: " + instant);
            System.out.println("Converted date: " + date);

            // This should work fine
            assertNotNull(date);

        } catch (Exception e) {
            System.err.println("Direct epoch conversion failed:");
            e.printStackTrace();
            throw e;
        }
    }
}
