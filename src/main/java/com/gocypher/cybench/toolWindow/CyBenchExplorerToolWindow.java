package com.gocypher.cybench.toolWindow;

import com.gocypher.cybench.toolWindow.factories.ToolWindowFactory;
import com.gocypher.cybench.utils.CyBenchTreeCellRenderer;
import com.gocypher.cybench.utils.Nodes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.File;
import java.util.Arrays;

public class CyBenchExplorerToolWindow {

    private JPanel toolWindowContent;

    private Tree reportList;


    public CyBenchExplorerToolWindow(ToolWindow toolWindow) {

        initComponents();

    }

    private void initComponents() {
        toolWindowContent = new JPanel(new BorderLayout(0, 0));
        reportList = new Tree();
        reportList.setRootVisible(false);
        reportList.setCellRenderer(new CyBenchTreeCellRenderer());
        reportList.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                ToolWindow cyBench_report = ToolWindowManager.getInstance(ProjectUtil.guessCurrentProject(toolWindowContent)).getToolWindow("CyBench report");
                cyBench_report.activate(null);


                File file = new File(getReportDir().getAbsolutePath() + File.separator + e.getPath().getLastPathComponent());

                if (!ToolWindowFactory.loaded.containsKey(file)) {
                    CyBenchToolWindow myToolWindow = new CyBenchToolWindow(cyBench_report, file);

                    ToolWindowFactory.addReportView(cyBench_report, myToolWindow);
                } else {
                    cyBench_report.getContentManager().setSelectedContent(ToolWindowFactory.loaded.get(file));
                }
            }
        });

        toolWindowContent.add(reportList, BorderLayout.CENTER);

        refreshReports();
    }

    private File getReportDir() {
        return new File(ProjectUtil.guessCurrentProject(toolWindowContent).getBasePath() + "/reports");
    }


    private void refreshReports() {
        reportList.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
        Project project = ProjectUtil.guessCurrentProject(toolWindowContent);
        File file = new File(project.getBasePath() + File.separator + "reports");
        if (file.exists()) {
            File[] files = file.listFiles(f -> f.getName().endsWith(".json"));
            Arrays.asList(files).stream().forEach(f -> ((DefaultMutableTreeNode) (reportList.getModel()).getRoot()).add(new Nodes.BenchmarkReportFileNode(f.getName(), f)));
            ((DefaultTreeModel) reportList.getModel()).reload();
        }
    }


    public JPanel getContent() {
        return toolWindowContent;
    }

}
