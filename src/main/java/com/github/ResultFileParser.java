package com.github;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.codehaus.jettison.json.JSONException;


import java.io.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ResultFileParser {


    public void parse(File file) throws IOException, JSONException {

        JsonPath compile = JsonPath.compile("$.benchmarks.*.*.name");
        Object read = compile.read(file);


        if (read instanceof List) {
            List testMethods = (List) read;
            for (Object o : testMethods) {
                onTest(String.valueOf(o));

                JsonPath JPathForTestResultObj = JsonPath.compile("$.benchmarks.*.[?(@.name == \"" + String.valueOf(o) + "\")]");
                Object read1 = JPathForTestResultObj.read(file);

                Set<Map.Entry> set = ((LinkedHashMap) ((JSONArray) read1).get(0)).entrySet();
                AtomicInteger index = new AtomicInteger();
                set.forEach(entry -> ontTestResultEntry(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()), index.getAndIncrement()));
                onTestEnd(String.valueOf(o));
            }
        }

    }

    public abstract void onTestEnd(String name);

    public abstract void onTest(String name);

    public abstract void ontTestResultEntry(String key, String value, int index);


}
