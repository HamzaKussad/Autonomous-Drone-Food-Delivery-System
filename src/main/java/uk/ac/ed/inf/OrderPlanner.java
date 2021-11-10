package uk.ac.ed.inf;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderPlanner {
    ServerClient client = new ServerClient("localhost", "9898");
    Menus menu = new Menus("localhost", "9898");


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
        HashMap<String,Order> orderList = DatabaseClient.getOrders();
        HashMap<String,OrderItems> orderItems = DatabaseClient.getOrderItems();
        orderList.get(currentOrder);
        double dist = Double.POSITIVE_INFINITY;
        for (String item : orderItems.get(nextOrder).items){
            LongLat restaurantLocation = client.getLocationOfMenuItem(item);
            LongLat pickupLocation = client.getLongLatFrom3Words(orderList.get(currentOrder).deliverTo);
            //do as in Drone.java
            if(pickupLocation.distanceTo(restaurantLocation) < dist){
                dist = pickupLocation.distanceTo(restaurantLocation);
            }

        }
        int price = menu.getDeliveryCost(orderItems.get(nextOrder).items);

        return dist * 1/price;

    }

//    public ArrayList getItemsLocations(String orderNumber){
//
//    }

    public double getJourneyCost(String[] orders){
        double totalCost = 0;
        for(int i=0; i< orders.length -1; i++){
            totalCost += getOrderHeuristic(orders[i], orders[i+1]);
        }
        return totalCost;
    }

    public double getJourneyDistance(LongLat[] journey){
        double totalDist = 0;
        for (int i=0; i< journey.length -1; i++){
            totalDist += journey[i].distanceTo(journey[i+1]);

        }
        return totalDist;

    }

}
