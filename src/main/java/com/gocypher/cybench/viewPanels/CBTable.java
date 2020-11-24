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

import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class CBTable extends JBTable {
    public CBTable() {

        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        getModel().setColumnIdentifiers(new String[]{"Attribute Name", "Attribute value"});
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = rowAtPoint(new Point(e.getX(), e.getY()));
                int col = columnAtPoint(new Point(e.getX(), e.getY()));
                Object url = getValueAt(row, col);
                if (url instanceof String && ((String) url).startsWith("<HTML><a")) {
                    String substring = ((String) url).substring(((String) url).indexOf("href=") + 6);
                    String substring1 = substring.substring(0, substring.indexOf("\""));
                    try {
                        Desktop.getDesktop().browse(new URI(substring1));
                    } catch (IOException ex) {

                    } catch (URISyntaxException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public DefaultTableModel getModel() {
        return (DefaultTableModel) super.getModel();
    }
}
