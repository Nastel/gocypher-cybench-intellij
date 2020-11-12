package com.gocypher.cybench.viewPanels;

import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;

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
