package com.gocypher.cybench.utils;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.io.File;

public class Nodes {
    public static class BenchmarkClassNode extends DefaultMutableTreeNode {
        public BenchmarkClassNode(Object userObject) {
            super(userObject);
        }
    }

    public static class BenchmarkTestNode extends DefaultMutableTreeNode {
        private String fullyQualifiedName;
        public BenchmarkTestNode(Object userObject) {

            super(getTestName(userObject));
            this.fullyQualifiedName = String.valueOf(userObject);
        }

        public String getFullyQualifiedName() {
            return fullyQualifiedName;
        }
    }

    @NotNull
    private static String getTestName(Object userObject) {
        String value = String.valueOf(userObject);
        try {
            return value.substring(value.lastIndexOf('.') + 1);
        } catch (Exception e) {
            return value;
        }
    }

    public static class BenchmarkRootNode extends DefaultMutableTreeNode {
        public BenchmarkRootNode(Object userObject) {
            super(userObject);
        }
    }

    public static class BenchmarkReportFileNode extends DefaultMutableTreeNode {
        File file;
        public BenchmarkReportFileNode(Object userObject, File file) {
            super(userObject);
            this.file = file;
        }

        public File getFile() {
            return file;
        }
    }

    public static DefaultMutableTreeNode findNode(String name, TreeModel tree) {
        Object root = tree.getRoot();
        for (int i = 0; i < tree.getChildCount(root); i++) {
            Object child = tree.getChild(root, i);
            if (child instanceof DefaultMutableTreeNode) {
                try {
                    Object userObject = ((DefaultMutableTreeNode) child).getUserObject();
                    if (child instanceof Nodes.BenchmarkClassNode && name.startsWith(userObject.toString())) {
                        return (DefaultMutableTreeNode) child;
                    }
                } catch (NullPointerException r) {
                }

            }
        }
        return (DefaultMutableTreeNode) root;
    }

    public static boolean addTest(String name, JTree tree) {

        TreeModel model = tree.getModel();
        DefaultMutableTreeNode currentClass = findNode(name, model);
        if (currentClass == null ) {
            return false;
        }

        currentClass.add(new Nodes.BenchmarkTestNode(name));
        ((DefaultTreeModel) model).reload();
        return true;
    }


    public static void addClass(String name, JTree tree) {
        Object root = tree.getModel().getRoot();
        if (!findNode(name, tree.getModel()).equals(root)) return;

        DefaultMutableTreeNode newChild = new Nodes.BenchmarkClassNode(name);
        ((DefaultMutableTreeNode) root).add(newChild);
        tree.expandPath(tree.getPathForRow(0));
    }

}
