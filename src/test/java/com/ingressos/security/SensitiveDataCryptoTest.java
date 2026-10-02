package com.ingressos.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SensitiveDataCryptoTest {
    private static final String TEST_KEY = "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";
    private final SensitiveDataCrypto crypto = new SensitiveDataCrypto(TEST_KEY, TEST_KEY);

    @Test
    void encryptsWithDifferentNoncesAndRoundTrips() {
        String firstCiphertext = crypto.encrypt("12345678909");
        String secondCiphertext = crypto.encrypt("12345678909");

        assertNotEquals(firstCiphertext, secondCiphertext);
        assertEquals("12345678909", crypto.decrypt(firstCiphertext));
        assertEquals("12345678909", crypto.decrypt(secondCiphertext));
    }

    @Test
    void createsDeterministicSearchIndexWithoutExposingDocument() {
        String firstIndex = crypto.index("12345678909");

        assertEquals(firstIndex, crypto.index("12345678909"));
        assertNotEquals("12345678909", firstIndex);
        assertEquals(64, firstIndex.length());
    }

    @Test
    void converterStoresCiphertextAndReadsLegacyPlaintext() {
        AesGcmStringConverter converter = new AesGcmStringConverter(crypto);
        String databaseValue = converter.convertToDatabaseColumn("12345678909");

        assertNotEquals("12345678909", databaseValue);
        assertEquals("12345678909", converter.convertToEntityAttribute(databaseValue));
        assertEquals("12345678909", converter.convertToEntityAttribute("12345678909"));
    }
}