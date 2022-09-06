package uk.ac.ed.inf;

import java.sql.*;

/**
 * A class that represents the Order Object
 * The class has been encapsulated with setters and getters
 */

public class Order {
    private String orderNo;
    private Date deliveryDate;
    private String customer;
    private LongLat deliverTo;
    private String deliverToW3W;

    public String getOrderNo() {
        return orderNo;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public String getCustomer() {
        return customer;
    }

    public LongLat getDeliverTo() {
        return deliverTo;
    }

    public String getDeliverToW3W(){
        return deliverToW3W;
    }


    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public void setDeliverTo(LongLat deliverTo) {
        this.deliverTo = deliverTo;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public void setDeliverToW3W(String deliverToW3W){
        this.deliverToW3W = deliverToW3W;
    }
}
