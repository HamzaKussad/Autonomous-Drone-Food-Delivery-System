package uk.ac.ed.inf;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderPlanner {

    Menus menus;
    WordsW3W words;


    //fix all
//    private ArrayList<LongLat> getCustomerLocations(){
//        HashMap<String,Order> orderList = db.getOrders();
//        ArrayList<LongLat> locations = new ArrayList<>();
//        for (String order: orderList.keySet()){
//            locations.add(client.getLongLatFrom3Words(orderList.get(order).deliverTo));
//        }
//        return locations;
//    }
//    public void getLocationsForOrder(String ordernumber){
//        HashMap<String , OrderItems> orderItem = db.orderDetails();
//        List<Restaurant> responseRestaurants = client.getMenus();
//
//        ArrayList<LongLat> locations = new ArrayList<>();
//
//        for(String order: orderItem.keySet()){
//           if (orderItem.get(order).items
//        }
//
//    }

    public double getOrderHeuristic(String currentOrder, String nextOrder){
        HashMap<String,Order> orderList = OrdersIO.getOrders();
        HashMap<String,OrderItems> orderItems = OrdersIO.getOrderItems();
        orderList.get(currentOrder);
        double dist = Double.POSITIVE_INFINITY;
        for (String item : orderItems.get(nextOrder).items){
            LongLat restaurantLocation = words.getLongLatFrom3Words(menus.getLocationOfMenuItem(item));
            LongLat pickupLocation = words.getLongLatFrom3Words(orderList.get(currentOrder).deliverTo);
            //do as in Drone.java
            if(pickupLocation.distanceTo(restaurantLocation) < dist){
                dist = pickupLocation.distanceTo(restaurantLocation);
            }

        }
        int price = menus.getDeliveryCost(orderItems.get(nextOrder).items);

        return dist * 1/(3*price);

    }




    public double getJourneyDistance(LongLat[] journey){
        double totalDist = 0;
        for (int i=0; i< journey.length -1; i++){
            totalDist += journey[i].distanceTo(journey[i+1]);

        }
        return totalDist;

    }

}
