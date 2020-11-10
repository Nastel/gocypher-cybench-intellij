package com.gocypher.cybench.utils;

import com.gocypher.cybench.viewPanels.HWJPanel;
import com.gocypher.cybench.viewPanels.JVMJPanel;
import com.gocypher.cybench.viewPanels.ResultJPanel;
import com.gocypher.cybench.launcher.model.BenchmarkReport;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

public class NodeAndTabFiller extends ResultFileParser {
    private ResultJPanel currentTestPanel;
    private final HashMap<String, JScrollPane> testResultTabs;
    private final JTabbedPane tabs;


    public NodeAndTabFiller(HashMap<String, JScrollPane> testResultTabs, JTabbedPane tabs) {
        this.testResultTabs = testResultTabs;
        this.tabs = tabs;
    }

    @Override
    protected void onFinished() {
        Map.Entry<String, JScrollPane> next = testResultTabs.entrySet().iterator().next();
        tabs.addTab(next.getKey(), next.getValue());
    }

    @Override
    protected void onEnviromentEntries(Map<String, Object> environment) {
        HWJPanel hwjPanel = new HWJPanel();
        tabs.add("HW properties", hwjPanel);
        int[] idx = { 0 };
        environment.forEach((k, v)-> hwjPanel.table.getModel().addRow(getRowData(k,v)));
    }

    @Override
    protected void onJVMEnties(Map<String, Object> jvmSettings) {
        JVMJPanel panel = new JVMJPanel();
        tabs.add("JVM properties", panel);
        int[] idx = { 0 };
        jvmSettings.forEach((k, v)-> panel.table.getModel().addRow(getRowData(k,v)));
    }

    @Override
    public void onTestEnd(BenchmarkReport report) {
        JBScrollPane jbScrollPane = new JBScrollPane(currentTestPanel, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_ALWAYS);
        testResultTabs.put(report.getName(), jbScrollPane);

    }

    @Override
    public void onTest(BenchmarkReport report) {
        ResultJPanel testResultPanel = new ResultJPanel();
        currentTestPanel = testResultPanel;


    }

    @Override
    public void ontTestResultEntry(String key, String value, int index) {
        switch (key) {
            case "name":
                currentTestPanel.setName(value);
                break;
            case "score":
                currentTestPanel.setScore(value);
                break;
            case "minScore":
                currentTestPanel.setMin(value);
                break;
            case "maxScore":
                currentTestPanel.setMax(value);
                break;

        }

        if (key.length() > 2 && key.substring(0,2).toUpperCase().equals("GC")) {
            ((DefaultTableModel)currentTestPanel.gcTable.getModel()).addRow(getRowData(key, value));

        }
        ((DefaultTableModel)currentTestPanel.allResults.getModel()).addRow(getRowData(key, value));
        //addKeyValue(key, value, index, currentTestPanel.other);

    }

    @NotNull
    private static String[] getRowData(String key, Object value) {
        return new String[]{Utils.getKeyName(key), Utils.convertNumToStringByLength(String.valueOf(value))};
    }

    private static void addKeyValue(String key, String value, int index, JPanel currentTestPanel) {
        GridBagConstraints cc = new GridBagConstraints();
        cc.gridy = index;

        JLabel testResKey = new JLabel(Utils.getKeyName(key), SwingConstants.LEFT);
        JLabel testResValue = new JLabel(Utils.convertNumToStringByLength(value), SwingConstants.RIGHT);
        JPanel testResultPanel =currentTestPanel ;
        cc.gridx = 0;
        cc.anchor = GridBagConstraints.WEST;
        testResultPanel.add(testResKey, cc);
        cc.gridx = 1;
        cc.anchor = GridBagConstraints.EAST;
        testResultPanel.add(testResValue, cc);
    }
}
