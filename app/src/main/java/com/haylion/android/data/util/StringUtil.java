package com.haylion.android.data.util;

public class StringUtil {

    private StringUtil() {
    }

    public static String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return "";
        }
        if (phoneNumber.length() == 11) {
            return phoneNumber.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        }
        return phoneNumber;
    }



}
