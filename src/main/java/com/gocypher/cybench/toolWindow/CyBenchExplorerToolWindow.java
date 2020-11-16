package com.gocypher.cybench.toolWindow;

import com.gocypher.cybench.toolWindow.factories.BrowseReportsToolWindowFactory;
import com.gocypher.cybench.utils.CyBenchTreeCellRenderer;
import com.gocypher.cybench.utils.Nodes;
import com.gocypher.cybench.viewPanels.CBTable;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.dualView.TreeTableView;
import com.intellij.ui.table.JBTable;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.ui.treeStructure.treetable.ListTreeTableModelOnColumns;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.xml.ui.BooleanColumnInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class CyBenchExplorerToolWindow {

    private static JPanel toolWindowContent;

    private File reportsFolder;
    private JBTable reportList;


    public CyBenchExplorerToolWindow(ToolWindow toolWindow) {

        initComponents();

    }

    private void initComponents() {
        toolWindowContent = new JPanel(new BorderLayout(0, 0));

        reportList = new JBTable(new DefaultTableModel());
        ((DefaultTableModel) reportList.getModel()).setColumnIdentifiers(new String[]{"Benchmark Name", "Score", "Timestamp", "Actual_File_Hidden"});
        reportList.removeColumn(reportList.getColumnModel().getColumn(3));

        reportList.getSelectionModel().addListSelectionListener(x -> {
            int selectionIndex =x.getFirstIndex();
            File valueAt = (File) reportList.getModel().getValueAt(selectionIndex-1, 3);
            CyBenchToolWindow.activateReportView(valueAt, CyBenchExplorerToolWindow.this.toolWindowContent, null);
        });

        toolWindowContent.add(new JScrollPane(reportList), BorderLayout.CENTER);

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
        for(int i = reportList.getModel().getRowCount()-1; i>0; i--) {
            ((DefaultTableModel) reportList.getModel()).removeRow(i);
        }
        Project project = ProjectUtil.guessCurrentProject(toolWindowContent);
        if (reportsFolder == null) {
            reportsFolder = new File(project.getBasePath() + File.separator + "reports");
        }
        if (reportsFolder.exists()) {
            File[] files = reportsFolder.listFiles(f -> f.getName().endsWith(".cybench"));
            Arrays.asList(files).stream().forEach(f -> {
                ((DefaultTableModel) reportList.getModel()).addRow(getRow(f));
            });
        }
    }

    public static void refreshToolWindow() {
        ToolWindow cyBench_explorer = ToolWindowManager.getInstance(ProjectUtil.guessCurrentProject(toolWindowContent)).getToolWindow("CyBench explorer");
        cyBench_explorer.activate(() -> CyBenchToolWindow.noop());
        BrowseReportsToolWindowFactory.myToolWindow.refreshReports();

    }

    private Object[] getRow(File f) {
        Object[] row;
        String name = f.getName();
        Pattern pattern = Pattern.compile("(?<rName>.*)-(?<timestamp>[0-9]*)-(?<score>[0-9.,]*).cybench");
        Matcher matcher = pattern.matcher(name);
        if (matcher.matches()) {
            String timestamp;
            try {
                timestamp = new SimpleDateFormat("yyy-MM-dd HH:mm:ss").format(Long.parseLong(matcher.group("timestamp")));
            } catch (Exception e) {
                timestamp = "";
            }

            row = new Object[]{matcher.group("rName"), matcher.group("score"), timestamp, f};
        } else {
            row = new Object[]{name, "", "", f};
        }


        return row;

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
