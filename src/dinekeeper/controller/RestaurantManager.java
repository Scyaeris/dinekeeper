package dinekeeper.controller;

import dinekeeper.model.Table;
import dinekeeper.model.data.RestaurantData;
import dinekeeper.view.TableView;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/** A controller that manages usage and storage in the tables of a restaurant. */
public class RestaurantManager {
    private RestaurantData restaurant;
    private TableView view;
    private Map<Integer, Integer> idRowMap = new HashMap<>();
    private static int nextRow = 0;

    private DefaultTableModel dtm = new DefaultTableModel(null,
            new String[]{"ID", "Occupancy", "Availability"});


    public RestaurantManager(TableView v, RestaurantData restaurant) {
        this.restaurant = restaurant;
        view = v;
        addListeners();
        initializeTableView();
        view.createTable(dtm);
    }

    /** Formats restaurant data into tabular form for GUI. */
    private void initializeTableView() {
        Map<Integer, Table> map = restaurant.getTables();
        for (Map.Entry<Integer, Table> set : map.entrySet()) {
            dtm.addRow(new Object[]{set.getKey(), set.getValue().getOccupancy(), set.getValue().getAvailability()});
            idRowMap.put(set.getKey(), nextRow++);
        }
    }

    public void insertTable(int id, int occupancy) {
        Table t = new Table(id, occupancy);
        restaurant.insert(t);
        //update table view
        dtm.addRow(new Object[]{id, occupancy, Boolean.TRUE});
        idRowMap.put(id, nextRow++);
    }

    public void removeTable(int id) {
        restaurant.remove(id);
        //update table view
        dtm.removeRow(idRowMap.get(id));
        nextRow--;
    }

    public void changeAvailability(int id, int row) {
        restaurant.getTable(id).setAvailability(!restaurant.checkAvailability(id));
        dtm.setValueAt(!(Boolean) dtm.getValueAt(row, 2), row, 2);
    }

    public void addListeners() {
        view.addAddListener(e -> {
            int id = Integer.parseInt(JOptionPane.showInputDialog("Table id: "));
            int occupancy = Integer.parseInt(JOptionPane.showInputDialog("Occupancy: "));
            insertTable(id, occupancy);
        });

        view.addRemoveListener(e -> {
            int id = Integer.parseInt(JOptionPane.showInputDialog("Input id: "));
            removeTable(id);
        });

        view.addChangeListener(e -> {
            int row = view.selectedRow();
            if (row != -1) {
                changeAvailability((int) dtm.getValueAt(row, 0), row);
            }
        });
    }

    //TODO: Change occupancy & availability options
}