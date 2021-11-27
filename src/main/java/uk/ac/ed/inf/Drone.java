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

    ServerClient client;
    DatabaseIO orders;
    int MOVES = 1500;

    Date date;
    AStar pathFinder;
    JourneyPlanner journeyPlanner;
    String[] orderList;
    ArrayList<String> journey;

    OrderPlanner orderPlanner = new OrderPlanner();


    public Drone(Date date , AStar pathFinder, JourneyPlanner journeyPlanner, ServerClient client, DatabaseIO orders){

        this.date = date;
        this.pathFinder = pathFinder;
        this.journeyPlanner = journeyPlanner;
        this.client = client;
        this.orders = orders;

        this.orderList = DatabaseIO.getOrderIds();
        this.journey = journeyPlanner.planJourney(orderList);


    }

    ArrayList<Flightpath> flightpaths = new ArrayList<>();

    public FeatureCollection getPlan(){

        System.out.println("orderlist:" + orderList);
        String order[] = {"177055e5","406d9b98"};
        ArrayList<LongLat> completePlan = planRoute(journey);
        while(MOVES <=0){
            System.out.println("removing last order");
            MOVES = 1500;
            journey.remove(journey.size()-1);
            completePlan = planRoute(journey);

        }

        System.out.println( MOVES);
        System.out.println(toFeatureCollection(completePlan).toJson());
        return toFeatureCollection(completePlan);

    }

    private ArrayList<LongLat> planRoute(ArrayList<String> journey) {
        ArrayList<LongLat> coords = orderPlanner.orderToPath(journey);
        ArrayList<LongLat> completePlan = new ArrayList<>();
        System.out.println(journey.size());
        for(int i = 0; i< coords.size() -1;i++){

            ArrayList<LongLat> path = pathFinder.nodeToList(pathFinder.getPath(coords.get(i).toNode(), (coords.get(i+1)).toNode()));

            MOVES -= path.size() +1; // +1 for hover move

            for(LongLat move : path){

                completePlan.add(move);
            }
            completePlan.add(path.get(path.size()-1)); //add target location for hovering position
        }
        return completePlan;
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
            file.write(getPlan().toJson());
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

//    public ArrayList<Flightpath> flightpathsDataForDatabase(){
//        ArrayList<Flightpath> flightpaths = new ArrayList<>();
//    }



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
