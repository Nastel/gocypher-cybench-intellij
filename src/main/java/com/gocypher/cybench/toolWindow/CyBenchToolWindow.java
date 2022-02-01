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

package com.gocypher.cybench.toolWindow;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;

import org.codehaus.jettison.json.JSONException;
import org.jetbrains.annotations.NotNull;

import com.gocypher.cybench.launcher.model.BenchmarkReport;
import com.gocypher.cybench.toolWindow.factories.ToolWindowFactory;
import com.gocypher.cybench.utils.CyBenchTreeCellRenderer;
import com.gocypher.cybench.utils.NodeAndTabFiller;
import com.gocypher.cybench.utils.Nodes;
import com.gocypher.cybench.utils.ResultFileParser;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.treeStructure.Tree;

public class CyBenchToolWindow {

    private File file;
    private JPanel toolWindowContent;
    private Tree reportList;
    private JSplitPane splitPane;
    private JTabbedPane tabs;
    private HashMap<String, JScrollPane> testResultTabs = new HashMap<>();

    public CyBenchToolWindow(ToolWindow toolWindow, File file) {
        $$$setupUI$$$();
        createUIComponents();
        this.file = file;
        if (file == null) {
            this.file = getDefaultReport();
        }
        if (!this.file.exists()) {
            // score is not known before it finishes
            File[] list = file.getParentFile().listFiles(
                    (dir, name) -> name.startsWith(file.getName().substring(0, file.getName().lastIndexOf('.')))
                            && name.endsWith("cybench")

            );
            if (list.length > 0) {
                this.file = list[0];
            }
        }

        reportList.setModel(new DefaultTreeModel(new Nodes.BenchmarkRootNode("CyBench report")));
        reportList.setCellRenderer(new CyBenchTreeCellRenderer());
        reportList.setRootVisible(false);
        reportList.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                Object selected = e.getPath().getLastPathComponent();

                if (selected instanceof Nodes.BenchmarkClassNode) {
                    if (tabs.getSelectedIndex() == 0) {
                        tabs.setSelectedIndex(1);
                    } else {
                        tabs.setSelectedIndex(0);
                    }
                }
                if (selected instanceof Nodes.BenchmarkTestNode) {
                    selectActualReport(((Nodes.BenchmarkTestNode) selected).getFullyQualifiedName());

                }
            }
        });

        populateNodeFromFile();

    }

    public static void main(String[] args) {
        JDialog d = new JDialog();
        d.setModal(true);
        CyBenchToolWindow dialog = new CyBenchToolWindow(null, null);

        d.setContentPane(dialog.getContent());
        d.pack();
        d.setVisible(true);
        System.exit(0);
    }

    public static void activateReportView(File file, JPanel toolWindowContent, String selectReport, Project project) {
        ToolWindow cyBench_report = ToolWindowManager.getInstance(project).getToolWindow("CyBench Report");
        ApplicationManager.getApplication().invokeLater(() -> cyBench_report.activate(null));

        if (!ToolWindowFactory.loaded.containsKey(file)) {
            CyBenchToolWindow myToolWindow = new CyBenchToolWindow(cyBench_report, file);

            ToolWindowFactory.addReportView(cyBench_report, myToolWindow);
            if (selectReport != null) {
                myToolWindow.selectActualReport(selectReport);
            }
        } else {
            ApplicationManager.getApplication().invokeLater(
                    () -> cyBench_report.getContentManager().setSelectedContent(ToolWindowFactory.loaded.get(file)));
            if (selectReport != null) {
                ToolWindowFactory.loadedWindows.get(file).selectActualReport(selectReport);
            }
        }

    }

    public static void noop() {

    }

    @NotNull
    private File getDefaultReport() {
        File reportsDir = new File(ProjectUtil.guessCurrentProject(getContent()).getBasePath() + "/reports/");
        File[] list = reportsDir.listFiles((dir, name) -> name.endsWith(".cybench"));

        if (reportsDir.exists() && list.length >= 1) {
            return list[0];
        }
        // try {
        // return new File(getClass().getClassLoader().getResource("sample_report.cybench").toURI());
        // } catch (URISyntaxException e) {
        return null;
        // } //TODO load sample
    }

    public void selectActualReport(String component) {
        tabs.remove(3);
        tabs.addTab("Benchmark Details", testResultTabs.get(component));
        tabs.setSelectedIndex(3);
    }

    private void populateNodeFromFile() {

        ResultFileParser resultFileParser = new NodeAndTabFiller(testResultTabs, tabs) {
            @Override
            public void onTest(BenchmarkReport report) {
                String name = report.getName();
                Nodes.addClass(name.substring(0, name.lastIndexOf('.')), reportList);
                Nodes.addTest(name, reportList);
                super.onTest(report);
            }
        };

        try {
            resultFileParser.parse(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public JPanel getContent() {
        return toolWindowContent;
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer >>> IMPORTANT!! <<< DO NOT edit this method OR call it in your
     * code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        toolWindowContent = new JPanel();
        toolWindowContent.setLayout(new BorderLayout(0, 0));
        splitPane = new JSplitPane();
        toolWindowContent.add(splitPane, BorderLayout.CENTER);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return toolWindowContent;
    }

    private void createUIComponents() {
        reportList = new Tree();
        // reportList.setRootVisible(false);

        JBScrollPane scroll = new JBScrollPane(reportList, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_ALWAYS);

        splitPane.setLeftComponent(scroll);

        tabs = new JBTabbedPane(JBTabbedPane.BOTTOM);

        // do not show tab header; navigation enabled with tree
        // TODO: place custom component creation code here
        splitPane.setRightComponent(tabs);
    }

    public File getFile() {
        return file;
    }

}
