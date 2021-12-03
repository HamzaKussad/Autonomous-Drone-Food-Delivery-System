package uk.ac.ed.inf;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Drone class that is responsible for all movements
 * and outputs of the drone
 */

public class Drone {

    /** Stores the number of moves left in the drone */
    private int MOVES = 1500;
    /** Path finding Algorithm instance*/
    private PathFinder pathFinder;
    /** Order planning algorithm instance */
    private OrderPlanner orderPlanner;
    /** Journey planning algorithm instance */
    private JourneyPlanner journeyPlanner;

    /**
     * Creates a Drone instance
     * @param pathFinder Path finding algorithm
     * @param journeyPlanner Order planning algorithm
     * @param orderPlanner Journey planning algorithm
     */
    public Drone( PathFinder pathFinder, JourneyPlanner journeyPlanner, OrderPlanner orderPlanner){

        this.pathFinder = pathFinder;
        this.orderPlanner = orderPlanner;
        this.journeyPlanner = journeyPlanner;

        String order[] = {"4f457bd9"};

    }

    /** ArrayList that store the complete plan as LongLat array to use in GeoJSON file */
    private ArrayList<LongLat> completePlan = new ArrayList<>();
    /** ArrayList that store the complete plan as Flightpath array to use in FLIGHTPATH database */
    private ArrayList<Flightpath> completeFlightpath = new ArrayList<>();
    /** Arraylist that stores the ordered journey using the orderIDs */
    private ArrayList<String> journey = new ArrayList<>();


    /**
     * This function executes the plan of the drone
     * and fills up the completePlan array to be used
     * for the outputs.
     */
    public void executePlan(){
        journey = journeyPlanner.planJourney(DatabaseIO.getOrderIds());
        completePlan = planRoute();
        while(MOVES <=0){
            System.out.println("removing last order");
            MOVES = 1500;
            journey.remove(journey.size()-1);
            completePlan = planRoute();

        }
        System.out.println( MOVES);
        System.out.println(toFeatureCollection(completePlan).toJson());
    }

    /**
     * This function output the drone's path as a GeoJSON file using the
     * date that the program is run on, and creates a folder
     * if the format of drone-DD-MM-YYYY.geojson
     * @param day
     * @param month
     * @param year
     */

    public void outputGeoJsonFolder(String day, String month, String year) {
        try{

            FileWriter file = new FileWriter("drone-"+day +"-" + month + "-" + year +".geojson");
            file.write(toFeatureCollection(completePlan).toJson());
            file.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * This function creates a Delivery Arraylist that will be used
     * to create a DELIVERIES table in the database.
     * Delivery information is collected and stored.
     * @return Arraylist of the Delivery Object
     */

    public ArrayList<Delivery> deliveryDataForDatabase() {
        ArrayList<Delivery> deliveries = new ArrayList<>();
        HashMap<String,Order> orderList1 = DatabaseIO.getOrders();
        HashMap<String, OrderDetails> orderItems = DatabaseIO.getOrderDetails();
        for(String order: journey){
            Delivery delivery = new Delivery();
            delivery.setOrderNo(order);
            delivery.setDeliveredTo(orderList1.get(order).getDeliverToW3W());
            delivery.setCostInPence(Menus.getDeliveryCost(orderItems.get(order).getItems()));
            deliveries.add(delivery);
        }
        return deliveries;
    }

    /**
     * gets the completeFlightpath of the journey to be used
     * to create a FLIGHTPATH table in the database
     * @return Arraylist of the Flightpath Object
     */

    public ArrayList<Flightpath> flightpathsDataForDatabase(){
        return completeFlightpath;
    }

    //--------Functions for route planning with helpers

    /**
     * returns the full plan for all the orders in the journey
     * starts from appleton tower and ends at appleton tower always
     * @return Arraylist of LongLat that represents the complete plan
     */

    private ArrayList<LongLat> planRoute() {

        int firstOrder = 0;
        int lastOrder = journey.size()-1;

        ArrayList<LongLat> stops = orderPlanner.orderToStops(journey.get(firstOrder));

        Node target = flyFromAppleton(firstOrder, stops);
        target = orderMoves( stops, firstOrder, target);

        for(int order=1; order<journey.size(); order++){
            stops = orderPlanner.orderToStops(journey.get(order));
            target = orderMoves( stops, order, target);
        }
        returnBackToAppleton(lastOrder , target);

        return completePlan;
    }

    /**
     * This function computes the moves between each stop in an order.
     * It loops through all the orders and uses the target location achieved
     * from previous order as a starting location and so on, and uses helper functions
     * to collect the moves and the flight paths
     *
     * @param stops Arraylist of stops in an order represented as LongLat
     * @param order the corresponding position of an order in the journey
     * @param target the target location of the pathfinding algorithm
     * @return the last location the drone visited (which is closeTo a dropoff location)
     */

    private Node orderMoves( ArrayList<LongLat> stops, int order, Node target) {
        for(int j = 0; j< stops.size() ; j++) {

            LongLat startLocation = new LongLat(target.getLongitude(), target.getLatitude());
            LongLat targetLocation = stops.get(j);

            target = (pathFinder.getPath(startLocation.toNode(), targetLocation.toNode()));

            collectMoves( target);
            collectFlightpaths(target,order);

        }
        return target;
    }

    /**
     * A function that is responsible for the first path of the drone,
     * which is flying from appleton and finishing the first order
     *
     * @param stops Arraylist of stops in an order represented as LongLat
     * @param order the corresponding position of an order in the journey which is the first
     *              stop in the journey
     *
     * @return the last location the drone visited (which is closeTo a dropoff location of the order)
     */

    private Node flyFromAppleton( int order, ArrayList<LongLat> stops) {

        Node target = pathFinder.getPath(Constants.appletonTower.toNode(), (stops.get(0)).toNode());

        collectMoves(target);
        collectFlightpaths(target,order);

        return target;
    }

    /**
     * This function is responsible to return the drone back to Appleton tower
     *
     * @param order the corresponding position of an order in the journey which is the last
     *              stop in the journey
     * @param target the target location of the pathfinding algorithm which is the last stop
     *               in this case
     */

    private void returnBackToAppleton( int order, Node target) {

        LongLat last = new LongLat(target.getLongitude(), target.getLatitude());
        target = (pathFinder.getPath(last.toNode(), Constants.appletonTower.toNode()));

        collectMoves(target);
        collectFlightpaths(target,order);
    }

    /**
     * This function takes a Node and returns a list of nodes
     * where each node contains the LongLat position and the angle
     * the drone was facing at the time
     *
     *
     * @param path
     * @return ArrayList of nodes
     */

    private ArrayList<Node> nodeToList(Node path){
        ArrayList<Node> nodes = new ArrayList<>();
        nodes.add(path);
        while (path.parent != null){
            path = path.parent;
            nodes.add(path);
        }

        Collections.reverse(nodes);
        return nodes;
    }

    /**
     * This function collects all the moves done by the drone between 2 point
     * and gets the flight paths for it, it even handles the hovering case
     * and fills the completeFlightpath List
     *
     * This function uses the LongLats and Angle saved during Astar
     * for the flight paths
     *
     * @param path A node containing the path between 2 points
     * @param order the corresponding position of an order in the journey
     */

    private void collectFlightpaths(Node path, int order) {
        ArrayList<Flightpath> flightpaths = new ArrayList<>();

        ArrayList<Node> nodes = nodeToList(path);
        for(int i=0; i< nodes.size()-1; i++){
            Flightpath flightpath = new Flightpath();
            flightpath.setFromLatitude(nodes.get(i).getLatitude());
            flightpath.setFromLongitude(nodes.get(i).getLongitude());
            flightpath.setAngle(nodes.get(i+1).angle);
            flightpath.setToLatitude(nodes.get(i+1).getLatitude());
            flightpath.setToLongitude(nodes.get(i+1).getLongitude());
            flightpaths.add(flightpath);

        }
        Node lastNode = nodes.get(nodes.size()-1);
        Flightpath flightpathLast = new Flightpath();
        flightpathLast.setFromLatitude(lastNode.getLatitude());
        flightpathLast.setFromLongitude(lastNode.getLongitude());
        flightpathLast.setAngle(-999);
        flightpathLast.setToLatitude(lastNode.getLatitude());
        flightpathLast.setToLongitude(lastNode.getLongitude());
        flightpaths.add(flightpathLast);

        for(Flightpath flightpath: flightpaths){
            flightpath.setOrderNo(journey.get(order));
            completeFlightpath.add(flightpath);
        }
    }

    /**
     * This function uses the list of Nodes, and gets the LongLat of each
     * node and adds them to the completePlan to be used.
     * This function also calculates the MOVES done in each path between
     * 2 stops, and subtracts an extra move for the Hover
     *
     * @param path
     */

    private void collectMoves(Node path) {
        ArrayList<Node> nodes = nodeToList(path);

        for (int k = 0; k < nodes.size(); k++) {
            completePlan.add(nodes.get(k).toLongLat());
        }
        MOVES -= nodes.size() +1; // +1 for hover move
    }

    //------------------------------------


    /**
     * A helper function that changes the moves done by the drone to
     * a FeatureCollection that contains only one linestring that contains
     * all the points from the moves
     * @param moves ArrayList of LongLat that contains all the moves done by drone
     * @return FeatureCollection with one LineString
     */
    private FeatureCollection toFeatureCollection(ArrayList<LongLat> moves){
        ArrayList<Point> points = new ArrayList<>();

        for(LongLat move: moves){
            points.add(Point.fromLngLat(move.getLongitude(), move.getLatitude()));
        }

        var featureLineString =  Feature.fromGeometry(LineString.fromLngLats(points));
        return FeatureCollection.fromFeature(featureLineString);
    }


    /**
     * This function calculated the sampled average percentage monetary value
     * @return sampled average percentage monetary value
     */

    public double percentageMoney (){
        HashMap<String, OrderDetails> orderItems = DatabaseIO.getOrderDetails();
        double actualDelivered = 0;
        double allOrders = 0;

        for (String order: DatabaseIO.getOrderIds()){
            allOrders+= Menus.getDeliveryCost(orderItems.get(order).getItems());
        }
        executePlan();
        for (String order: journey){
            actualDelivered+= Menus.getDeliveryCost(orderItems.get(order).getItems());
        }
        return (actualDelivered/allOrders)*100;
    }



}
