package uk.ac.ed.inf;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

public class Drone {

    private ServerClient client;
    private DatabaseIO orders;
    private int MOVES = 1500;

    private Date date;
    private AStar pathFinder;
    private JourneyPlanner journeyPlanner;
    private String[] orderList;
    private ArrayList<String> journey;
    private OrderPlanner orderPlanner = new OrderPlanner();


    public Drone(Date date , AStar pathFinder, JourneyPlanner journeyPlanner, ServerClient client, DatabaseIO orders){

        this.date = date;
        this.pathFinder = pathFinder;
        this.journeyPlanner = journeyPlanner;
        this.client = client;
        this.orders = orders;

        this.orderList = DatabaseIO.getOrderIds();
        String order[] = {"4f457bd9"};
        //"4e67bb23"
        this.journey = journeyPlanner.planJourney(orderList);
    }

    ArrayList<LongLat> completePlan = new ArrayList<>();
    ArrayList<Flightpath> completeFlightpath = new ArrayList<>();

    public FeatureCollection getPlan(){


        System.out.println("orderlist:" + orderList);
        String order[] = {"177055e5","406d9b98"};
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

    private ArrayList<LongLat> planRoute() {
        ArrayList<LongLat> completePlan = new ArrayList<>();
        completeFlightpath = new ArrayList<>();
        LongLat appleton = new LongLat(-3.186874,55.944494 );

        ArrayList<LongLat> coords;

        if(journey.size() == 1) {
            coords = orderPlanner.orderToPathTest(journey.get(0));
            Node target = flyFromAppleton(completePlan, appleton, coords);
            target = orderMoves(completePlan, coords, 0, target);
            returnBackToAppleton(completePlan, appleton, target);
            return completePlan;
        }


        coords = orderPlanner.orderToPathTest(journey.get(0));
        Node target = flyFromAppleton(completePlan, appleton, coords);
        target = orderMoves(completePlan, coords, 0, target);
        for(int order=1; order<journey.size(); order++){
            coords = orderPlanner.orderToPathTest(journey.get(order));
            target = orderMoves(completePlan, coords, order, target);
        }
        returnBackToAppleton(completePlan, appleton, target);




        return completePlan;
    }

    private Node orderMoves(ArrayList<LongLat> completePlan, ArrayList<LongLat> coords, int i, Node target) {
        for(int j = 0; j< coords.size() ; j++) {
            LongLat startLocation = new LongLat(target.longitude, target.latitude);
            LongLat targetLocation = coords.get(j);
            target = (pathFinder.getPath(startLocation.toNode(), targetLocation.toNode()));
            ArrayList<LongLat> path = pathFinder.nodeToList(target);
            ArrayList<Flightpath> flightpaths = pathFinder.nodeToFlightpath(target);


            for (LongLat move: path) {
                completePlan.add(move);
            }
            MOVES -= path.size() +1; // +1 for hover move
            for(Flightpath flightpath: flightpaths){
                flightpath.setOrderNo(journey.get(i));
                completeFlightpath.add(flightpath);
            }
        }
        return target;
    }

    private Node flyFromAppleton(ArrayList<LongLat> completePlan, LongLat appleton, ArrayList<LongLat> coords) {

        Node pathNode = pathFinder.getPath(appleton.toNode(), (coords.get(0)).toNode());


        ArrayList<LongLat> path = pathFinder.nodeToList(pathNode);

        for (int k = 0; k < path.size(); k++) {
            completePlan.add(path.get(k));
        }
        MOVES -= path.size() +1; // +1 for hover move

        ArrayList<Flightpath> flightpaths = pathFinder.nodeToFlightpath(pathNode);

        for(Flightpath flightpath: flightpaths){
            flightpath.setOrderNo(journey.get(0));
            completeFlightpath.add(flightpath);
        }
        return pathNode;
    }

    private void returnBackToAppleton(ArrayList<LongLat> completePlan, LongLat appleton, Node pathNode) {
        ArrayList<LongLat> path;
        ArrayList<Flightpath> flightpaths;
        LongLat last = new LongLat(pathNode.longitude, pathNode.latitude);

        pathNode = (pathFinder.getPath(last.toNode(), appleton.toNode()));

        path = pathFinder.nodeToList(pathNode);
        flightpaths = pathFinder.nodeToFlightpath(pathNode);


        for (int k = 0; k < path.size(); k++) {
            completePlan.add(path.get(k));
        }
        MOVES -= path.size() +1; // +1 for hover move
        for(Flightpath flightpath: flightpaths){
            int lastOrder= journey.size()-1;
            flightpath.setOrderNo(journey.get(lastOrder));
            completeFlightpath.add(flightpath);
        }
    }

    private FeatureCollection toFeatureCollection(ArrayList<LongLat> routes){
        ArrayList<Point> points = new ArrayList<>();
        for(LongLat route: routes){

            points.add(Point.fromLngLat(route.longitude,route.latitude));
        }
        var featureLineString =  Feature.fromGeometry(LineString.fromLngLats(points));
        return FeatureCollection.fromFeature(featureLineString);
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
        HashMap<String,OrderItems> orderItems = DatabaseIO.getOrderItems();
        for(String order: journey){
            Delivery delivery = new Delivery();
            delivery.orderNo = order;
            delivery.costInPence = Menus.getDeliveryCost(orderItems.get(order).getItems());
            delivery.deliveredTo = orderList1.get(order).getDeliverToW3W();
            deliveries.add(delivery);
        }
        return deliveries;
    }

    public ArrayList<Flightpath> flightpathsDataForDatabase(){
//        ArrayList<Flightpath> completeFlightpaths = new ArrayList<>();
//        for(String order: journey){
//            for(Flightpath flightpath: flightpaths){
//                flightpath.setOrderNo(order);
//                completeFlightpaths.add(flightpath);
//            }
//            Flightpath hoverAt = flightpaths.get(flightpaths.size()-1);
//            double destinationLat = hoverAt.getFromLatitude();
//            double destinationLong = hoverAt.getFromLongitude();
//            hoverAt.getFromLongitude();
//            hoverAt.setAngle(-999);
//            hoverAt.setToLongitude(destinationLong);
//            hoverAt.setToLatitude(destinationLat);
//
//            completeFlightpaths.add(hoverAt);
//        }
        return completeFlightpath;
    }



    public double percentageMoney (){
        HashMap<String,OrderItems> orderItems = DatabaseIO.getOrderItems();
        double actualDelivered = 0;
        double allOrders = 0;

        for (String order: journey){
            allOrders+= Menus.getDeliveryCost(orderItems.get(order).getItems());
        }
        getPlan();
        for (String order: journey){
            actualDelivered+= Menus.getDeliveryCost(orderItems.get(order).getItems());
        }
        return (actualDelivered/allOrders)*100;
    }

}
