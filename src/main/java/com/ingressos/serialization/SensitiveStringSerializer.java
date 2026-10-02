package com.ingressos.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.regex.Pattern;

public class SensitiveStringSerializer extends StdSerializer<String> {
    private static final Pattern EMAIL = Pattern.compile("^([^@]+)@(.+)$");

    public SensitiveStringSerializer() {
        super(String.class);
    }

    @Override
    public void serialize(String value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeString(mask(value));
    }

    private String mask(String value) {
        if (value == null) {
            return null;
        }

        var emailMatcher = EMAIL.matcher(value);
        if (emailMatcher.matches()) {
            String localPart = emailMatcher.group(1);
            String maskedLocalPart = localPart.length() < 3
                    ? localPart.charAt(0) + "***"
                    : localPart.charAt(0) + "***" + localPart.charAt(localPart.length() - 1);
            return maskedLocalPart + "@" + emailMatcher.group(2);
        }

        long digitCount = value.chars().filter(Character::isDigit).count();
        if (digitCount != 11 && digitCount != 14) {
            return "***";
        }

        int digitsToMask = (int) digitCount - 2;
        StringBuilder masked = new StringBuilder(value.length());
        for (char character : value.toCharArray()) {
            if (Character.isDigit(character) && digitsToMask > 0) {
                masked.append('*');
                digitsToMask--;
            } else {
                masked.append(character);
            }
        }
        return masked.toString();
    }
}