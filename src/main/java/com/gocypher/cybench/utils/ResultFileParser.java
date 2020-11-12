package com.gocypher.cybench.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gocypher.cybench.launcher.model.BenchmarkOverviewReport;
import com.gocypher.cybench.launcher.model.BenchmarkReport;
import org.codehaus.jettison.json.JSONException;


import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ResultFileParser {


    public void parse(File file) throws IOException, JSONException {
        ObjectMapper mapper = new ObjectMapper();
        BenchmarkOverviewReport benchmarkOverviewReport = mapper.readValue(file, BenchmarkOverviewReport.class);


//        JsonPath compile = JsonPath.compile("$.benchmarks.*.*.name");
//        Object read = compile.read(file);


        Map<String, List<BenchmarkReport>> benchmarks = benchmarkOverviewReport.getBenchmarks();

        onJVMEntries((Map) benchmarkOverviewReport.getEnvironmentSettings().get("jvmEnvironment"));
        onEnvironmentEntries((Map) benchmarkOverviewReport.getEnvironmentSettings().get("environment"));


        Map<String, Object> entries = new HashMap<>();
        try {
            PropertyDescriptor[] propertyDescriptors1 = Introspector.getBeanInfo(BenchmarkOverviewReport.class).getPropertyDescriptors();
            for (int i = 0; i < propertyDescriptors1.length; i++) {
                PropertyDescriptor propertyDescriptor = propertyDescriptors1[i];
                if (!propertyDescriptor.getReadMethod().getReturnType().equals(Map.class)) {
                    entries.put(propertyDescriptor.getName(), propertyDescriptor.getReadMethod().invoke(benchmarkOverviewReport));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        onSummaryEntries(entries);


        Object collect = Stream.of(benchmarks.values().toArray(new List[benchmarks.size()])).flatMap(t -> t.stream()).collect(Collectors.toList());


        ((List<BenchmarkReport>) collect).stream().forEach(report -> {
            onTest(report);
            try {
                PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(BenchmarkReport.class).getPropertyDescriptors();
                for (int i = 0; i < propertyDescriptors.length; i++) {
                    PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
                    Object value = propertyDescriptor.getReadMethod().invoke(report);
                    ontTestResultEntry(propertyDescriptor.getName(), String.valueOf(value), i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            onTestEnd(report);
        });
        onFinished();

    }

    protected abstract void onFinished();

    protected abstract void onEnvironmentEntries(Map<String, Object> environment);

    protected abstract void onJVMEntries(Map<String, Object> environmentSettings);

    protected abstract void onSummaryEntries(Map<String, Object> environmentSettings);


    public abstract void onTestEnd(BenchmarkReport report);

    public abstract void onTest(BenchmarkReport report);

    public abstract void ontTestResultEntry(String key, String value, int index);


}
