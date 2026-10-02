package com.ingressos.security;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Component
@Converter
public class AesGcmStringConverter implements AttributeConverter<String, String> {
    private final SensitiveDataCrypto crypto;

    public AesGcmStringConverter(SensitiveDataCrypto crypto) {
        this.crypto = crypto;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return crypto.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String databaseValue) {
        return crypto.decrypt(databaseValue);
    }
}