package dinekeeper.controller;

import dinekeeper.model.Table;
import dinekeeper.model.data.RestaurantData;
import dinekeeper.util.InvalidTableAssignmentException;
import dinekeeper.util.InvalidTableUpdateException;
import dinekeeper.view.TableView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

/** A controller that manages usage and storage in the tables of a restaurant. */
public class RestaurantManager {
    private RestaurantData restaurant;
    private TableView view;

    /** Stores the ID of each table for faster lookup/table updates. Indices are rows. */
    private LinkedList<Integer> ids = new LinkedList<>();

    private DefaultTableModel dtm = new DefaultTableModel(null,
            new String[]{"ID", "Occupancy", "Availability"}) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 2;
        }

        @Override
        public Class<?> getColumnClass(int col) {
            if (col == 2) return Boolean.class;
            return super.getColumnClass(col);
        }
    };

    public RestaurantManager(TableView v, RestaurantData restaurant) {
        this.restaurant = restaurant;
        view = v;
        initializeTableView();
        view.createTable(dtm);
        addListeners();
    }

    /** Formats restaurant data into tabular form for GUI. */
    private void initializeTableView() {
        Map<Integer, Table> map = restaurant.getTables();
        for (Map.Entry<Integer, Table> set : map.entrySet()) {
            dtm.addRow(new Object[]{set.getKey(), set.getValue().getOccupancy(), set.getValue().getAvailability()});
            ids.add(set.getKey());
        }
        view.updateSize(restaurant.size());
    }

    public void insertTable(int id, int occupancy) {
        Table t = new Table(id, occupancy);
        try {
            restaurant.insert(t);
            //update table view
            dtm.addRow(new Object[]{id, occupancy, Boolean.TRUE});
            ids.add(id);
            view.updateSize(restaurant.size());
        } catch (InvalidTableAssignmentException e) {
            JOptionPane.showMessageDialog(null, "Table ID " + id + " already exists.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void removeTable(int id) {
        try {
            restaurant.remove(id);
            //update table view
            dtm.removeRow(ids.indexOf(id));
            ids.remove(ids.indexOf(id));
            view.updateSize(restaurant.size());
        } catch (InvalidTableUpdateException e) {
            JOptionPane.showMessageDialog(null, "Table ID " + id + " does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void addListeners() {
        view.addAddListener(e -> {
            try {
                int id = Integer.parseInt(JOptionPane.showInputDialog("Table id: "));
                int occupancy = Integer.parseInt(JOptionPane.showInputDialog("Occupancy: "));
                insertTable(id, occupancy);
            } catch (NumberFormatException ex) {}
        });

        view.addRemoveListener(e -> {
            try {
                int id = Integer.parseInt(JOptionPane.showInputDialog("Input id: "));
                removeTable(id);
            } catch (NumberFormatException ex) {}
        });

        view.addSaveListener(e -> {
            //serialise
            try {
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("bin/restaurant-data.bin"));
                oos.writeObject(restaurant);
                JOptionPane.showMessageDialog(null, "Restaurant Data Saved!");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        view.addTableListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int id = (int) dtm.getValueAt(e.getFirstRow(), 0);
                int col = e.getColumn();
                if (col == 2) {
                    restaurant.changeAvailability(id);
                    view.updateSize(restaurant.size());
                }
            }
        });
    }
}