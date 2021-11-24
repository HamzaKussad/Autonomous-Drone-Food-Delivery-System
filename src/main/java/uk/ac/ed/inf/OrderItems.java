package uk.ac.ed.inf;

import java.util.ArrayList;

public class OrderItems {
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
