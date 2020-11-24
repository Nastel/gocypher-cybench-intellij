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
