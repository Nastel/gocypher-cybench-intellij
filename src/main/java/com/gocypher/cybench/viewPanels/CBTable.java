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
