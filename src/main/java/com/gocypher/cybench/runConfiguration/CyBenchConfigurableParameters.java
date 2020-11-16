package com.gocypher.cybench.runConfiguration;

import com.gocypher.cybench.launcher.utils.Constants;

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
    MEASUREMENT_ITERATIONS(Constants.MEASUREMENT_ITERATIONS, 5, "Number of iterations", "", TYPE.NUMBER, s -> {
        int value = Integer.parseInt(s);
        return value >= 0 && value <= 10;
    }, "Value should be a number between 0 and 10"),
    WARM_UP_ITERATIONS(Constants.WARM_UP_ITERATIONS, 1, "Number of warm-up iterations", "", TYPE.NUMBER, s -> {
        int value = Integer.parseInt(s);
        return value >= 0 && value <= 10;
    }, "Value should be a number between 0 and 10"),
    WARM_UP_SECONDS(Constants.WARM_UP_SECONDS, 5, "Warm-up seconds", "", TYPE.NUMBER, s -> {
        int value = Integer.parseInt(s);
        return value >= 0 && value <= 10;
    }, "Value should be a number between 0 and 10"),
    SHOULD_SEND_REPORT(Constants.SEND_REPORT, 1, "Should send report to CyBench", "", TYPE.BOOLEAN, s ->
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


