package dinekeeper.view;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

/** A Swing GUI showcasing the list of upcoming reservations. */
public class CalendarView extends JPanel {
    private JButton addButton;
    private JButton changeButton;
    private JButton removeButton;
    private JButton serviceButton;

    //service
    private JScrollPane pane;
    private JTable table;
    private DefaultTableModel dtm;

    public CalendarView() {
        addButton = new JButton("Make reservation");
        removeButton = new JButton("Remove reservation");
        changeButton = new JButton("Change reservation");
        serviceButton = new JButton("Service");
        add(addButton);
        add(removeButton);
        add(changeButton);
        add(serviceButton);
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

    /** Returns the selected row. Returns -1 if no row is selected. */
    public int selectedRow() {
        return table.getSelectedRow();
    }

    public void createTable(DefaultTableModel dtm) {
        this.dtm = dtm;
        table = new JTable(this.dtm);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        pane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollBar bar = pane.getVerticalScrollBar();
        bar.setPreferredSize(new Dimension(40, 0));
        add(pane);
    }
}
