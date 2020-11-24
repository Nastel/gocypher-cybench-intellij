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

package com.gocypher.cybench.runConfiguration;

import com.gocypher.cybench.toolWindow.CyBenchExplorerToolWindow;
import com.gocypher.cybench.toolWindow.CyBenchToolWindow;
import com.gocypher.cybench.utils.NodeAndTabFiller;
import com.gocypher.cybench.utils.Nodes;
import com.gocypher.cybench.utils.ResultFileParser;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.icons.AllIcons;
import com.intellij.ide.ui.laf.darcula.ui.DarculaTabbedPaneUI;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.wm.ToolWindowManager;
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
    protected ConsoleViewImpl consoleView;
    JPanel consoleViewPanel;
    private JPanel rootPanel;

    private Tree tree;
    private JPanel tabs;
    private boolean testsFinished;
    private File reportFile;

    public CyBenchResultTreeConsoleView(@NotNull Project project) {
        this.project = project;
        $$$setupUI$$$();
        initialize();
        tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                Object lastPathComponent = e.getPath().getLastPathComponent();
                if (lastPathComponent instanceof Nodes.BenchmarkTestNode && testsFinished) {

                    CyBenchToolWindow.activateReportView(reportFile, null, String.valueOf(((Nodes.BenchmarkTestNode) lastPathComponent).getUserObject()));


                }

            }
        });
    }

    public void initialize() {

        consoleView = new ConsoleViewImpl(project, /*viewer=*/true);
        tree.setModel(new DefaultTreeModel(new Nodes.BenchmarkRootNode("CyBenchBenchmark")));
        tree.setCellRenderer(new ColoredTreeCellRenderer() {
            @Override
            public void customizeCellRenderer(@NotNull JTree jTree, Object o, boolean b, boolean b1, boolean b2, int i, boolean b3) {
                setIcon(new AnimatedIcon.Default());
                if (o instanceof DefaultMutableTreeNode) {
                    append(String.valueOf(((DefaultMutableTreeNode) o).getUserObject()));
                }
            }
        });
        tabs.add(consoleView.getComponent());
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
        rootPanel = new JPanel();
        rootPanel.setLayout(new BorderLayout(0, 0));
        consoleViewPanel = new JPanel();
        consoleViewPanel.setLayout(new BorderLayout(0, 0));
        rootPanel.add(consoleViewPanel, BorderLayout.CENTER);
        tree = new Tree();
        consoleViewPanel.add(tree, BorderLayout.WEST);
        tabs = new JPanel();
        tabs.setLayout(new BorderLayout(0, 0));
        consoleViewPanel.add(tabs, BorderLayout.CENTER);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }

    public void onBenchmarkFinished() {
        this.testsFinished = true;
        ((ColoredTreeCellRenderer) tree.getCellRenderer()).setIcon(AllIcons.RunConfigurations.TestPassed);
        tree.updateUI();
        CyBenchToolWindow.activateReportView(reportFile, this.consoleView, null);
        CyBenchExplorerToolWindow.refreshToolWindow();

    }

    private void createUIComponents() {

    }

    public void setReportFile(String reportFileName) {
        this.reportFile = new File(ProjectUtil.guessCurrentProject(null).getBasePath() + File.separator + "reports" + File.separator + reportFileName);
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
