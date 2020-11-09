package com.gocypher.cybench;

import org.junit.Test;

import static com.gocypher.cybench.utils.Utils.convertNumToStringByLength;
import static com.gocypher.cybench.utils.Utils.getKeyName;

public class UtilsTest {
    @Test
    public void testConvertNumToStringByLength() {
        System.out.println(convertNumToStringByLength("1"));
        System.out.println(convertNumToStringByLength("925.7556616196711"));
        System.out.println(convertNumToStringByLength("598.3014591995966"));
        System.out.println(convertNumToStringByLength("0.880172"));
        System.out.println(convertNumToStringByLength("0.9126399999999999"));
        System.out.println(convertNumToStringByLength("1.085460088E9"));
        System.out.println(convertNumToStringByLength("1"));
        System.out.println(convertNumToStringByLength("1"));
        System.out.println(convertNumToStringByLength("1"));

    }

    @Test
    public void testGetKeyName() {
        System.out.println(getKeyName("totalScore"));
    }
}
