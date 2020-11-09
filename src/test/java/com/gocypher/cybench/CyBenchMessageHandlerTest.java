package com.gocypher.cybench;

import com.gocypher.cybench.runConfiguration.CyBenchMessageHandler;
import com.gocypher.cybench.runConfiguration.CyBenchResultTreeConsoleView;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.util.Key;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class CyBenchMessageHandlerTest  {

    private CyBenchMessageHandler handler;
    private int foundClasses;
    private int foundMethods;


    @Before
    public void setup() {
        this.handler = new CyBenchMessageHandler(mock(CyBenchResultTreeConsoleView.class)) {
            @Override
            void testClassFound(String name) {
                System.out.println("Class " + name);
                foundClasses++;
            }

            @Override
            void testClassFinished() {
                System.out.println("Class end");
            }

            @Override
            void testStarted(String name) {
                System.out.println("\t Benchmanrk method " + name);
                foundMethods++;
            }

            @Override
            void testsFinished() {
                System.out.println("Benchmanrk method end");
            }
        };
    }


    @Test
    public void testWithSingleTestClass() throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("singleBenchmarkOutput")));


        ProcessEvent mock = mock(ProcessEvent.class);


        String line;
        while ((line = reader.readLine()) != null) {
            when(mock.getText()).thenReturn(line);
            handler.onTextAvailable(mock, mock(Key.class));
        }
        assertEquals(1 ,this.foundClasses);
        assertEquals(6 ,this.foundMethods);

    }

    @Test
    public void testWitMultipleTestClass() throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("multipleBenchmarkOutput")));


        ProcessEvent mock = mock(ProcessEvent.class);


        String line;
        while ((line = reader.readLine()) != null) {
            when(mock.getText()).thenReturn(line);
            handler.onTextAvailable(mock, mock(Key.class));
        }
        assertEquals(7 ,this.foundClasses);
        assertEquals(37 ,this.foundMethods);

    }

}
