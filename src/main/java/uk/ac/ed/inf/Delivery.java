package uk.ac.ed.inf;

/**
 * Class that represents the Delivery Object
 * Class has been encapsulated
 */
public class Delivery {
    private String orderNo;
    private String deliveredTo;
    private int costInPence;

    public String getOrderNo() {
        return orderNo;
    }

    public String getDeliveredTo() {
        return deliveredTo;
    }

    public int getCostInPence() {
        return costInPence;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public void setDeliveredTo(String deliveredTo) {
        this.deliveredTo = deliveredTo;
    }

    public void setCostInPence(int costInPence) {
        this.costInPence = costInPence;
    }
}
