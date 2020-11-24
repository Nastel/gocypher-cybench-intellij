/*
 * Copyright (C) 2020, K2N.IO.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301  USA
 */

package com.gocypher.cybench.runConfiguration;

import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.gocypher.cybench.utils.Nodes.*;

public class CyBenchMessageHandler implements ProcessListener {
    CyBenchResultTreeConsoleView tree;

    public CyBenchMessageHandler(CyBenchResultTreeConsoleView tree) {
        this.tree = tree;
    }

    @Override
    public void startNotified(@NotNull ProcessEvent processEvent) {

    }

    @Override
    public void processTerminated(@NotNull ProcessEvent processEvent) {

    }

    @Override
    public void processWillTerminate(@NotNull ProcessEvent processEvent, boolean b) {

    }

    @Override
    public void onTextAvailable(@NotNull ProcessEvent processEvent, @NotNull Key key) {
        String message = processEvent.getText();
        Pattern filterPattern = Pattern.compile("Computed hash (.*) for class (.*), classloader (.*)");
        Matcher filterMatcher = filterPattern.matcher(message.replace('/', '.').replace(".class", ""));
        boolean startTest = filterMatcher.find();
        if (startTest) {
            testClassFound(filterMatcher.group(2));
        }

        Pattern filterPattern1 = Pattern.compile("^# Benchmark: " + "(.*)");
        Matcher filterMatcher1 = filterPattern1.matcher(message);
        boolean startTest1 = filterMatcher1.find();
        if (startTest1) {
            testStarted(filterMatcher1.group(1));
        }

        Pattern filterPattern2 = Pattern.compile("Finished CyBench benchmarking");
        Matcher filterMatcher2 = filterPattern2.matcher(message);
        boolean startTest2 = filterMatcher2.find();
        if (startTest2) {
            testsFinished();
        }
    }

    protected void testClassFound(String name) {
        addClass(name, tree.getTree());
    }



    protected void testClassFinished() {
    }

    protected void testStarted(String name) {
        addTest(name, tree.getTree());
    }



    protected void testsFinished() {
        tree.onBenchmarkFinished();

    }


    private void expandAllNodes(JTree tree, int startingIndex, int rowCount) {
        for (int i = startingIndex; i < rowCount; ++i) {
            tree.expandRow(i);
        }

        if (tree.getRowCount() != rowCount) {
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }

    private void expandAllNodes(JTree tree) {
        int j = tree.getRowCount();
        int i = 0;
        while (i < j) {
            tree.expandRow(i);
            i += 1;
            j = tree.getRowCount();
        }
    }

}
