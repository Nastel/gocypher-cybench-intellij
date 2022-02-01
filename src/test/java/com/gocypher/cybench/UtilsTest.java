/*
 * Copyright (C) 2020, K2N.IO.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301  USA
 */

package com.gocypher.cybench;

import static com.gocypher.cybench.utils.Utils.convertNumToStringByLength;
import static com.gocypher.cybench.utils.Utils.getKeyName;

import org.junit.Test;

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
