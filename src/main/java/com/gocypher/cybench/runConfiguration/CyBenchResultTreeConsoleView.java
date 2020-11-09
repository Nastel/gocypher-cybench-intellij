/*
 * Copyright 2015-2016 Rose Silver Software LLC and Scott Wells
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gocypher.cybench.runConfiguration;

import com.gocypher.cybench.utils.NodeAndTabFiller;
import com.gocypher.cybench.utils.Nodes;
import com.gocypher.cybench.utils.ResultFileParser;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.ide.ui.laf.darcula.ui.DarculaTabbedPaneUI;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.AnimatedIcon;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.UIUtil;
import org.codehaus.jettison.json.JSONException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CyBenchResultTreeConsoleView implements ConsoleView {
    private final Project project;
    private final List<ConsoleViewEntry> consoleViewEntries = new LinkedList<ConsoleViewEntry>();
    JPanel consoleViewPanel;
    ConsoleViewImpl consoleView;
    private JPanel rootPanel;
    private HashMap<String, JScrollPane> testResultTabs = new HashMap<>();

    private JTabbedPane tabs;
    private Tree tree;
    private boolean testsFinished;

    public CyBenchResultTreeConsoleView(@NotNull Project project) {
        this.project = project;
        $$$setupUI$$$();
        initialize();
        tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                Object lastPathComponent = e.getPath().getLastPathComponent();
                if (lastPathComponent instanceof Nodes.BenchmarkTestNode && testsFinished) {
                    tabs.setSelectedComponent(testResultTabs.get(((Nodes.BenchmarkTestNode) lastPathComponent).getUserObject()));
                }
                if (lastPathComponent instanceof Nodes.BenchmarkRootNode) {
                    tabs.setSelectedIndex(0);
                }
            }
        });
    }

    public void initialize() {

        consoleView = new ConsoleViewImpl(project, /*viewer=*/true);
        tabs.remove(0);
        tabs.add("console", consoleView.getComponent());
        tree.setModel(new DefaultTreeModel(new Nodes.BenchmarkRootNode("CyBenchBenchmark")));
        UIUtil.putClientProperty(tree, AnimatedIcon.ANIMATION_IN_RENDERER_ALLOWED, true);
        tree.setCellRenderer(new ColoredTreeCellRenderer() {
            @Override
            public void customizeCellRenderer(@NotNull JTree jTree, Object o, boolean b, boolean b1, boolean b2, int i, boolean b3) {
                setIcon(new AnimatedIcon.Default());
                if (o instanceof DefaultMutableTreeNode) {
                    append(String.valueOf(((DefaultMutableTreeNode) o).getUserObject()));
                }
            }
        });
        Disposer.register(project, consoleView);
    }

    @NotNull
    public Project getProject() {
        return project;
    }

    @Override
    public JComponent getComponent() {
        // Make sure the console component is initialized
        consoleView.getComponent();

        // But return the root panel for this view
        return rootPanel;
    }

    public void clear() {
        consoleViewEntries.clear();
        consoleView.clear();
    }

    public void print(@NotNull String message, @NotNull ConsoleViewContentType contentType) {
        String[] messageLines = StringUtil.splitByLinesKeepSeparators(message);
        for (String messageLine : messageLines) {
            print(messageLine, contentType, true);
        }
    }

    public void print(@NotNull String message, @NotNull ConsoleViewContentType contentType, boolean allowFiltering) {
        String[] messageLines = StringUtil.splitByLinesKeepSeparators(message);
        for (String messageLine : messageLines) {
            printRaw(messageLine, contentType, allowFiltering);
        }
    }

    private void printRaw(@NotNull String messageLine, @NotNull ConsoleViewContentType contentType, boolean allowFiltering) {
        ConsoleViewEntry consoleViewEntry = new ConsoleViewEntry(messageLine, contentType, allowFiltering);
        consoleViewEntries.add(consoleViewEntry);
        printLogEntry(consoleViewEntry);
    }

    @SuppressWarnings("unused")
    public void printHyperlink(@NotNull String message, @NotNull HyperlinkInfo hyperlinkInfo) {
        printHyperlink(message, hyperlinkInfo, true);
    }

    public void printHyperlink(@NotNull String message, @NotNull HyperlinkInfo hyperlinkInfo, boolean allowFiltering) {
        String[] messageLines = StringUtil.splitByLinesKeepSeparators(message);
        for (String messageLine : messageLines) {
            ConsoleViewEntry consoleViewEntryEntry = new ConsoleViewEntry(messageLine, hyperlinkInfo, allowFiltering);
            consoleViewEntries.add(consoleViewEntryEntry);
            printLogEntry(consoleViewEntryEntry);
        }
    }

    private void updateConsoleView() {
        consoleView.clear();

        for (ConsoleViewEntry consoleViewEntryEntry : consoleViewEntries) {
            printLogEntry(consoleViewEntryEntry);
        }
    }

    private void printLogEntry(@NotNull ConsoleViewEntry consoleViewEntryEntry) {

        String message = consoleViewEntryEntry.getMessage();

        ConsoleViewContentType contentType = consoleViewEntryEntry.getContentType();
        HyperlinkInfo hyperlinkInfo = consoleViewEntryEntry.getHyperlinkInfo();
        if (hyperlinkInfo != null) {
            consoleView.printHyperlink(message, hyperlinkInfo);
        } else {
            consoleView.print(message, contentType);
        }

    }

    // Straight delegation to the real console view

    public void scrollTo(int row) {
        consoleView.scrollTo(row);
    }

    @Override
    public void attachToProcess(ProcessHandler processHandler) {
        consoleView.attachToProcess(processHandler);
    }

    @Override
    public boolean isOutputPaused() {
        return consoleView.isOutputPaused();
    }

    @Override
    public void setOutputPaused(boolean paused) {
        consoleView.setOutputPaused(paused);
    }

    @Override
    public boolean hasDeferredOutput() {
        return consoleView.hasDeferredOutput();
    }

    @Override
    public void performWhenNoDeferredOutput(Runnable runnable) {
        consoleView.performWhenNoDeferredOutput(runnable);
    }

    @Override
    public void setHelpId(String helpId) {
        consoleView.setHelpId(helpId);
    }

    @Override
    public void addMessageFilter(Filter filter) {
        consoleView.addMessageFilter(filter);
    }

    @Override
    public int getContentSize() {
        return consoleView.getContentSize();
    }

    @Override
    public boolean canPause() {
        return consoleView.canPause();
    }

    @NotNull
    @Override
    public AnAction[] createConsoleActions() {
        return consoleView.createConsoleActions();
    }

    @Override
    public void allowHeavyFilters() {
        consoleView.allowHeavyFilters();
    }

    @Override
    public JComponent getPreferredFocusableComponent() {
        return consoleView.getPreferredFocusableComponent();
    }

    @Override
    public void dispose() {
        consoleView.dispose();
    }

    public JTree getTree() {
        return tree;
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        rootPanel = new JPanel();
        rootPanel.setLayout(new BorderLayout(0, 0));
        consoleViewPanel = new JPanel();
        consoleViewPanel.setLayout(new BorderLayout(0, 0));
        rootPanel.add(consoleViewPanel, BorderLayout.CENTER);
        tree = new Tree();
        consoleViewPanel.add(tree, BorderLayout.WEST);
        tabs.setTabPlacement(1);
        consoleViewPanel.add(tabs, BorderLayout.CENTER);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        tabs.addTab("Untitled", panel1);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }

    public void generateResultTabs() {
        this.testsFinished = true;

        //getTree().getModel().




        ResultFileParser resultFileParser = new NodeAndTabFiller(testResultTabs, tabs);

        try {
            resultFileParser.parse(new File(project.getBasePath() + File.separator + "reports" + File.separator + "report.json"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void createUIComponents() {
        tabs = new JBTabbedPane();

        //do not show tab header; navigation enabled with tree
        tabs.setUI(new DarculaTabbedPaneUI() {
            @Override
            protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
                return 0;
            }
        });

    }

    private static class ConsoleViewEntry {
        private final String message;
        private final ConsoleViewContentType contentType;
        private final HyperlinkInfo hyperlinkInfo;
        private final boolean allowFiltering;

        public ConsoleViewEntry(@NotNull String message, @NotNull ConsoleViewContentType contentType, @Nullable HyperlinkInfo hyperlinkInfo, boolean allowFiltering) {
            this.message = message;
            this.contentType = contentType;
            this.hyperlinkInfo = hyperlinkInfo;
            this.allowFiltering = allowFiltering;
        }

        public ConsoleViewEntry(@NotNull String message, @NotNull ConsoleViewContentType contentType, boolean allowFiltering) {
            this(message, contentType, null, allowFiltering);
        }

        public ConsoleViewEntry(@NotNull String message, @NotNull HyperlinkInfo hyperlinkInfo, boolean allowFiltering) {
            this(message, ConsoleViewContentType.NORMAL_OUTPUT, hyperlinkInfo, allowFiltering);
        }

        @NotNull
        public String getMessage() {
            return message;
        }

        @NotNull
        public ConsoleViewContentType getContentType() {
            return contentType;
        }

        @Nullable
        public HyperlinkInfo getHyperlinkInfo() {
            return hyperlinkInfo;
        }

        public boolean isAllowFiltering() {
            return allowFiltering;
        }
    }

}
