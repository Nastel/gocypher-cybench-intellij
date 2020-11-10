package com.gocypher.cybench.utils;

import com.intellij.ui.ColoredTreeCellRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class CyBenchTreeCellRenderer extends ColoredTreeCellRenderer {
    @Override
    public void customizeCellRenderer(@NotNull JTree jTree, Object o, boolean b, boolean b1, boolean b2, int i, boolean b3) {
        if (o instanceof Nodes.BenchmarkClassNode) {
            setIcon(CyBenchIcons.classNodeIcon);

        }
        if (o instanceof Nodes.BenchmarkTestNode) {
            setIcon(CyBenchIcons.testNodeIcon);

        }
        if (o instanceof Nodes.BenchmarkReportFileNode) {
            setIcon(CyBenchIcons.reportNodeIcon);

        }
        if (o instanceof DefaultMutableTreeNode) {
            append(String.valueOf(((DefaultMutableTreeNode) o).getUserObject()));
        }
    }
}
