package com.gocypher.cybench.utils;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;

public class Nodes {
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
}
