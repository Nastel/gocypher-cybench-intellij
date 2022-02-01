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

import java.io.File;
import java.util.Map;

import org.junit.Test;

import com.gocypher.cybench.launcher.model.BenchmarkReport;
import com.gocypher.cybench.utils.ResultFileParser;

public class ResultFileParserTest {

    @Test
    public void testReadFromResultFile() throws Exception {
        ResultFileParser resultFileParser = new ResultFileParser() {
            @Override
            protected void onFinished() {

            }

            @Override
            protected void onEnvironmentEntries(Map<String, Object> environment) {

            }

            @Override
            protected void onJVMEntries(Map<String, Object> environmentSettings) {

            }

            @Override
            protected void onSummaryEntries(Map<String, Object> environmentSettings) {

            }

            @Override
            public void onTestEnd(BenchmarkReport report) {
                System.out.println("Test end");
            }

            @Override
            public void onTest(BenchmarkReport report) {
                System.out.println("Test: " + report);
            }

            @Override
            public void ontTestResultEntry(String key, String value, int index) {
                System.out.println("\t" + index + " - " + key + " : " + value);
            }
        };
        resultFileParser.parse(new File("C:\\Users\\slabs\\Downloads\\report.json"));
    }

}
