package uk.ac.ed.inf;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

public class OrderPlanner {

    public ArrayList<LongLat> orderToPath(ArrayList<String> orders){
        ArrayList<LongLat> coords = new ArrayList<>();
        HashMap<String,Order> orders1 = OrdersIO.getOrders();
        LongLat appletone = new LongLat(-3.186874,55.944494 );
        coords.add(appletone);
        HashMap<String,OrderItems> orderItems = OrdersIO.getOrderItems();

        ArrayList<LongLat> itemShops = new ArrayList<>();

        HashMap<String,LongLat> itemShopsNoDuplicate = new HashMap<>();

        for(String item : orderItems.get(orders.get(0)).items ){
            String w3wRestaurantLocation = Menus.getW3WOfMenuItem(item);
            LongLat restaurantLocation = Menus.getLocationOfMenuItem(item);
            itemShopsNoDuplicate.put(w3wRestaurantLocation, restaurantLocation);
        }

        itemShops =  sortRestaurantPickups( itemShopsNoDuplicate, appletone);

        for(LongLat coord :  itemShops){
            coords.add(coord);
        }

        for(int i =0; i< orders.size()-1;i++){

            LongLat dropOffLocation = (orders1.get(orders.get(i)).deliverTo);
            coords.add(dropOffLocation);

            itemShopsNoDuplicate = new HashMap<>();
            for(String item :orderItems.get(orders.get(i+1)).items ){
                String w3wRestaurantLocation = Menus.getW3WOfMenuItem(item);
                LongLat restaurantLocation = Menus.getLocationOfMenuItem(item);
                itemShopsNoDuplicate.put(w3wRestaurantLocation, restaurantLocation);
            }

            itemShops =  sortRestaurantPickups( itemShopsNoDuplicate, dropOffLocation);

            for(LongLat coord :  itemShops){
                coords.add(coord);
            }

        }
        LongLat lastOrderDropOff = (orders1.get(orders.get(orders.size()-1)).deliverTo);

        coords.add(lastOrderDropOff);
        coords.add(appletone);
        return coords;

    }


    public double getOrderHeuristic(String currentOrder, String nextOrder){
        HashMap<String,Order> orderList = OrdersIO.getOrders();
        HashMap<String,OrderItems> orderItems = OrdersIO.getOrderItems();
        orderList.get(currentOrder);

        HashMap<String,LongLat> itemShopsNoDuplicate = new HashMap<>();
        for(String item :orderItems.get(nextOrder).items ){
            String w3wRestautantLocation = Menus.getW3WOfMenuItem(item);
            LongLat restaurantLocation = Menus.getLocationOfMenuItem(item);
            itemShopsNoDuplicate.put(w3wRestautantLocation, restaurantLocation);
        }

        ArrayList<LongLat> itemShops =  sortRestaurantPickups(itemShopsNoDuplicate, orderList.get(currentOrder).deliverTo);

        double totalDist = 0.0;

        totalDist += orderList.get(currentOrder).deliverTo.distanceTo(itemShops.get(0));

        for(int i=0; i< itemShops.size()-1;i++){
            totalDist+= itemShops.get(i).distanceTo(itemShops.get(i+1));
        }

        totalDist+= itemShops.get(itemShops.size()-1).distanceTo(orderList.get(nextOrder).deliverTo);

        int price =  Menus.getDeliveryCost(orderItems.get(nextOrder).items);

        return totalDist/price;

    }

    private ArrayList<LongLat> sortRestaurantPickups( HashMap<String, LongLat> itemShopsNoDuplicate, LongLat dropOffLocation) {
        ArrayList<LongLat> itemShops = new ArrayList<>();
        double dist = Double.POSITIVE_INFINITY;
        for(String restaurantLocation: itemShopsNoDuplicate.keySet()){

            if(dropOffLocation.distanceTo(itemShopsNoDuplicate.get(restaurantLocation)) < dist){
                dist = dropOffLocation.distanceTo(itemShopsNoDuplicate.get(restaurantLocation));
                itemShops.add(0,itemShopsNoDuplicate.get(restaurantLocation));
//                System.out.println("closest:" + resLocation.longitude + " " + resLocation.latitude);
            }else{
                itemShops.add(itemShopsNoDuplicate.get(restaurantLocation));
            }
            // System.out.println(resLocation.longitude + " " + resLocation.latitude);
        }

        return itemShops;

    }

}
