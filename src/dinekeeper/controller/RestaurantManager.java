package dinekeeper.controller;

import dinekeeper.model.Table;
import dinekeeper.model.data.RestaurantData;

public class RestaurantManager {
    public void insertTable(int id, int occupancy) {
        Table t = new Table(id, occupancy);
        RestaurantData.insert(t);
    }

    public void removeTable(int id) {
        RestaurantData.remove(id);
    }

    public void setAvailable(int id) {
        RestaurantData.getTable(id).setAvailability(true);
    }

    public void setUnavailable(int id) {
        RestaurantData.getTable(id).setAvailability(false);
    }
}