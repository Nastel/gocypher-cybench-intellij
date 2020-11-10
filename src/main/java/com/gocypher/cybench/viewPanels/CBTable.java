package com.gocypher.cybench.viewPanels;

import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class CBTable extends JBTable {
    public CBTable() {

        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        getModel().setColumnIdentifiers(new String[]{"Attribute Name", "Attribute value"});

    }

    @Override
    public DefaultTableModel getModel() {
        return (DefaultTableModel)super.getModel();
    }
}
