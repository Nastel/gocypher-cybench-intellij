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

package com.gocypher.cybench.utils;

import static com.gocypher.cybench.toolWindow.CyBenchToolWindow.noop;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.jetbrains.annotations.NotNull;

import com.gocypher.cybench.launcher.model.BenchmarkReport;
import com.gocypher.cybench.viewPanels.HWJPanel;
import com.gocypher.cybench.viewPanels.JVMJPanel;
import com.gocypher.cybench.viewPanels.ResultJPanel;
import com.gocypher.cybench.viewPanels.SummaryPanel;
import com.intellij.ui.components.JBScrollPane;

public class NodeAndTabFiller extends ResultFileParser {
    private final HashMap<String, JScrollPane> testResultTabs;
    private final JTabbedPane tabs;
    private ResultJPanel currentTestPanel;

    public NodeAndTabFiller(HashMap<String, JScrollPane> testResultTabs, JTabbedPane tabs) {
        this.testResultTabs = testResultTabs;
        this.tabs = tabs;
    }

    @NotNull
    private static String[] getRowData(String key, Object value) {
        return new String[] { Utils.getKeyName(key), Utils.convertNumToStringByLength(String.valueOf(value)) };
    }

    private static void addKeyValue(String key, String value, int index, JPanel currentTestPanel) {
        GridBagConstraints cc = new GridBagConstraints();
        cc.gridy = index;

        JLabel testResKey = new JLabel(Utils.getKeyName(key), SwingConstants.LEFT);
        JLabel testResValue = new JLabel(Utils.convertNumToStringByLength(value), SwingConstants.RIGHT);
        JPanel testResultPanel = currentTestPanel;
        cc.gridx = 0;
        cc.anchor = GridBagConstraints.WEST;
        testResultPanel.add(testResKey, cc);
        cc.gridx = 1;
        cc.anchor = GridBagConstraints.EAST;
        testResultPanel.add(testResValue, cc);
    }

    @Override
    protected void onFinished() {
        try {

            Map.Entry<String, JScrollPane> next = testResultTabs.entrySet().iterator().next();
            tabs.addTab("Benchmark Details", next.getValue());
        } catch (NoSuchElementException e) {
            noop();
        }
    }

    @Override
    protected void onEnvironmentEntries(Map<String, Object> environment) {
        if (environment == null || environment.isEmpty()) {
            return;
        }

        HWJPanel hwjPanel = new HWJPanel();
        tabs.add("HW properties", hwjPanel);
        int[] idx = { 0 };
        environment.forEach((k, v) -> hwjPanel.table.getModel().addRow(getRowData(k, v)));
    }

    @Override
    protected void onJVMEntries(Map<String, Object> jvmSettings) {
        if (jvmSettings == null || jvmSettings.isEmpty()) {
            return;
        }
        JVMJPanel panel = new JVMJPanel();
        tabs.add("JVM properties", panel);
        int[] idx = { 0 };
        jvmSettings.forEach((k, v) -> panel.table.getModel().addRow(getRowData(k, v)));
    }

    @Override
    protected void onSummaryEntries(Map<String, Object> summary) {
        if (summary == null || summary.isEmpty()) {
            return;
        }
        SummaryPanel panel = new SummaryPanel();
        tabs.add("Summary", panel);
        summary.forEach((k, v) -> panel.table.getModel().addRow(getRowData(k, v)));

    }

    @Override
    public void onTestEnd(BenchmarkReport report) {
        JBScrollPane jbScrollPane = new JBScrollPane(currentTestPanel, VERTICAL_SCROLLBAR_ALWAYS,
                HORIZONTAL_SCROLLBAR_ALWAYS);
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
            currentTestPanel.setName(Utils.convertNumToStringByLength(String.valueOf(value)));
            break;
        case "score":
            currentTestPanel.setScore(Utils.convertNumToStringByLength(String.valueOf(value)));
            break;
        case "minScore":
            currentTestPanel.setMin(Utils.convertNumToStringByLength(String.valueOf(value)));
            break;
        case "maxScore":
            currentTestPanel.setMax(Utils.convertNumToStringByLength(String.valueOf(value)));
            break;

        }

        if (key.length() > 2 && key.substring(0, 2).equalsIgnoreCase("GC")) {
            ((DefaultTableModel) currentTestPanel.gcTable.getModel()).addRow(getRowData(key, value));

        }
        ((DefaultTableModel) currentTestPanel.allResults.getModel()).addRow(getRowData(key, value));
        // addKeyValue(key, value, index, currentTestPanel.other);

    }
}
