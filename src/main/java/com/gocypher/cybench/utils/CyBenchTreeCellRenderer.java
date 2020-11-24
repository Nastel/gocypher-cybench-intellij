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
