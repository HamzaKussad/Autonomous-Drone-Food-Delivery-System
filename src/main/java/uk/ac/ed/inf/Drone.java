package uk.ac.ed.inf;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.TreeSet;

public class Drone {

    ServerClient client = new ServerClient("localhost", "9898");
    int MOVES = 1500;

    Date date;
    //AStar pathFinder;
    AStarPathFinder pathFinder;
    JourneyPlanner journeyPlanner;
    public Drone(Date date ,AStarPathFinder pathFinder, JourneyPlanner journeyPlanner){

        this.date = date;
        this.pathFinder = pathFinder;
        this.journeyPlanner = journeyPlanner;


    }
    public LineString getPlan(){

        HashMap<String,Order> orderList = DatabaseClient.getOrders();
        System.out.println("orderlist:" + orderList);
        ArrayList<LongLat> completePlan = new ArrayList<>();
        String order[] = {"177055e5","406d9b98"};
        String[] journey = journeyPlanner.planJourney(getOrders(orderList));
        ArrayList<LongLat> coords = orderToPath(journey);
        System.out.println(journey.length);
        while(MOVES > 0){
            for(int i = 0; i< coords.size() -1;i++){
                System.out.println("long:"  +coords.get(i).longitude + " lat:" + coords.get(i).latitude);
                ArrayList<LongLat> path = pathFinder.findRoute(coords.get(i), (coords.get(i+1)));
                System.out.println(path);
                MOVES -= path.size() + 1; // +1 for hover move
                for(LongLat move : path){
                    completePlan.add(move);
                }
                completePlan.add(path.get(path.size()-1)); //get target location for hovering position

            }
            break;

        }
        return toLineString(completePlan);

    }

    private String[] getOrders(HashMap<String,Order> orderList){
        ArrayList<String> orders = new ArrayList<>();
        for(String order: DatabaseClient.getOrders().keySet()){
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

    private ArrayList<LongLat> orderToPath(String[] orders){
        ArrayList<LongLat> coords = new ArrayList<>();
        HashMap<String,Order> orders1 = DatabaseClient.getOrders();
        LongLat appletone = new LongLat(-3.186874,55.944494 );
        coords.add(appletone);
        double dist = Double.POSITIVE_INFINITY;
        for(String order : orders){
            HashMap<String,OrderItems> orderItems = DatabaseClient.getOrderItems();
            ArrayList<LongLat> itemShops = new ArrayList<>();
            LinkedHashSet<LongLat> itemShopsNoDuplicate = new LinkedHashSet<>();


            for(String item :orderItems.get(order).items ){
                LongLat restaurantLocation = client.getLocationOfMenuItem(item);
                LongLat pickupLocation = client.getLongLatFrom3Words(orders1.get(order).deliverTo);


                if(pickupLocation.distanceTo(restaurantLocation) < dist){
                    dist = pickupLocation.distanceTo(restaurantLocation);
                    itemShops.add(0,restaurantLocation);
                    System.out.println("closest:" + restaurantLocation.longitude + " " + restaurantLocation.latitude);
                }
                itemShops.add(restaurantLocation);
                System.out.println(restaurantLocation.longitude + " " + restaurantLocation.latitude);


            }
            System.out.println(orderItems.get(order).items);
            System.out.println(itemShops.size());
            System.out.println(itemShops);

            for(LongLat rest: itemShops){
                itemShopsNoDuplicate.add(rest);
            }
            System.out.println(itemShopsNoDuplicate.size());


            for(LongLat coord : itemShopsNoDuplicate){
                System.out.println("nodup: "+ coord.longitude + " " + coord.latitude);
                coords.add(coord);
            }
            coords.add(client.getLongLatFrom3Words(orders1.get(order).deliverTo));



        }
        coords.add(appletone);
        return coords;


    }

}
