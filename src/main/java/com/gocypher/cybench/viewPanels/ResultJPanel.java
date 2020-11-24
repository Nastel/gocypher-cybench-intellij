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

package com.gocypher.cybench.viewPanels;

import com.gocypher.cybench.utils.CyBenchIcons;
import com.intellij.ui.table.JBTable;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ResultJPanel extends JPanel {
    JPanel benchmarkInfo;
    JPanel scores;
    JPanel gcInfo;
    public JPanel other;

    JLabel name;
    JLabel score;
    JLabel mode;
    JLabel gcTime;
    JLabel gcCalls;
    JLabel gcAllocRate;
    JLabel gcChurn;
    JLabel min;
    JLabel mean;
    JLabel max;
    public JTable gcTable;
    public JTable allResults;


    String[] columnNames = {"GC Calls",
            "GC Alloc Rate",
            "GC Time",
            "GC Churn"};

    public ResultJPanel() {
        super();
        init();
    }

    private void init() {
        this.setLayout(new GridLayoutManager(4, 3, new Insets(0, 0, 0, 0), -1, -1));
        benchmarkInfo = new JPanel();
        benchmarkInfo.setLayout(new BorderLayout(0, 0));
        this.add(benchmarkInfo, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        name = new JLabel();
        Font nameFont = this.$$$getFont$$$(null, Font.BOLD, 18, name.getFont());
        if (nameFont != null) name.setFont(nameFont);
        name.setText("com.gocypher.cybench.jmh.jvm.client.tests.StringBenchmarks.findRegexUnCompiled");
        name.setIcon(CyBenchIcons.cyBenchLarge);
        benchmarkInfo.add(name, BorderLayout.WEST);
        final Spacer spacer1 = new Spacer();
        this.add(spacer1, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel scores = new JPanel();
        scores.setLayout(new GridLayoutManager(2, 5, new Insets(0, 0, 0, 0), -1, -1));
        this.add(scores, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        score = new JLabel();
        Font scoreFont = this.$$$getFont$$$(null, Font.BOLD, 22, score.getFont());
        if (scoreFont != null) score.setFont(scoreFont);
        score.setText("10000");
        scores.add(score, new GridConstraints(0, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        scores.add(spacer2, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Min:");
        scores.add(label1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        min = new JLabel();
        min.setText("50");
        scores.add(min, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Max:");
        scores.add(label2, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        max = new JLabel();
        max.setText("10000");
        scores.add(max, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        gcTable = new CBTable();
        JPanel gcInfo = new JPanel();
        gcInfo.setLayout(new BorderLayout());
        gcInfo.add(gcTable, BorderLayout.CENTER);

        this.add(gcInfo, new GridConstraints(2, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(150, 50), null, null, 0, false));


        allResults = new CBTable();
        other = new JPanel();
        other.setLayout(new BorderLayout());
        other.add(allResults, BorderLayout.CENTER);
        this.add(other, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));

    }

    public void setName(String className) {
        this.name.setText(className);
    }

    public void setScore(String score) {
        this.score.setText(score);
    }

    public void setMode(String mode) {
        this.mode.setText(mode);
    }

    public void setGcTime(String gcTime) {
        this.gcTime.setText(gcTime);
    }

    public void setGcCalls(String gcCalls) {
        this.gcCalls.setText(gcCalls);
    }

    public void setGcAllocRate(String gcAllocRate) {
        this.gcAllocRate.setText(gcAllocRate);
    }

    public void setGcChurn(String gcChurn) {
        this.gcChurn.setText(gcChurn);
    }

    public void setMin(String min) {
        this.min.setText(min);
    }

    public void setMean(String mean) {
        this.mean.setText(mean);
    }

    public void setMax(String max) {
        this.max.setText(max);
    }

    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    public static void main(String[] args) {
        JDialog d = new JDialog();
        d.setModal(true);
        ResultJPanel dialog = new ResultJPanel();

        d.setContentPane(dialog);
        d.pack();
        d.setVisible(true);
        System.exit(0);
    }



}
