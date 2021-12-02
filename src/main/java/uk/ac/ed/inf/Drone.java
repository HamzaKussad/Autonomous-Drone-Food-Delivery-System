package uk.ac.ed.inf;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
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

    /**  */
    private ArrayList<LongLat> completePlan = new ArrayList<>();
    private ArrayList<Flightpath> completeFlightpath = new ArrayList<>();
    private ArrayList<String> journey = new ArrayList<>();

    public FeatureCollection getPlan(){
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
        return toFeatureCollection(completePlan);

    }

    public void outputGeoJsonFolder(String day, String month, String year) {
        try{

            FileWriter file = new FileWriter("drone-"+day +"-" + month + "-" + year +".geojson");
            file.write(toFeatureCollection(completePlan).toJson());
            file.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

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

    private ArrayList<LongLat> planRoute() {
        ArrayList<LongLat> completePlan = new ArrayList<>();

        int firstOrder = 0;
        int lastOrder = journey.size()-1;

        ArrayList<LongLat> coords = orderPlanner.orderToStops(journey.get(firstOrder));

        Node target = flyFromAppleton(completePlan, Constants.appletonTower, firstOrder, coords);
        target = orderMoves(completePlan, coords, firstOrder, target);

        for(int order=1; order<journey.size(); order++){
            coords = orderPlanner.orderToStops(journey.get(order));
            target = orderMoves(completePlan, coords, order, target);
        }
        returnBackToAppleton(completePlan, Constants.appletonTower, lastOrder , target);

        return completePlan;
    }

    private Node orderMoves(ArrayList<LongLat> completePlan, ArrayList<LongLat> coords, int order, Node target) {
        for(int j = 0; j< coords.size() ; j++) {

            LongLat startLocation = new LongLat(target.getLongitude(), target.getLatitude());
            LongLat targetLocation = coords.get(j);

            target = (pathFinder.getPath(startLocation.toNode(), targetLocation.toNode()));

            collectMoves(completePlan, target);
            collectFlightpaths(target,order);

        }
        return target;
    }

    private Node flyFromAppleton(ArrayList<LongLat> completePlan, LongLat appleton,int order, ArrayList<LongLat> coords) {

        Node target = pathFinder.getPath(appleton.toNode(), (coords.get(0)).toNode());

        collectMoves(completePlan, target);
        collectFlightpaths(target,order);

        return target;
    }

    private void returnBackToAppleton(ArrayList<LongLat> completePlan, LongLat appleton, int order, Node target) {

        LongLat last = new LongLat(target.getLongitude(), target.getLatitude());
        target = (pathFinder.getPath(last.toNode(), appleton.toNode()));

        collectMoves(completePlan, target);
        collectFlightpaths(target,order);
    }

    private void collectFlightpaths(Node target, int order) {
        ArrayList<Flightpath> flightpaths = new ArrayList<>();

        ArrayList<Node> nodes = nodeToList(target);
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

    private void collectMoves(ArrayList<LongLat> completePlan, Node pathNode) {
        ArrayList<Node> path = nodeToList(pathNode);

        for (int k = 0; k < path.size(); k++) {
            completePlan.add(path.get(k).toLongLat());
        }
        MOVES -= path.size() +1; // +1 for hover move
    }

    private FeatureCollection toFeatureCollection(ArrayList<LongLat> routes){
        ArrayList<Point> points = new ArrayList<>();
        for(LongLat route: routes){

            points.add(Point.fromLngLat(route.getLongitude(), route.getLatitude()));
        }
        var featureLineString =  Feature.fromGeometry(LineString.fromLngLats(points));
        return FeatureCollection.fromFeature(featureLineString);
    }


    public ArrayList<Flightpath> flightpathsDataForDatabase(){
        return completeFlightpath;
    }

    public double percentageMoney (){
        HashMap<String, OrderDetails> orderItems = DatabaseIO.getOrderDetails();
        double actualDelivered = 0;
        double allOrders = 0;

        for (String order: DatabaseIO.getOrderIds()){
            allOrders+= Menus.getDeliveryCost(orderItems.get(order).getItems());
        }
        getPlan();
        for (String order: journey){
            actualDelivered+= Menus.getDeliveryCost(orderItems.get(order).getItems());
        }
        return (actualDelivered/allOrders)*100;
    }

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

}
