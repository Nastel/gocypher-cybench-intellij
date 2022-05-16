/*
 * Copyright (C) 2020-2022, K2N.IO.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 */

package com.gocypher.cybench.runConfiguration;

import java.util.function.Predicate;

import com.gocypher.cybench.launcher.utils.Constants;

public enum CyBenchConfigurableParameters {

    REPORT_NAME(Constants.BENCHMARK_REPORT_NAME, getDefaultReportName(), "Report Name", "", TYPE.STRING, s -> true, ""),

    FORKS(Constants.NUMBER_OF_FORKS, 1, "Forks", "", TYPE.NUMBER, s -> {
        int value = Integer.parseInt(s);
        return value >= 0 && value <= 10;
    }, "Value should be a number between 0 and 10"), //
    THREADS(Constants.RUN_THREAD_COUNT, 1, "Threads", "", TYPE.NUMBER, s -> {
        int value = Integer.parseInt(s);
        return value >= 0 && value <= 10;
    }, "Value should be a number between 0 and 10"), //
    MEASUREMENT_ITERATIONS(Constants.MEASUREMENT_ITERATIONS, 3, "Measurement Iterations", "", TYPE.NUMBER, s -> {
        int value = Integer.parseInt(s);
        return value >= 0 && value <= 10;
    }, "Value should be a number between 0 and 10"), //
    MEASUREMENT_SECONDS(Constants.MEASUREMENT_SECONDS, 1, "Measurement Time (s)", "", TYPE.NUMBER, s -> {
        int value = Integer.parseInt(s);
        return value >= 0 && value <= 100;
    }, "Value should be a number between 0 and 100"), //
    WARM_UP_ITERATIONS(Constants.WARM_UP_ITERATIONS, 1, "Warmup Iterations", "", TYPE.NUMBER, s -> {
        int value = Integer.parseInt(s);
        return value >= 0 && value <= 10;
    }, "Value should be a number between 0 and 10"), //
    WARM_UP_SECONDS(Constants.WARM_UP_SECONDS, 3, "Warmup Time (s)", "", TYPE.NUMBER, s -> {
        int value = Integer.parseInt(s);
        return value >= 0 && value <= 10;
    }, "Value should be a number between 0 and 10"), //
    SHOULD_SEND_REPORT(Constants.SEND_REPORT, false, "Send Report To CyBench", "", TYPE.BOOLEAN, s -> true, ""), //
    SEND_REPORT_WHERE(Constants.REPORT_UPLOAD_STATUS, "public", "Report Upload Status", 
        "Select whether or not to send report to a public or private repository", TYPE.STRING, s-> true, "Value should be either 'public' or 'private'"), //
    COLLECT_HW(Constants.COLLECT_HW, false, "Include Hardware Properties",
            "If checked collects information about processor, memory, graphics, discs, network, os etc. If it's not checked report is not eligible for storing online",
            TYPE.BOOLEAN, s -> true, ""), //

    BENCHMARK_CLASS(Constants.BENCHMARK_RUN_CLASSES, "", "Execute", "Benchmark class to execute", TYPE.CLASS, s -> true,
            "Value should be a class"), //
    BENCH_TOKEN(Constants.USER_REPORT_TOKEN, "", "Bench Access Token",
            "Private benchmark store repo or empty for public", TYPE.STRING, s -> true, ""), //
    QUERY_TOKEN(Constants.USER_QUERY_TOKEN, "", "Bench Query Token",
            "Provide to run automated comparisons in private repo", TYPE.STRING, s -> true, ""), //

    AUTO_SHOULD_RUN(Constants.AUTO_SHOULD_RUN_COMPARISON, false, "Run Performance Regression Test",
            "If checked will take your defined configuration and run automated performance regression testing within your project",
            TYPE.BOOLEAN, s -> true, ""), //
    AUTO_SCOPE(Constants.AUTO_SCOPE, "WITHIN", "Scope", "Scope of automated performance regression testing",
            TYPE.STRING, s -> true, "Value should be either 'WITHIN' or 'BETWEEN'"), //
    AUTO_COMPARE_VERSION(Constants.AUTO_COMPARE_VERSION, "", "Compare Version", "Project version to compare against",
            TYPE.STRING, s -> true, ""), //
    AUTO_LATEST_REPORTS(Constants.AUTO_LATEST_REPORTS, 1, "Number of Latest Reports",
            "Number of latest reports to compare against", TYPE.NUMBER, s -> {
                int value = Integer.parseInt(s);
                return value >= 1;
            }, "Value should be a number greater than or equal to 1"), //
    AUTO_ANOMALIES_ALLOWED(Constants.AUTO_ANOMALIES_ALLOWED, 0, "Number of Allowed Anomalies",
            "Number of anomalies to allow before benchmark runner fails", TYPE.NUMBER, s -> {
                int value = Integer.parseInt(s);
                return value >= 0;
            }, "Value should be a number greater than or equal to 0"), //
    AUTO_METHOD(Constants.AUTO_METHOD, "DELTA", "Comparison Method", "Method to use for comparison", TYPE.STRING,
            s -> true, "Value should be either 'DELTA' or 'SD'"), //
    AUTO_THRESHOLD(Constants.AUTO_THRESHOLD, "GREATER", "Comparison Threshold", "Threshold to use for comparison",
            TYPE.STRING, s -> true, "Value should be either 'GREATER' or 'PERCENT_CHANGE'"), //
    AUTO_PERCENT_CHANGE(Constants.AUTO_PERCENT_CHANGE, 0, "Percent Change Allowed",
            "Percent change allowed before an anomaly is flagged", TYPE.NUMBER, s -> {
                Double value = Double.parseDouble(s);
                return value >= 0;
            }, "Value should be a number greater than or equal to 0"), //
    AUTO_DEVIATIONS_ALLOWED(Constants.AUTO_DEVIATIONS_ALLOWED, 0, "Deviations Allowed",
            "Deviations from mean of compared scores allowed before an anomaly is flagged", TYPE.NUMBER, s -> {
                Double value = Double.parseDouble(s);
                return value >= 0;
            }, "Value should be a number greater than or equal to 0");

    public String error;
    public String key;
    public Object defaultValue;
    public String readableName;
    public String detailExplanationMessage;
    public TYPE type;
    public Predicate<String> validator;

    CyBenchConfigurableParameters(String key, Object defaultValue, String readableName, String detailExplanationMessage,
            TYPE type, Predicate<String> validator, String errorMessage) {
        this.key = key;
        this.readableName = readableName;
        this.defaultValue = defaultValue;
        this.detailExplanationMessage = detailExplanationMessage;
        this.type = type;
        this.validator = validator;
        error = errorMessage;
    }

    private static Object getDefaultReportName() {
        return "Benchmark report";
    }

    public enum TYPE {
        NUMBER, BOOLEAN, CLASS, STRING
    }
}
