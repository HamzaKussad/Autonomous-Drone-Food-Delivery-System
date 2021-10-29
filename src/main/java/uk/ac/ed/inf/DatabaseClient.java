package uk.ac.ed.inf;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseClient {

    Connection conn;
    String url = "jdbc:derby://localhost:1527/derbyDB";

    public void connect(){
        try {
            conn = DriverManager.getConnection(url);
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void disconnect(){
        try {
            if (!conn.isClosed()){
                conn.close();
            }
        }catch (SQLException e){

        }
    }

    public HashMap getOrders(){
        HashMap<String,Order> orderList = new HashMap<>();
        final String ordersQuery =  "select * from orders";
        try{

            connect();
            PreparedStatement psOrderQuery = conn.prepareStatement(ordersQuery);
            ResultSet rs = psOrderQuery.executeQuery();
            while(rs.next()){
                Order order = new Order();
                order.orderNo = rs.getString("orderNo");
                order.deliveryDate = rs.getDate("deliveryDate");
                order.customer = rs.getString("customer");
                order.deliverTo = rs.getString("deliverTo");

                orderList.put(order.orderNo, order);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        disconnect();
        return orderList;

    }
    public HashMap getOrderItems(String orderNo){
        final String orderDetailsQuery = "select * from orderDetails where orderNo=(?)";
        HashMap<String , OrderDetails> orderDetail = new HashMap<>();
        try{
            connect();

            PreparedStatement psOrderDetailsQuery = conn.prepareStatement(orderDetailsQuery);
            psOrderDetailsQuery.setString(1,orderNo);

            ResultSet rs = psOrderDetailsQuery.executeQuery();
            ArrayList<String> items = new ArrayList<>();
            while (rs.next()) {
                String item = rs.getString("item");
                items.add(item);
            }

            OrderDetails orderDetails = new OrderDetails();

            orderDetails.orderNo = orderNo;
            orderDetails.items = items;

            orderDetail.put(orderNo,orderDetails);

        }catch (SQLException e){
            e.printStackTrace();
        }
        disconnect();
        return orderDetail;
    }

    public void fillOrderDetails(){
        HashMap<String,Order> orderList = getOrders();
        for (String order: orderList.keySet()){
            getOrderItems(order);
        }
    }
}
