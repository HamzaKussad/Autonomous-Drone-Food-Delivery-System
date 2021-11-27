package uk.ac.ed.inf;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseIO {

    private String name;
    private String port;

    public DatabaseIO(String name, String port){
        this.name = name;
        this.port = port;
    }

    private WordsW3W w3wServer = new WordsW3W("localhost",App.webServerPort);

    private static Connection conn;

    private static HashMap<String,Order> orders = new HashMap<>();

    private static HashMap<String,OrderItems> orderItems = new HashMap<>();

    public static HashMap<String,Order> getOrders(){
        return orders;
    }
    public static HashMap<String,OrderItems> getOrderItems(){
        return orderItems;
    }

    public static String[] getOrderIds(){
        return  orders.keySet().toArray(new String[orders.keySet().size()]);
    }

    private void connect(){
        String url = "jdbc:derby://" + name +":"+ port +"/derbyDB";
        try {
            conn = DriverManager.getConnection(url);
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void disconnect(){
        try {
            if (!conn.isClosed()){
                conn.close();
            }
        }catch (SQLException e){

        }
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
                order.setOrderNo(rs.getString("orderNo"));
                order.setDeliveryDate(rs.getDate("deliveryDate"));
                order.setCustomer(rs.getString("customer"));
                order.setDeliverToW3W(rs.getString("deliverTo"));
                order.setDeliverTo(w3wServer.getLongLatFrom3Words( rs.getString("deliverTo")));

                orders.put(order.getOrderNo(), order);
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

            orderDetails.setOrderNo(orderNo);
            orderDetails.setItems(items);


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


    //creating database
    public void creatingDeliveriesDatabase(ArrayList<Delivery> deliveries){
        try{
            connect();
            Statement statement = conn.createStatement();
            DatabaseMetaData databaseMetaData = conn.getMetaData();

            ResultSet resultSet = databaseMetaData.getTables(null,null, "deliveries", null);

            if(resultSet.next()){
                statement.execute("drop table deliveries");
            }

            statement.execute(
                    "create table deliveries("+
                            "orderNo char(8), "+
                            "deliveryDate date, "+
                            "customer char(8), "+
                            "deliverTo varchar(18))");

            PreparedStatement psDeliveries = conn.prepareStatement(
                    "insert into deliveries values (?,?,?)");

            for(Delivery d : deliveries){
                psDeliveries.setString(1, d.orderNo);
                psDeliveries.setString(2,d.deliveredTo);
                psDeliveries.setInt(3, d.costInPence);
            }

            disconnect();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public void creatingFlightpathDatabase(ArrayList<Flightpath> flightpaths){
        try{
            connect();
            Statement statement = conn.createStatement();
            DatabaseMetaData databaseMetaData = conn.getMetaData();

            ResultSet resultSet = databaseMetaData.getTables(null,null, "flightpath", null);

            if(resultSet.next()){
                statement.execute("drop table flightpath");
            }

            statement.execute(
                    "create table flightpath("+
                            "orderNo char(8), "+
                            "fromLongitude double, "+
                            "fromLatitude double, "+
                            "angle integer, "+
                            "toLongitude double, "+
                            "toLatitude double)");

            PreparedStatement psFlightpath = conn.prepareStatement(
                    "insert into flightpath values (?,?,?,?,?,?)");

            for(Flightpath f : flightpaths){
                psFlightpath.setString(1, f.getOrderNo());
                psFlightpath.setDouble(2,f.getFromLongitude());
                psFlightpath.setDouble(3, f.getFromLatitude());
                psFlightpath.setInt(4,f.getAngle());
                psFlightpath.setDouble(5,f.getToLongitude());
                psFlightpath.setDouble(6,f.getToLatitude());

            }

            disconnect();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
