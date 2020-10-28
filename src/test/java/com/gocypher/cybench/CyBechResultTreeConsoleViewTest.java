package com.gocypher.cybench;

import com.github.CyBenchMessageHandler;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.openapi.project.Project;
import groovy.util.GroovyTestCase;
import org.junit.Test;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class CyBechResultTreeConsoleViewTest  {

    private static CyBechResultTreeConsoleView console;

    public static void main(String[] args) {

        Project mock = mock(Project.class);
        doReturn(".").when(mock).getBasePath();
        console = new CyBechResultTreeConsoleView(mock) {
            @Override
            public void initialize() {
                JPanel jPanel = new JPanel();
                jPanel.setBackground(Color.CYAN);

                consoleView = mock(ConsoleViewImpl.class);

                //consoleViewPanel.add(jPanel, BorderLayout.CENTER);
                getTree().setModel(new DefaultTreeModel(new DefaultMutableTreeNode("CyBenchBenchmark")));

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
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("HelloWorldSwing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add the ubiquitous "Hello World" label.
        JLabel label = new JLabel("Hello World");
        frame.getContentPane().add(console.getComponent());

        frame.setPreferredSize(new Dimension(600,600));
        //Display the window.

        frame.pack();
        frame.setVisible(true);


    }


}
