package uk.ac.ed.inf;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class handles all the Input and Output of the database.
 */

public class DatabaseIO {

    /**
     * name and port of the database server
     */

    private String name;
    private String port;

    /**
     * Creates a DatabaseIO instance
     * @param name
     * @param port
     */

    public DatabaseIO(String name, String port){
        this.name = name;
        this.port = port;
    }

    /**
     * This creates a w3wServer instance to be able to switch from w3w to LongLat
     * immediately when reading from database
     */

    private WordsW3W w3wServer = new WordsW3W("localhost",App.webServerPort);

    /**
     * creating a connection used to connect to database once and make it static as its heavy
     */

    private static Connection conn;

    /**
     * collects orders read from database
     */

    private static HashMap<String,Order> orders = new HashMap<>();

    /**
     * collects the items of a specific order
     */

    private static HashMap<String, OrderDetails> orderDetails = new HashMap<>();

   //----getters

    public static HashMap<String,Order> getOrders(){
        return orders;
    }
    public static HashMap<String, OrderDetails> getOrderDetails(){
        return orderDetails;
    }

    public static String[] getOrderIds(){
        return  orders.keySet().toArray(new String[orders.keySet().size()]);
    }

    //-----

    /**
     * function that connects to the database
     */

    private void connect(){
        String url = "jdbc:derby://" + name +":"+ port +"/derbyDB";
        try {
            conn = DriverManager.getConnection(url);
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * function that disconnects from the database
     */

    private void disconnect(){
        try {
            if (!conn.isClosed()){
                conn.close();
            }
        }catch (SQLException e){

        }
    }


    /**
     * A function that uses an Order object to store
     * all the orders in a specific date using a hashmap.
     * It connects to the database, uses a query to get the orders
     * in a specific date, then reads all the data of that order
     * and stores them as Order object and adds them to the hashmap
     * with orderID as key and the order object as its value.
     *
     * @param deliveryDate from one of the input arguments when running
     *                     this program
     */

    public void storeOrders(Date deliveryDate){
        
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


    }

    /**
     * This function gets the order items of a specific orders by connecting
     * to the database and using a query to get the items from the orderDetails
     * database for a specific order
     * @param orderID
     * @return an OrderDetails object which will be used later to store all
     * the order details of all the orders in a specific date
     */
    
    
    private OrderDetails storeOrderDetails(String orderID){
        final String orderDetailsQuery = "select * from orderDetails where orderNo=(?)";
        OrderDetails orderDetails = new OrderDetails();
        try{

            connect();
            PreparedStatement psOrderDetailsQuery = conn.prepareStatement(orderDetailsQuery);
            psOrderDetailsQuery.setString(1,orderID);

            ResultSet rs = psOrderDetailsQuery.executeQuery();
            ArrayList<String> items = new ArrayList<>();
            while (rs.next()) {
                String item = rs.getString("item");
                items.add(item);
            }

            orderDetails.setOrderNo(orderID);
            orderDetails.setItems(items);


        }catch (SQLException e){
            e.printStackTrace();
        }
        disconnect();
        return orderDetails;
    }

    /**
     * This function get all the orders stored from the previous "storeOrders"
     * function, and runs the "storeOrderDetails" function for each order
     * to get the order details for all the orders in a given date
     */

    public void storeOrderDetails(){

//        System.out.println(orders);
        for (String order: orders.keySet()){
            orderDetails.put(order, storeOrderDetails(order));
        }
    }

    /**
     * This function creates a DELIVERIES database, it first connects to the
     * database server, it then creates a database named DELIVERIES, it checks
     * it a database of the same name already exists, if it does, it will drop it
     * and create a new one.
     *
     * The function then takes in an arraylist of deliveries as a Delivery Object
     * and adds the data to each row using the requirements provided in the project
     * description
     *
     * The function then disconnects from the server after creating the database
     * @param deliveries ArrayList of Delivery Object
     */

    //creating database
    public void creatingDeliveriesDatabase(ArrayList<Delivery> deliveries){
        try{
            connect();
            Statement statement = conn.createStatement();
            DatabaseMetaData databaseMetaData = conn.getMetaData();

            ResultSet resultSet = databaseMetaData.getTables(null,null, "DELIVERIES", null);

            if(resultSet.next()){
                statement.execute("drop table deliveries");
            }

            statement.execute(
                    "create table deliveries("+
                            "orderNo char(8), "+
                            "deliveredTo varchar(19), "+
                            "costInPence int)");

            PreparedStatement psDeliveries = conn.prepareStatement(
                    "insert into deliveries values (?,?,?)");

            for(Delivery d : deliveries){
                psDeliveries.setString(1, d.getOrderNo());
                psDeliveries.setString(2,d.getDeliveredTo());
                psDeliveries.setInt(3, d.getCostInPence());

                psDeliveries.execute();
            }

            disconnect();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * This function creates a FLIGHTPATH database, it first connects to the
     * database server, it then creates a database named FLIGHTPATH, it checks
     * it a database of the same name already exists, if it does, it will drop it
     * and create a new one.
     *
     * The function then takes in an arraylist of flightpaths as a Flightpath Object
     * and adds the data to each row using the requirements provided in the project
     * description
     *
     * The function then disconnects from the server after creating the database
     * @param flightpaths ArrayList of Flightpath Object
     */

    public void creatingFlightpathDatabase(ArrayList<Flightpath> flightpaths){
        try{
            connect();
            Statement statement = conn.createStatement();
            DatabaseMetaData databaseMetaData = conn.getMetaData();

            ResultSet resultSet = databaseMetaData.getTables(null,null, "FLIGHTPATH", null);

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

                psFlightpath.execute();
            }

            disconnect();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
