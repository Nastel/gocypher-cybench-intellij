package com.gocypher.cybench;;


import org.codehaus.jettison.json.JSONException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class ResultFileParserTest {

    @Test
    public void testReadFromResultFile() throws IOException, JSONException {
        ResultFileParser resultFileParser = new ResultFileParser() {
            @Override
            public void onTestEnd(String name) {
                System.out.println("Test end");
            }

            @Override
            public void onTest(String name) {
                System.out.println("Test: " + name);
            }

            @Override
            public void ontTestResultEntry(String key, String value, int index) {
                System.out.println("\t" + index + " - " + key +" : " + value);
            }
        };
        resultFileParser.parse(new File("C:\\Users\\slabs\\Downloads\\report.json"));
    }

}
