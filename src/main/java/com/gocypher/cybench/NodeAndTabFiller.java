package com.gocypher.cybench;

import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

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
    public void onTestEnd(String name) {
        JBScrollPane jbScrollPane = new JBScrollPane(currentTestPanel, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_ALWAYS);
        testResultTabs.put(name, jbScrollPane);

        tabs.add(name, jbScrollPane);
    }

    @Override
    public void onTest(String name) {
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
        GridBagConstraints cc = new GridBagConstraints();
        cc.gridy = index;

        JLabel testResKey = new JLabel(Utils.getKeyName(key), SwingConstants.LEFT);
        JLabel testResValue = new JLabel(Utils.convertNumToStringByLength(value), SwingConstants.RIGHT);
        JPanel testResultPanel = currentTestPanel.other;
        cc.gridx = 0;
        cc.anchor = GridBagConstraints.WEST;
        testResultPanel.add(testResKey, cc);
        cc.gridx = 1;
        cc.anchor = GridBagConstraints.EAST;
        testResultPanel.add(testResValue, cc);

    }
}
