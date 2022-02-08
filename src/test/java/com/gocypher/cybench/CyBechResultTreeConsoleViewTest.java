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

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.awt.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.jetbrains.annotations.NotNull;

import com.gocypher.cybench.runConfiguration.CyBenchResultTreeConsoleView;
import com.gocypher.cybench.utils.Nodes;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.openapi.project.Project;
import com.intellij.ui.AnimatedIcon;
import com.intellij.ui.ColoredTreeCellRenderer;

public class CyBechResultTreeConsoleViewTest {

    private static CyBenchResultTreeConsoleView console;

    public static void main(String[] args) {

        Project mock = mock(Project.class);
        doReturn(".").when(mock).getBasePath();
        console = new CyBenchResultTreeConsoleView(mock) {
            @Override
            public void initialize() {
                JPanel jPanel = new JPanel();
                jPanel.setBackground(Color.CYAN);

                consoleView = mock(ConsoleViewImpl.class);

                // consoleViewPanel.add(jPanel, BorderLayout.CENTER);
                getTree().setModel(new DefaultTreeModel(new DefaultMutableTreeNode("CyBenchBenchmark")));
                getTree().setCellRenderer(new ColoredTreeCellRenderer() {
                    private static final long serialVersionUID = 6874622797138562561L;

                    @Override
                    public void customizeCellRenderer(@NotNull JTree jTree, Object o, boolean b, boolean b1, boolean b2,
                            int i, boolean b3) {
                        setIcon(new AnimatedIcon.Default());
                        if (o instanceof DefaultMutableTreeNode) {
                            append(String.valueOf(((DefaultMutableTreeNode) o).getUserObject()));
                        }
                    }
                });
            }
        };

        Object root = console.getTree().getModel().getRoot();
        DefaultMutableTreeNode newChild = new Nodes.BenchmarkClassNode("Test");
        newChild.add(new Nodes.BenchmarkTestNode(
                "com.gocypher.cybench.jmh.jvm.client.tests.StringBenchmarks.stringBufferReplaceAll"));
        newChild.add(new Nodes.BenchmarkTestNode(
                "com.gocypher.cybench.jmh.jvm.client.tests.StringBenchmarks.stringConcatMultiChars"));
        newChild.add(new Nodes.BenchmarkTestNode(
                "com.gocypher.cybench.jmh.jvm.client.tests.StringBenchmarks.stringReplaceAll"));
        ((DefaultMutableTreeNode) root).add(newChild);

        console.onBenchmarkFinished();

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI(console.getComponent());
            }
        });
    }

    public static void createAndShowGUI(JComponent component) {
        // Create and set up the window.
        JFrame frame = new JFrame("HelloWorldSwing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add the ubiquitous "Hello World" label.
        JLabel label = new JLabel("Hello World");
        frame.getContentPane().add(component);

        frame.setPreferredSize(new Dimension(600, 600));
        // Display the window.

        frame.pack();
        frame.setVisible(true);

    }

}
