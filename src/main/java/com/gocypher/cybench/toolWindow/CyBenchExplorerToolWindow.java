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

import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gocypher.cybench.toolWindow.factories.BrowseReportsToolWindowFactory;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.table.JBTable;

public class CyBenchExplorerToolWindow {

    private static JPanel toolWindowContent;
    private final Project project;

    private File reportsFolder;
    private JBTable reportList;
    private boolean reloading;

    public CyBenchExplorerToolWindow(ToolWindow toolWindow, Project project) {
        initComponents();
        this.project = project;
    }

    public static void refreshToolWindow(Project project) {
        ToolWindow cyBench_explorer = ToolWindowManager.getInstance(project).getToolWindow("CyBench explorer");
        if (cyBench_explorer != null) {
            ApplicationManager.getApplication().invokeLater(() -> cyBench_explorer.activate(null));
        }
        BrowseReportsToolWindowFactory.myToolWindow.refreshReports();

    }

    private void initComponents() {
        toolWindowContent = new JPanel(new BorderLayout(0, 0));

        reportList = new JBTable(new DefaultTableModel() {
            private static final long serialVersionUID = 5383002873310574249L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        ((DefaultTableModel) reportList.getModel())
                .setColumnIdentifiers(new String[] { "Benchmark Name", "Score", "Timestamp", "Actual_File_Hidden" });
        reportList.removeColumn(reportList.getColumnModel().getColumn(3));

        reportList.getSelectionModel().addListSelectionListener(x -> {

            if (x.getValueIsAdjusting() || reloading) {
                return;
            }

            int selectionIndex = x.getLastIndex();
            File valueAt = (File) reportList.getModel().getValueAt(selectionIndex, 3);
            ApplicationManager.getApplication()
                    .invokeLater(() -> CyBenchToolWindow.activateReportView(valueAt, toolWindowContent, null, project));

            reportList.getSelectionModel().clearSelection();
        });

        toolWindowContent.add(new JScrollPane(reportList), BorderLayout.CENTER);

        ActionToolbar cb_explorer_toolbar = ActionManager.getInstance()
                .createActionToolbar(ActionPlaces.TOOLWINDOW_CONTENT, new ActionGroup() {
                    @NotNull
                    @Override
                    public AnAction[] getChildren(@Nullable AnActionEvent anActionEvent) {
                        return new AnAction[] { new RefreshAction(), new SelectReportFolderAction() };
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
        reloading = true;
        for (int i = reportList.getModel().getRowCount() - 1; i > -1; i--) {
            ((DefaultTableModel) reportList.getModel()).removeRow(i);
        }
        Project project = ProjectUtil.guessCurrentProject(toolWindowContent);
        if (reportsFolder == null) {
            reportsFolder = new File(project.getBasePath() + File.separator + "reports");
        }
        if (reportsFolder.exists()) {
            File[] files = reportsFolder.listFiles(f -> f.getName().endsWith(".cybench"));
            Arrays.forEach(f -> ((DefaultTableModel) reportList.getModel()).addRow(getRow(f)));
        }
        reloading = false;
    }

    private Object[] getRow(File f) {
        Object[] row;
        String name = f.getName();
        Pattern pattern = Pattern.compile("(?<rName>.*)-(?<timestamp>[0-9]*)-(?<score>[0-9.,]*).cybench");
        Matcher matcher = pattern.matcher(name);
        if (matcher.matches()) {
            String timestamp;
            try {
                timestamp = new SimpleDateFormat("yyy-MM-dd HH:mm:ss")
                        .format(Long.parseLong(matcher.group("timestamp")));
            } catch (Exception e) {
                timestamp = "";
            }

            row = new Object[] { matcher.group("rName"), matcher.group("score"), timestamp, f };
        } else {
            row = new Object[] { name, "", "", f };
        }

        return row;

    }

    public JPanel getContent() {
        return toolWindowContent;
    }

    private void setReportFolder(VirtualFile virtualFile) {
        String path = virtualFile.getPath();
        reportsFolder = new File(path);
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
            // descriptor.setRoots(ProjectUtil.guessCurrentProject(toolWindowContent).getBaseDir());
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
