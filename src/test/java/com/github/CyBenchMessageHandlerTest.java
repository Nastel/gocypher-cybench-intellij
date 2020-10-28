package com.github;

import com.gocypher.cybench.CyBechResultTreeConsoleView;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.util.Key;
import groovy.util.GroovyTestCase;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.mockito.Mockito.*;

public class CyBenchMessageHandlerTest  {
    @Test
    public void testWithSingleTestClass() throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("singleBenchmarkOutput")));
        CyBenchMessageHandler handler = new CyBenchMessageHandler(mock(CyBechResultTreeConsoleView.class)) {
            @Override
            void testClassStarted(String name) {
                System.out.println("Class " + name);
            }

            @Override
            void testClassFinished() {
                System.out.println("Class end");
            }

            @Override
            void testStarted(String name ) {
                System.out.println("\t Benchmanrk method " + name);
            }

            @Override
            void testFinished() {
                System.out.println("Benchmanrk method end");
            }
        };



        ProcessEvent mock = mock(ProcessEvent.class);


        String line;
        while ((line = reader.readLine()) != null) {
            when(mock.getText()).thenReturn(line);
            handler.onTextAvailable(mock, mock(Key.class));
        }


    }

}
