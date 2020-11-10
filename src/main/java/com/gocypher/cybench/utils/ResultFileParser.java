package com.gocypher.cybench.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gocypher.cybench.launcher.model.BenchmarkOverviewReport;
import com.gocypher.cybench.launcher.model.BenchmarkReport;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.codehaus.jettison.json.JSONException;


import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ResultFileParser {


    public void parse(File file) throws IOException, JSONException {
        ObjectMapper mapper = new ObjectMapper();
        BenchmarkOverviewReport benchmarkOverviewReport = mapper.readValue(file, BenchmarkOverviewReport.class);


//        JsonPath compile = JsonPath.compile("$.benchmarks.*.*.name");
//        Object read = compile.read(file);


        Map<String, List<BenchmarkReport>> benchmarks = benchmarkOverviewReport.getBenchmarks();

        onJVMEnties((Map) benchmarkOverviewReport.getEnvironmentSettings().get("jvmEnvironment"));
        onEnviromentEntries((Map) benchmarkOverviewReport.getEnvironmentSettings().get("environment"));


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

    protected abstract void onEnviromentEntries(Map<String, Object> environment);

    protected abstract void onJVMEnties(Map<String, Object> environmentSettings);


    public abstract void onTestEnd(BenchmarkReport report);

    public abstract void onTest(BenchmarkReport report);

    public abstract void ontTestResultEntry(String key, String value, int index);


}
