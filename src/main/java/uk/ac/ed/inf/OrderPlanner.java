package uk.ac.ed.inf;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

public class OrderPlanner {
    DatabaseClient db = new DatabaseClient();
    ServerClient client = new ServerClient("localhost", "9898");

    public HashMap getOrdersFromDate(Date deliveryDate){
        HashMap<String,Order> orderList = db.getOrders();
        HashMap<String,Order> ordersByDate = new HashMap<>();
        for(String order: orderList.keySet()){
            if (orderList.get(order).deliveryDate == deliveryDate){
                ordersByDate.put(order,orderList.get(order));
            }
        }
        return ordersByDate;
    }
    private ArrayList<LongLat> getOrderLocations(Date deliveryDate){
        HashMap<String,Order> orderList = db.getOrders();
        ArrayList<LongLat> locations = new ArrayList<>();
        for (String order: orderList.keySet()){
            if(orderList.get(order).deliveryDate == deliveryDate){
                locations.add(client.getLongLatFrom3Words(orderList.get(order).deliverTo));
            }
        }
        return locations;
    }
    public void getLocationsForOrder(String ordernumber){
        HashMap<String , OrderItems> orderItem = db.orderDetails();
        HashMap<String,String> locations = client.storeLocationOfMenuItem();
        for(String order: orderItem.keySet()){


        }

    }
    public double getJourneyDistance(LongLat[] journey){
        double totalDist = 0;
        for (int i=0; i< journey.length -1; i++){
            totalDist += journey[i].distanceTo(journey[i+1]);

        }
        return totalDist;

    }

}
