package dinekeeper.view;

import dinekeeper.controller.ReservationManager;
import java.util.Scanner;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;

/** A CLI showcasing all tables and their statuses within the restaurant. */
public class TableView extends JPanel {
    private JButton addButton;
    private JButton removeButton;
    private JButton changeButton;
    private JScrollPane pane;
    private JTable table;

    public TableView() {

        addButton = new JButton("Add table");
        removeButton = new JButton("Remove table");
        changeButton = new JButton("Change Availability");
        //change availability button
        add(addButton);
        add(removeButton);
        add(changeButton);

    }

    public void addAddListener(ActionListener l) {
        addButton.addActionListener(l);
    }

    public void addRemoveListener(ActionListener l) {
        removeButton.addActionListener(l);
    }

    /** Returns the selected row. Returns -1 if no row is selected. */
    public void addChangeListener(ActionListener l) {
        changeButton.addActionListener(l);
    }

    public int selectedRow() {
        return table.getSelectedRow();
    }
    public void createTable(DefaultTableModel dtm) {
        table = new JTable(dtm);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        pane = new JScrollPane(table);
        add(pane);
    }
}
