package dinekeeper.view;


import dinekeeper.util.TableColumnManager;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

/** A Swing GUI showcasing all tables and their statuses within the restaurant. */
public class TableView extends JPanel {
    private JButton addButton;
    private JButton removeButton;
    private JButton saveButton;
    private JLabel sizeLabel;
    private JScrollPane pane;
    private JTable table;
    private DefaultTableModel dtm;

    public TableView() {
        JPanel buttons = new JPanel();
        addButton = new JButton("Add table");
        removeButton = new JButton("Remove table");
        saveButton = new JButton("Save");
        sizeLabel = new JLabel("Available Tables: 0");
        buttons.add(addButton);
        buttons.add(removeButton);
        buttons.add(saveButton);
        buttons.add(sizeLabel);
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

    public void updateSize(int x) {
        sizeLabel.setText("Available Tables: " + x);
    }

    public void addSaveListener(ActionListener l) {
        saveButton.addActionListener(l);
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
