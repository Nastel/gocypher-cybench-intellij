/*
 * Copyright (C) 2020-2022, K2N.IO.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 */

package com.gocypher.cybench.viewPanels;

import java.awt.*;

import javax.swing.*;

public class HWJPanel extends JScrollPane {
    private static final long serialVersionUID = 6129830890657362838L;

    public CBTable table = new CBTable();
    private JPanel insidePanel = new JPanel();

    public HWJPanel() {
        super(null, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_ALWAYS);
        insidePanel.setLayout(new BorderLayout());
        insidePanel.add(table, BorderLayout.CENTER);
        setViewportView(insidePanel);
    }

}
