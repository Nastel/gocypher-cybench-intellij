package com.gocypher.cybench;

import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.openapi.project.Project;
import com.intellij.ui.AnimatedIcon;
import com.intellij.ui.ColoredTreeCellRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class CyBechResultTreeConsoleViewTest  {

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

                //consoleViewPanel.add(jPanel, BorderLayout.CENTER);
                getTree().setModel(new DefaultTreeModel(new DefaultMutableTreeNode("CyBenchBenchmark")));
                getTree().putClientProperty(AnimatedIcon.ANIMATION_IN_RENDERER_ALLOWED, true);
                getTree().setCellRenderer(new ColoredTreeCellRenderer() {
                    @Override
                    public void customizeCellRenderer(@NotNull JTree jTree, Object o, boolean b, boolean b1, boolean b2, int i, boolean b3) {
                        setIcon(new AnimatedIcon.Default());
                        if (o instanceof DefaultMutableTreeNode) {
                            append(String.valueOf(((DefaultMutableTreeNode) o).getUserObject()));
                        }
                    }
                });
            }
        };

        Object root = console.getTree().getModel().getRoot();
        DefaultMutableTreeNode newChild = new CyBenchMessageHandler.BenchmarkClassNode("Test");
        newChild.add(new CyBenchMessageHandler.BenchmarkTestNode("com.gocypher.cybench.jmh.jvm.client.tests.StringBenchmarks.stringBufferReplaceAll"));
        newChild.add(new CyBenchMessageHandler.BenchmarkTestNode("com.gocypher.cybench.jmh.jvm.client.tests.StringBenchmarks.stringConcatMultiChars"));
        newChild.add(new CyBenchMessageHandler.BenchmarkTestNode("com.gocypher.cybench.jmh.jvm.client.tests.StringBenchmarks.stringReplaceAll"));
        ((DefaultMutableTreeNode) root).add(newChild);

        console.generateResultTabs();

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(console.getComponent());
            }
        });
    }

    public static void createAndShowGUI(JComponent component) {
        //Create and set up the window.
        JFrame frame = new JFrame("HelloWorldSwing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add the ubiquitous "Hello World" label.
        JLabel label = new JLabel("Hello World");
        frame.getContentPane().add(component);

        frame.setPreferredSize(new Dimension(600,600));
        //Display the window.

        frame.pack();
        frame.setVisible(true);


    }


}
