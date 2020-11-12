package com.gocypher.cybench.viewPanels;

import javax.swing.*;
import java.awt.*;

public class SummaryPanel extends JScrollPane {
    public CBTable table = new CBTable();
    private JPanel insidePanel = new JPanel();

    public SummaryPanel() {
        super(null, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_ALWAYS);
        insidePanel.setLayout(new BorderLayout());
        insidePanel.add(table, BorderLayout.CENTER);
        setViewportView(insidePanel);
    }

}
