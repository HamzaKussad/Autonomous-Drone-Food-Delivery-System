package uk.ac.ed.inf;

import java.sql.*;

public class Order {
    private String orderNo;
    private Date deliveryDate;
    private String customer;
    private LongLat deliverTo;

    public String getOrderNo() {
        return orderNo;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public LongLat getDeliverTo() {
        return deliverTo;
    }

    public String getCustomer() {
        return customer;
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
}
