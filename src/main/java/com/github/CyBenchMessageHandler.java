package com.github;

import com.gocypher.cybench.CyBechResultTreeConsoleView;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CyBenchMessageHandler implements ProcessListener {
    CyBechResultTreeConsoleView tree;
    private DefaultMutableTreeNode currentClass;

    public CyBenchMessageHandler(CyBechResultTreeConsoleView tree) {
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
        Pattern filterPattern = Pattern.compile("(?i)" + "benchmarkClass name " + "(.*)");
        Matcher filterMatcher = filterPattern.matcher(message);
        boolean startTest = filterMatcher.find();
        if (startTest) {
            testClassStarted(filterMatcher.group(1));
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
            testClassFinished();
        }
    }

    void testClassStarted(String name) {
        Object root = tree.getTree().getModel().getRoot();
        DefaultMutableTreeNode newChild = new BenchmarkClassNode(name);
        ((DefaultMutableTreeNode) root).add(newChild);
        currentClass = newChild;
        tree.getTree().expandPath(tree.getTree().getPathForRow(0));


    }

    void testClassFinished() {
        tree.generateResultTabs();
    }

    void testStarted(String name) {
        currentClass.add(new BenchmarkTestNode(name));
        ((DefaultTreeModel) tree.getTree().getModel()).reload();

    }

    void testFinished() {
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

    public static class BenchmarkClassNode extends DefaultMutableTreeNode {
        public BenchmarkClassNode(Object userObject) {
            super(userObject);
        }
    }

    public static class BenchmarkTestNode extends DefaultMutableTreeNode {
        public BenchmarkTestNode(Object userObject) {
            super(userObject);
        }
    }
}
