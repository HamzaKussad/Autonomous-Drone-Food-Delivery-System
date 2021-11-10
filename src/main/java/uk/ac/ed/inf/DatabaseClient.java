package uk.ac.ed.inf;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseClient {

    Connection conn;
    String url = "jdbc:derby://localhost:1527/derbyDB";
    private static HashMap<String,Order> orders = new HashMap<>();
    private static HashMap<String,OrderItems> orderItems = new HashMap<>();

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

    public static HashMap<String,Order> getOrders(){
        return orders;
    }
    public static HashMap<String,OrderItems> getOrderItems(){
        return orderItems;
    }

    public HashMap<String,Order> storeOrders(Date deliveryDate){


        final String ordersQuery =  "select * from orders where deliveryDate = (?)";
        try{

            connect();
            PreparedStatement psOrderQuery = conn.prepareStatement(ordersQuery);
            psOrderQuery.setDate(1,deliveryDate);
            ResultSet rs = psOrderQuery.executeQuery();
            while(rs.next()){
                Order order = new Order();
                order.orderNo = rs.getString("orderNo");
                order.deliveryDate = rs.getDate("deliveryDate");
                order.customer = rs.getString("customer");
                order.deliverTo = rs.getString("deliverTo");

                orders.put(order.orderNo, order);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        disconnect();

        System.out.println(orders);

        return orders;

    }
    private OrderItems getOrderItems(String orderNo){
        final String orderDetailsQuery = "select * from orderDetails where orderNo=(?)";
        OrderItems orderDetails = new OrderItems();
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



            orderDetails.orderNo = orderNo;
            orderDetails.items = items;


        }catch (SQLException e){
            e.printStackTrace();
        }
        disconnect();
        return orderDetails;
    }

    public HashMap storeOrderDetails(){

        System.out.println(orders);
        for (String order: orders.keySet()){
            orderItems.put(order , getOrderItems(order));
        }

        return orderItems;
    }
}
