package com.gocypher.cybench;

import com.gocypher.cybench.utils.Nodes;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        Object root = tree.getTree().getModel().getRoot();
        DefaultMutableTreeNode newChild = new Nodes.BenchmarkClassNode(name);
        ((DefaultMutableTreeNode) root).add(newChild);
        tree.getTree().expandPath(tree.getTree().getPathForRow(0));


    }

    void testClassFinished() {
    }

    void testStarted(String name) {
        TreeModel model = tree.getTree().getModel();
        DefaultMutableTreeNode currentClass = findNode(name, model);
        currentClass.add(new Nodes.BenchmarkTestNode(name));
        ((DefaultTreeModel) model).reload();

    }

    private DefaultMutableTreeNode findNode(String name, TreeModel tree) {
        Object root = tree.getRoot();
        for (int i = 0; i < tree.getChildCount(root); i++) {
            Object child = tree.getChild(root, i);
            if (child instanceof DefaultMutableTreeNode) {
                try {
                    if (name.startsWith(((DefaultMutableTreeNode) child).getUserObject().toString())) {
                        return (DefaultMutableTreeNode) child;
                    }
                } catch (NullPointerException r) {
                }

            }
        }
        return (DefaultMutableTreeNode) root;
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
