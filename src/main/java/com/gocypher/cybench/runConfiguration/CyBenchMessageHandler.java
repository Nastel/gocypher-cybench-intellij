package com.gocypher.cybench.runConfiguration;

import com.gocypher.cybench.runConfiguration.CyBenchResultTreeConsoleView;
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

    void testClassFound(String name) {
        addClass(name, tree.getTree());
    }



    void testClassFinished() {
    }

    void testStarted(String name) {
        addTest(name, tree.getTree());
    }



    void testsFinished() {
        tree.generateResultTabs();

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
