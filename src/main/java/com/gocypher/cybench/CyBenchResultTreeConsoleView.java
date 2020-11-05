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

package com.gocypher.cybench;

import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.runner.ui.TestTreeRenderer;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.ide.ui.laf.darcula.ui.DarculaTabbedPaneUI;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.AnimatedIcon;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.UIUtil;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.codehaus.jettison.json.JSONException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

public class CyBenchResultTreeConsoleView implements ConsoleView {
    private final Project project;
    private final List<ConsoleViewEntry> consoleViewEntries = new LinkedList<ConsoleViewEntry>();
    JPanel consoleViewPanel;
    ConsoleViewImpl consoleView;
    private JPanel rootPanel;
    private HashMap<String, JScrollPane> testResultTabs = new HashMap<>();
    private Tree tree;
    private JTabbedPane tabs;
    private boolean testsFinished;

    public CyBenchResultTreeConsoleView(@NotNull Project project) {
        this.project = project;
        $$$setupUI$$$();
        initialize();
        tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                Object lastPathComponent = e.getPath().getLastPathComponent();
                if (lastPathComponent instanceof CyBenchMessageHandler.BenchmarkTestNode && testsFinished) {
                    tabs.setSelectedComponent(testResultTabs.get(((CyBenchMessageHandler.BenchmarkTestNode) lastPathComponent).getUserObject()));
                }
                if (lastPathComponent instanceof CyBenchMessageHandler.BenchmarkRootNode) {
                    tabs.setSelectedIndex(0);
                }
            }
        });
    }

    public void initialize() {

        consoleView = new ConsoleViewImpl(project, /*viewer=*/true);
        tabs.remove(0);
        tabs.add("console", consoleView.getComponent());
        tree.setModel(new DefaultTreeModel(new CyBenchMessageHandler.BenchmarkRootNode("CyBenchBenchmark")));
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
        final ResultJPanel[] currentTestPanel = {null};

        ResultFileParser resultFileParser = new ResultFileParser() {
            @Override
            public void onTestEnd(String name) {
                JBScrollPane jbScrollPane = new JBScrollPane(currentTestPanel[0], VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_ALWAYS);
                testResultTabs.put(name, jbScrollPane);

                tabs.add(name, jbScrollPane);
            }

            @Override
            public void onTest(String name) {
                ResultJPanel testResultPanel = new ResultJPanel();
                currentTestPanel[0] = testResultPanel;
            }

            @Override
            public void ontTestResultEntry(String key, String value, int index) {
                switch (key) {
                    case "name":
                        currentTestPanel[0].setName(value);
                        break;
                    case "score":
                        currentTestPanel[0].setScore(value);
                        break;
                    case "minScore":
                        currentTestPanel[0].setMin(value);
                        break;
                    case "maxScore":
                        currentTestPanel[0].setMax(value);
                        break;

                }
                GridBagConstraints cc = new GridBagConstraints();
                cc.gridy = index;

                JLabel testResKey = new JLabel(Utils.getKeyName(key), SwingConstants.LEFT);
                JLabel testResValue = new JLabel(Utils.convertNumToStringByLength(value), SwingConstants.RIGHT);
                JPanel testResultPanel = currentTestPanel[0].other;
                cc.gridx = 0;
                cc.anchor = GridBagConstraints.WEST;
                testResultPanel.add(testResKey, cc);
                cc.gridx = 1;
                cc.anchor = GridBagConstraints.EAST;
                testResultPanel.add(testResValue, cc);

            }
        };

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
