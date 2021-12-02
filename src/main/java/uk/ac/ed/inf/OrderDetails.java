package uk.ac.ed.inf;

import java.util.ArrayList;

/**
 * A class that represents the OrderDetails class
 * The class has been encapsulated with getters and setters
 */

public class OrderDetails {
    private String orderNo;
    private ArrayList<String> items;

    public String getOrderNo() {
        return this.orderNo;
    }

    public ArrayList<String> getItems() {
        return this.items;
    }

    public void setItems(ArrayList<String> items) {
        this.items = items;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
