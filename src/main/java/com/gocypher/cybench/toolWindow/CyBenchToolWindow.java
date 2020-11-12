package com.gocypher.cybench.toolWindow;

import com.gocypher.cybench.toolWindow.factories.ToolWindowFactory;
import com.gocypher.cybench.utils.*;
import com.gocypher.cybench.launcher.model.BenchmarkReport;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.ui.treeStructure.treetable.ListTreeTableModelOnColumns;
import com.intellij.ui.treeStructure.treetable.TreeTable;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.xml.ui.BooleanColumnInfo;
import org.codehaus.jettison.json.JSONException;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

public class CyBenchToolWindow {

    private File file;
    private JPanel toolWindowContent;
    private Tree reportList;
    private TreeTable treeTable;
    private JSplitPane splitPane;
    private JTabbedPane tabs;
    private JTabbedPane hv_jvm_result;
    private HashMap<String, JScrollPane> testResultTabs = new HashMap<>();


    public CyBenchToolWindow(ToolWindow toolWindow, File file) {
        $$$setupUI$$$();
        createUIComponents();
        this.file = file;
        if (file == null) {
            this.file = new File(ProjectUtil.guessCurrentProject(getContent()).getBasePath() + "/reports/report.json");
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

    public void selectActualReport(String component) {
        tabs.remove(3);
        tabs.addTab("Benchmark Details", CyBenchToolWindow.this.testResultTabs.get(component));
        tabs.setSelectedIndex(3);
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

    public static void activateReportView(File file, JPanel toolWindowContent, String selectReport) {
        ToolWindow cyBench_report = ToolWindowManager.getInstance(ProjectUtil.guessCurrentProject(toolWindowContent)).getToolWindow("CyBench report");
        cyBench_report.activate(() -> noop());


        if (!ToolWindowFactory.loaded.containsKey(file)) {
            CyBenchToolWindow myToolWindow = new CyBenchToolWindow(cyBench_report, file);

            ToolWindowFactory.addReportView(cyBench_report, myToolWindow);
            if (selectReport != null) {
                myToolWindow.selectActualReport(selectReport);
            }
        } else {
            cyBench_report.getContentManager().setSelectedContent(ToolWindowFactory.loaded.get(file));
            if (selectReport != null) {
                ToolWindowFactory.loadedWindows.get(file).selectActualReport(selectReport);
            }
        }


    }

    public static void noop() {

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
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
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
        //reportList.setRootVisible(false);

        JBScrollPane scroll = new JBScrollPane(reportList, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_ALWAYS);

        splitPane.setLeftComponent(scroll);


        tabs = new JBTabbedPane(JBTabbedPane.BOTTOM);

        //do not show tab header; navigation enabled with tree
        // TODO: place custom component creation code here
        splitPane.setRightComponent(tabs);
    }

    public File getFile() {
        return file;
    }


}
