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

    ServerClient client;
    WordsW3W words = new WordsW3W("localhost", "9898");
    Menus menus;
    OrdersIO orders;
    int MOVES = 2000;

    Date date;
    AStar pathFinder;
    //AStarPathFinder pathFinder;
    JourneyPlanner journeyPlanner;
    public Drone(Date date ,AStar pathFinder, JourneyPlanner journeyPlanner,ServerClient client, OrdersIO orders){

        this.date = date;
        this.pathFinder = pathFinder;
        this.journeyPlanner = journeyPlanner;
        this.client = client;
        this.orders = orders;


    }
    public LineString getPlan(){

        HashMap<String,Order> orderList = OrdersIO.getOrders();
        System.out.println("orderlist:" + orderList);
        ArrayList<LongLat> completePlan = new ArrayList<>();
        String order[] = {"177055e5","406d9b98"};
        System.out.println(orderList.size());
        String[] journey = journeyPlanner.getOrdersExp(getOrders(orderList));
        ArrayList<LongLat> coords = orderToPath(journey);
        System.out.println(journey.length);
        while(MOVES > 0){
            for(int i = 0; i< coords.size() -1;i++){
                System.out.println("long:"  +coords.get(i).longitude + " lat:" + coords.get(i).latitude);
                ArrayList<LongLat> path = pathFinder.nodeToList(pathFinder.getPath(coords.get(i).toNode(), (coords.get(i+1)).toNode()));
                System.out.println(path.size());
                MOVES -= path.size() + 1; // +1 for hover move
                if(MOVES<=0){
                    System.out.println();
                }
                for(LongLat move : path){
                    completePlan.add(move);
                }
                completePlan.add(path.get(path.size()-1)); //get target location for hovering position
            }

            break;

        }
        System.out.println( MOVES);
        return toLineString(completePlan);

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

    private ArrayList<LongLat> orderToPath(String[] orders){
        ArrayList<LongLat> coords = new ArrayList<>();
        HashMap<String,Order> orders1 = OrdersIO.getOrders();
        LongLat appletone = new LongLat(-3.186874,55.944494 );
        coords.add(appletone);
        HashMap<String,OrderItems> orderItems = OrdersIO.getOrderItems();
        double dist = Double.POSITIVE_INFINITY;
        for(String order:orders){
            ArrayList<LongLat> itemShops = new ArrayList<>();
            LinkedHashSet<String> itemShopsNoDuplicate = new LinkedHashSet<>();

            LongLat pickupLocation = words.getLongLatFrom3Words(orders1.get(order).deliverTo);

            for(String item :orderItems.get(order).items ){
                String restaurantLocation = menus.getLocationOfMenuItem(item);
                itemShopsNoDuplicate.add(restaurantLocation);
            }

            for(String restaurantLocation: itemShopsNoDuplicate){
                LongLat resLocation = words.getLongLatFrom3Words(restaurantLocation);
                if(pickupLocation.distanceTo(resLocation) > dist){
                    dist = pickupLocation.distanceTo(resLocation);
                    itemShops.add(0,resLocation);
                    System.out.println("closest:" + resLocation.longitude + " " + resLocation.latitude);
                }else{
                    itemShops.add(resLocation);
                }
                System.out.println(resLocation.longitude + " " + resLocation.latitude);
            }

            System.out.println(itemShopsNoDuplicate.size());
            System.out.println(orderItems.get(order).items);
            System.out.println(itemShops.size());
            System.out.println(itemShops);

            System.out.println(itemShopsNoDuplicate.size());


            for(LongLat coord :  itemShops){
                System.out.println("nodup: "+ coord.longitude + " " + coord.latitude);
                coords.add(coord);
            }
            coords.add(words.getLongLatFrom3Words(orders1.get(order).deliverTo));
        }

        coords.add(appletone);
        return coords;


    }

}
