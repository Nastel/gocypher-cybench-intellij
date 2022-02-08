/*
 * Copyright (C) 2020-2022, K2N.IO.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 */

package com.gocypher.cybench;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Before;
import org.junit.Test;

import com.gocypher.cybench.runConfiguration.CyBenchMessageHandler;
import com.gocypher.cybench.runConfiguration.CyBenchResultTreeConsoleView;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.util.Key;

public class CyBenchMessageHandlerTest {

    private CyBenchMessageHandler handler;
    private int foundClasses;
    private int foundMethods;

    @Before
    public void setup() {
        handler = new CyBenchMessageHandler(mock(CyBenchResultTreeConsoleView.class)) {

            @Override
            protected void testClassFound(String name) {
                System.out.println("Class " + name);
                foundClasses++;
            }

            @Override
            public void testClassFinished() {
                System.out.println("Class end");
            }

            @Override
            public void testStarted(String name) {
                System.out.println("\t Benchmark method " + name);
                foundMethods++;
            }

            @Override
            public void testsFinished() {
                System.out.println("Benchmark method end");
            }
        };
    }

    @Test
    public void testWithSingleTestClass() throws IOException {

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream("singleBenchmarkOutput")));

        ProcessEvent mock = mock(ProcessEvent.class);

        String line;
        while ((line = reader.readLine()) != null) {
            when(mock.getText()).thenReturn(line);
            handler.onTextAvailable(mock, mock(Key.class));
        }
        assertEquals(1, foundClasses);
        assertEquals(6, foundMethods);

    }

    @Test
    public void testWitMultipleTestClass() throws IOException {

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream("multipleBenchmarkOutput")));

        ProcessEvent mock = mock(ProcessEvent.class);

        String line;
        while ((line = reader.readLine()) != null) {
            when(mock.getText()).thenReturn(line);
            handler.onTextAvailable(mock, mock(Key.class));
        }
        assertEquals(7, foundClasses);
        assertEquals(37, foundMethods);

    }

}
