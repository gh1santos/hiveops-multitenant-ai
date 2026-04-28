package com.company.hiveops.shared.utils;

public class TokenCalculatorUtils {

    private TokenCalculatorUtils() {
    }
    public static int estimateTokens(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        return (int) Math.ceil((double) text.length() / 4.0);
    }
}