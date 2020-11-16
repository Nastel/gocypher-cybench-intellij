package com.gocypher.cybench.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gocypher.cybench.launcher.model.BenchmarkOverviewReport;
import com.gocypher.cybench.launcher.model.BenchmarkReport;
import org.codehaus.jettison.json.JSONException;


import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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


        Map<String, Object> entries = new LinkedHashMap<>();

        entries.put("Benchmark Name", benchmarkOverviewReport.getBenchmarkSettings().get("benchReportName"));
        entries.put("Timestamp", new SimpleDateFormat("yyy-MM-dd HH:mm:ss").format(benchmarkOverviewReport.getTimestamp()));
        entries.put("Online report", benchmarkOverviewReport.getReportURL());

        entries.put("Total score", benchmarkOverviewReport.getTotalScore());
        entries.put("Visibility", benchmarkOverviewReport.getUploadStatus());

        entries.put("Thread Count", benchmarkOverviewReport.getBenchmarkSettings().get("benchThreadCount"));

        Object unclassifiedProperties = benchmarkOverviewReport.getEnvironmentSettings().get("unclassifiedProperties");
        if (unclassifiedProperties instanceof Map) {
            ((Map) unclassifiedProperties).forEach((k, v) -> {
                if (k.equals("performanceGarbageCollectors") && v instanceof String) {
                    entries.put(String.valueOf(k), ((List<String>) v).stream().map(vv -> String.valueOf(v)).collect(Collectors.joining(",")));
                    return;
                }
                if (k.equals("performanceJvmRuntimeParameters")) {
                    entries.put("performanceJvmRuntimeParameters", "");
                    ((List<String>) v).stream().map(String::valueOf).forEach(kk -> {
                        entries.put("\t" +kk.substring(0, kk.indexOf("=")), kk.substring(kk.indexOf("=") + 1));
                    });
                    return;
                }
                entries.put(String.valueOf(k), String.valueOf(v));
            });
        }


        onSummaryEntries(entries);
        onJVMEntries((Map) benchmarkOverviewReport.getEnvironmentSettings().get("jvmEnvironment"));
        onEnvironmentEntries((Map) benchmarkOverviewReport.getEnvironmentSettings().get("environment"));


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
