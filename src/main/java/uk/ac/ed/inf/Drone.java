package uk.ac.ed.inf;

import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

public class Drone {

    ServerClient client;
    OrdersIO orders;
    int MOVES = 1500;

    Date date;
    AStar pathFinder;
    JourneyPlanner journeyPlanner = new CostPriority();
    HashMap<String,Order> orderList;
    ArrayList<String> journey;

    OrderPlanner orderPlanner = new OrderPlanner();


    public Drone(Date date , AStar pathFinder, JourneyPlanner journeyPlanner, ServerClient client, OrdersIO orders){

        this.date = date;
        this.pathFinder = pathFinder;
        this.journeyPlanner = journeyPlanner;
        this.client = client;
        this.orders = orders;

        this.orderList = OrdersIO.getOrders();
        this.journey = journeyPlanner.planJourney(getOrders(orderList));


    }

    public LineString getPlan(){

        System.out.println("orderlist:" + orderList);
        String order[] = {"177055e5","406d9b98"};
        System.out.println(orderList.size());
        ArrayList<LongLat> completePlan = planRoute(journey);
        while(MOVES <=0){
            System.out.println("removing last order");
            MOVES = 1500;
            journey.remove(journey.size()-1);
            completePlan = planRoute(journey);

        }

        System.out.println( MOVES);
        System.out.println(toLineString(completePlan).toJson());
        return toLineString(completePlan);

    }

    private ArrayList<LongLat> planRoute(ArrayList<String> journey) {
        ArrayList<LongLat> coords = orderPlanner.orderToPath(journey);
        ArrayList<LongLat> completePlan = new ArrayList<>();
        System.out.println(journey.size());
        for(int i = 0; i< coords.size() -1;i++){
           // System.out.println("long:"  +coords.get(i).longitude + " lat:" + coords.get(i).latitude);
            ArrayList<LongLat> path = pathFinder.nodeToList(pathFinder.getPath(coords.get(i).toNode(), (coords.get(i+1)).toNode()));
           // System.out.println(path.size());
            MOVES -= path.size() + 1; // +1 for hover move
            for(LongLat move : path){
                completePlan.add(move);
            }
            completePlan.add(path.get(path.size()-1)); //get target location for hovering position
        }
        return completePlan;
    }

    private String[] getOrders(HashMap<String,Order> orderList){
        ArrayList<String> orders = new ArrayList<>();
        for(String order: OrdersIO.getOrders().keySet()){
            System.out.println(order);
            orders.add(order);
        }
        return  orders.toArray(new String[orders.size()]);
    }

    private LineString toLineString(ArrayList<LongLat> routes){
        ArrayList<Point> points = new ArrayList<>();
        for(LongLat route: routes){

            points.add(Point.fromLngLat(route.longitude,route.latitude));
        }
        return LineString.fromLngLats(points);
    }



    public double percentageMoney (){
        HashMap<String,OrderItems> orderItems = OrdersIO.getOrderItems();
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
