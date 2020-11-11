package com.gocypher.cybench;;


import com.gocypher.cybench.launcher.model.BenchmarkReport;
import com.gocypher.cybench.utils.ResultFileParser;
import org.codehaus.jettison.json.JSONException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ResultFileParserTest {

    @Test
    public void testReadFromResultFile() throws IOException, JSONException {
        ResultFileParser resultFileParser = new ResultFileParser() {
            @Override
            protected void onFinished() {

            }

            @Override
            protected void onEnviromentEntries(Map<String, Object> environment) {

            }

            @Override
            protected void onJVMEnties(Map<String, Object> environmentSettings) {

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
                System.out.println("\t" + index + " - " + key +" : " + value);
            }
        };
        resultFileParser.parse(new File("C:\\Users\\slabs\\Downloads\\report.json"));
    }

}
