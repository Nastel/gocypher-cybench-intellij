package com.gocypher.cybench.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.ResourceBundle;

public class Utils {


    static ResourceBundle titles = ResourceBundle.getBundle("titles");

    public static String convertNumToStringByLength(String value) {
        try {


            double v = Double.parseDouble(value);
            if (value != null) {
                if (value.indexOf(".") < 1) {
                    return value;
                }
                if (Math.abs(v) > 1) {
                    return convertNumToStringFrac(v, 2, 2);
                }
                if (Math.abs(v) > 0.1) {
                    return convertNumToStringFrac(v, 2, 2);
                }
                if (Math.abs(v) > 0.01) {
                    return convertNumToStringFrac(v, 3, 3);
                }
                if (Math.abs(v) > 0.001) {
                    return convertNumToStringFrac(v, 4, 4);
                }
                if (Math.abs(v) > 0.0001) {
                    return convertNumToStringFrac(v, 5, 5);
                }
                if (Math.abs(v) > 0.00001) {
                    return convertNumToStringFrac(v, 6, 6);
                }
                if (v == 0) {
                    return convertNumToStringFrac(v, 0, 0);
                }
                return convertNumToStringFrac(v, 6, 8);
            }
        } catch (NumberFormatException e) {
            return value;
        }


        return value;
    }

    private static String convertNumToStringFrac(Object value, int minFractionDigits, int maxFractionDigits) {

        DecimalFormat decimalFormat = new DecimalFormat();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        decimalFormat.setMinimumFractionDigits(minFractionDigits);
        decimalFormat.setMinimumFractionDigits(maxFractionDigits);
        decimalFormat.setDecimalFormatSymbols(symbols);
        return decimalFormat.format(value);
    }


    public static String getKeyName(String key) {
        if (titles.containsKey(key)) {
            return titles.getString(key);
        } else return key;
    }
}
