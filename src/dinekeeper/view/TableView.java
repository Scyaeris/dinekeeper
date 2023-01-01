package dinekeeper.view;


import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

/** A Swing GUI showcasing all tables and their statuses within the restaurant. */
public class TableView extends JPanel {
    private JButton addButton;
    private JButton removeButton;
    private JScrollPane pane;
    private JTable table;
    private DefaultTableModel dtm;

    public TableView() {
        JPanel buttons = new JPanel();
        addButton = new JButton("Add table");
        removeButton = new JButton("Remove table");
        buttons.add(addButton);
        buttons.add(removeButton);
        add(buttons, BorderLayout.NORTH);
    }

    public void addAddListener(ActionListener l) {
        addButton.addActionListener(l);
    }

    public void addRemoveListener(ActionListener l) {
        removeButton.addActionListener(l);
    }

    public void addTableListener(TableModelListener l) {
        dtm.addTableModelListener(l);
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
        add(pane, BorderLayout.CENTER);
        TableColumnManager tcm = new TableColumnManager(table);
    }
}
