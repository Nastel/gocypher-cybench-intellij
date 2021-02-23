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

package com.gocypher.cybench.runConfiguration;

import com.gocypher.cybench.launcher.utils.Constants;
import com.intellij.openapi.module.ModuleManager;

import java.util.function.Predicate;

public enum CyBenchConfigurableParameters {

    REPORT_NAME(Constants.BENCHMARK_REPORT_NAME, getDefaultReportName(), "Report Name", "", TYPE.STRING, s -> true, ""),


    FORKS(Constants.NUMBER_OF_FORKS, 1, "Number of forks", "", TYPE.NUMBER, s -> {
        int value = Integer.parseInt(s);
        return value >= 0 && value <= 10;
    }, "Value should be a number between 0 and 10"),
    THREADS(Constants.RUN_THREAD_COUNT, 1, "Number of threads", "", TYPE.NUMBER, s -> {
        int value = Integer.parseInt(s);
        return value >= 0 && value <= 10;
    }, "Value should be a number between 0 and 10"),
    MEASUREMENT_ITERATIONS(Constants.MEASUREMENT_ITERATIONS, 3, "Number of iterations", "", TYPE.NUMBER, s -> {
        int value = Integer.parseInt(s);
        return value >= 0 && value <= 10;
    }, "Value should be a number between 0 and 10"),
    MEASUREMENT_SECONDS(Constants.MEASUREMENT_SECONDS, 1, "Measurement time in seconds", "", TYPE.NUMBER, s -> {
        int value = Integer.parseInt(s);
        return value >= 0 && value <= 100;
    }, "Value should be a number between 0 and 100"),
    WARM_UP_ITERATIONS(Constants.WARM_UP_ITERATIONS, 1, "Number of warm-up iterations", "", TYPE.NUMBER, s -> {
        int value = Integer.parseInt(s);
        return value >= 0 && value <= 10;
    }, "Value should be a number between 0 and 10"),
    WARM_UP_SECONDS(Constants.WARM_UP_SECONDS, 3, "Warm-up seconds", "", TYPE.NUMBER, s -> {
        int value = Integer.parseInt(s);
        return value >= 0 && value <= 10;
    }, "Value should be a number between 0 and 10"),
    SHOULD_SEND_REPORT(Constants.SEND_REPORT, false, "Should send report to CyBench", "", TYPE.BOOLEAN, s ->
            true, ""),
    COLLECT_HW(Constants.COLLECT_HW, false, "Collect HW properties", "If checked collects information about processor, memory, graphics, discs, network, os etc. If it's not checked report is not eligible for storing online", TYPE.BOOLEAN, s ->
            true, ""),

    BENCHMARK_CLASS(Constants.BENCHMARK_RUN_CLASSES, "", "Benchmark class", "", TYPE.CLASS, s -> true, "Value should be a class");


    public String error;
    public String key;
    public Object defaultValue;
    public String readableName;
    public String detailExplanationMessage;
    public TYPE type;
    public Predicate<String> validator;


    CyBenchConfigurableParameters(String key, Object defaultValue, String readableName, String detailExplanationMessage, TYPE type, Predicate<String> validator, String errorMessage) {
        this.key = key;
        this.readableName = readableName;
        this.defaultValue = defaultValue;
        this.detailExplanationMessage = detailExplanationMessage;
        this.type = type;
        this.validator = validator;
        this.error = errorMessage;
    }

    private static Object getDefaultReportName() {
        return "Benchmark report";
    }


    public enum TYPE {
        NUMBER,
        BOOLEAN,
        CLASS,
        STRING;
    }
}


