package com.gocypher.cybench.toolWindow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gocypher.cybench.NodeAndTabFiller;
import com.gocypher.cybench.ResultFileParser;
import com.gocypher.cybench.ResultJPanel;
import com.gocypher.cybench.launcher.model.BenchmarkOverviewReport;
import com.gocypher.cybench.launcher.model.BenchmarkReport;
import com.gocypher.cybench.utils.Nodes;
import com.intellij.ide.ui.laf.darcula.ui.DarculaTabbedPaneUI;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.ui.components.JBTabbedPane;
import org.codehaus.jettison.json.JSONException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CyBenchToolWindow {
    private JPanel toolWindowContent;
    private JTree reportList;
    private JButton refresh;
    private JSplitPane splitPane;
    private JTabbedPane tabs;
    private HashMap<String, JScrollPane> testResultTabs = new HashMap<>();


    public CyBenchToolWindow(ToolWindow toolWindow) {
        $$$setupUI$$$();
        refresh.addActionListener(actionEvent -> refreshReports());
        reportList.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
        reportList.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                Object selected = e.getPath().getLastPathComponent();
                if (selected instanceof Nodes.BenchmarkReportFileNode) {
                    populateNodeFromFile((Nodes.BenchmarkReportFileNode) selected, ((Nodes.BenchmarkReportFileNode) selected).getFile());
                }
                if (selected instanceof Nodes.BenchmarkClassNode) {

                }
                if (selected instanceof Nodes.BenchmarkTestNode) {
                    tabs.setSelectedComponent(testResultTabs.get(((Nodes.BenchmarkTestNode) selected).getUserObject()));

                }
            }
        });


        refreshReports();
        ((ToolWindowEx) toolWindow).setAdditionalGearActions(new MyActionGroup());
        //Module module = ModuleManager.getInstance(project).getModules()[0];
    }

    public static void main(String[] args) {
        JDialog d = new JDialog();
        d.setModal(true);
        CyBenchToolWindow dialog = new CyBenchToolWindow(null);

        d.setContentPane(dialog.getContent());
        d.pack();
        d.setVisible(true);
        System.exit(0);
    }

    private void populateNodeFromFile(Nodes.BenchmarkReportFileNode selected, File file) {
        ObjectMapper mapper = new ObjectMapper();
            tabs = new JBTabbedPane();
            //do not show tab header; navigation enabled with tree
            tabs.setUI(new DarculaTabbedPaneUI() {
                @Override
                protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
                    return 0;
                }
            });
            testResultTabs = new HashMap<>();

            new NodeAndTabFiller(testResultTabs, tabs) {
                @Override
                public void onTest(String name) {
                    Nodes.BenchmarkClassNode newChild = new Nodes.BenchmarkClassNode(name);
                    selected.add(newChild);

                    newChild.add(new Nodes.BenchmarkTestNode(name));
                    super.onTest(name);
                }
            };

    }

    private void refreshReports() {
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
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        splitPane.setRightComponent(panel1);
        reportList = new JTree();
        reportList.setRootVisible(false);
        splitPane.setLeftComponent(reportList);
        refresh = new JButton();
        refresh.setText("Button");
        toolWindowContent.add(refresh, BorderLayout.SOUTH);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return toolWindowContent;
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
        // TODO: place custom component creation code here
    }


    private class RefreshAction extends AnAction {
        @Override
        public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
            refreshReports();
        }
    }

    private class MyActionGroup extends ActionGroup {
        @NotNull
        @Override
        public AnAction[] getChildren(@Nullable AnActionEvent anActionEvent) {
            return new RefreshAction[0];
        }
    }
}
