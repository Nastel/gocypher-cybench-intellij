package com.gocypher.cybench.toolWindow;

import com.gocypher.cybench.utils.CyBenchTreeCellRenderer;
import com.gocypher.cybench.utils.Nodes;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.dualView.TreeTableView;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.ui.treeStructure.treetable.ListTreeTableModelOnColumns;
import com.intellij.ui.treeStructure.treetable.TreeTable;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.xml.ui.BooleanColumnInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private File reportsFolder;
    private TreeTableView treeTable;


    public CyBenchExplorerToolWindow(ToolWindow toolWindow) {

        initComponents();

    }

    private void initComponents() {
        toolWindowContent = new JPanel(new BorderLayout(0, 0));
        reportList = new Tree();

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) reportList.getModel().getRoot();
        treeTable =
                new TreeTableView(new ListTreeTableModelOnColumns(root, new ColumnInfo[]{new BooleanColumnInfo("A"), new BooleanColumnInfo("A")}));
        root.add(new Nodes.BenchmarkTestNode("a"));


        reportList.setRootVisible(false);
        reportList.setCellRenderer(new CyBenchTreeCellRenderer());
        reportList.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                CyBenchToolWindow.activateReportView(new File(getReportDir().getAbsolutePath() + File.separator + e.getPath().getLastPathComponent()), CyBenchExplorerToolWindow.this.toolWindowContent, null);

            }
        });


        toolWindowContent.add(reportList, BorderLayout.CENTER);
        toolWindowContent.add(treeTable, BorderLayout.SOUTH);

        ActionToolbar cb_explorer_toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLWINDOW_CONTENT, new ActionGroup() {
            @NotNull
            @Override
            public AnAction[] getChildren(@Nullable AnActionEvent anActionEvent) {
                return new AnAction[]{new RefreshAction(), new SelectReportFolderAction()};
            }
        }, false);
        cb_explorer_toolbar.setOrientation(SwingConstants.HORIZONTAL);
        cb_explorer_toolbar.setTargetComponent(toolWindowContent);
        toolWindowContent.add(cb_explorer_toolbar.getComponent(), BorderLayout.NORTH);

        refreshReports();
    }

    private File getReportDir() {
        return new File(ProjectUtil.guessCurrentProject(toolWindowContent).getBasePath() + "/reports");
    }


    private void refreshReports() {
        reportList.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
        Project project = ProjectUtil.guessCurrentProject(toolWindowContent);
        if (reportsFolder == null) {
            reportsFolder = new File(project.getBasePath() + File.separator + "reports");
        }
        if (reportsFolder.exists()) {
            File[] files = reportsFolder.listFiles(f -> f.getName().endsWith(".json"));
            Arrays.asList(files).stream().forEach(f -> ((DefaultMutableTreeNode) (reportList.getModel()).getRoot()).add(new Nodes.BenchmarkReportFileNode(f.getName(), f)));
            //Arrays.asList(files).stream().forEach(f -> ((DefaultMutableTreeNode) treeTable.getTree().getModel().getRoot()).add(new Nodes.BenchmarkReportFileNode(f.getName(), f)));
            ((DefaultTreeModel) reportList.getModel()).reload();
        }
    }


    public JPanel getContent() {
        return toolWindowContent;
    }

    private void setReportFolder(VirtualFile virtualFile) {
        String path = virtualFile.getPath();
        this.reportsFolder = new File(path);
        refreshReports();

    }

    private class SelectReportFolderAction extends AnAction {
        public SelectReportFolderAction() {
            getTemplatePresentation().setIcon(AllIcons.Actions.Menu_open);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
            FileChooserDescriptor descriptor = new FileChooserDescriptor(false, true, false, false, false, false);
            descriptor.setTitle("Choose reports folder");
            descriptor.setRoots(ProjectUtil.guessCurrentProject(toolWindowContent).getBaseDir());
            FileChooser.chooseFile(descriptor, null, null, CyBenchExplorerToolWindow.this::setReportFolder);
        }
    }


    private class RefreshAction extends AnAction {

        public RefreshAction() {
            getTemplatePresentation().setIcon(AllIcons.Actions.ForceRefresh);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
            refreshReports();
        }
    }
}
