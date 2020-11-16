package com.gocypher.cybench.utils;

import com.gocypher.cybench.core.utils.JSONUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.ResourceBundle;

public class Utils {


    static ResourceBundle titles = ResourceBundle.getBundle("titles");

    public static String convertNumToStringByLength(String value) {
        try {
            return JSONUtils.convertNumToStringByLength(value);
        } catch (NumberFormatException e) {
            return findURI(value);
        }
    }

    private static String findURI(String value) {
        if (value.startsWith("http://") || value.startsWith("https://")) {
            return "<HTML><a href=\"" + value + "\">Open " + value + "</a></HTML>";
        }

        return value;
    }


    public static String getKeyName(String key) {
        if (titles.containsKey(key)) {
            return titles.getString(key);
        } else return key;
    }
}
