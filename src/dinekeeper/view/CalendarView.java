package dinekeeper.view;

import dinekeeper.util.TableColumnManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

/** A Swing GUI showcasing the list of upcoming reservations. */
public class CalendarView extends JPanel {
    private JButton addButton;
    private JButton removeButton;
    private JButton serviceButton;
    private JButton saveButton;

    private JLabel sizeLabel;
    private JScrollPane pane;
    private JTable table;
    private DefaultTableModel dtm;

    public CalendarView() {
        JPanel buttons = new JPanel();
        addButton = new JButton("Make reservation");
        removeButton = new JButton("Remove reservation");
        serviceButton = new JButton("Service");
        saveButton = new JButton("Save");
        sizeLabel = new JLabel("Upcoming reservations: 0");
        buttons.add(saveButton);
        buttons.add(addButton);
        buttons.add(removeButton);
        buttons.add(serviceButton);
        buttons.add(sizeLabel);
        add(buttons, BorderLayout.NORTH);
    }

    public void addAddListener(ActionListener l) {
        addButton.addActionListener(l);
    }

    public void addRemoveListener(ActionListener l) {
        removeButton.addActionListener(l);
    }

    public void addServiceListener(ActionListener l) {
        serviceButton.addActionListener(l);
    }

    public void addTableListener(TableModelListener l) {
        dtm.addTableModelListener(l);
    }

    public void addSaveListener(ActionListener l) {
        saveButton.addActionListener(l);
    }

    public void updateSize(int x) {
        sizeLabel.setText("Upcoming reservations: " + x);
    }

    /** Returns the selected row. Returns -1 if no row is selected. */
    public int selectedRow() {
        return table.getSelectedRow();
    }

    public void createTable(DefaultTableModel dtm) {
        this.dtm = dtm;
        table = new JTable(this.dtm);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        pane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pane.setPreferredSize(new Dimension(700, 300));
        JScrollBar bar = pane.getVerticalScrollBar();
        bar.setPreferredSize(new Dimension(40, 0));
        add(pane, BorderLayout.CENTER);
        TableColumnManager tcm = new TableColumnManager(table);
    }
}
