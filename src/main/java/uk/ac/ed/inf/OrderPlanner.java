package uk.ac.ed.inf;

import java.util.ArrayList;
import java.util.HashMap;

public class OrderPlanner {

    public ArrayList<LongLat> orderToPathTest(String order){
        ArrayList<LongLat> coords = new ArrayList<>();
        HashMap<String,Order> ordersDB = DatabaseIO.getOrders();
        LongLat appletone = new LongLat(-3.186874,55.944494 );

        HashMap<String, OrderDetails> orderItems = DatabaseIO.getOrderDetails();
        ArrayList<LongLat> itemShops;
        HashMap<String,LongLat> itemShopsNoDuplicate = new HashMap<>();


        for(String item : orderItems.get(order).getItems() ){
//            System.out.println(order);
//            System.out.println(item);
            String w3wRestaurantLocation = Menus.getW3WOfMenuItem(item);
//            System.out.println( w3wRestaurantLocation);
            LongLat restaurantLocation = Menus.getLocationOfMenuItem(item);

            itemShopsNoDuplicate.put(w3wRestaurantLocation, restaurantLocation);
        }
        LongLat dropOffLocation = (ordersDB.get(order).getDeliverTo());
//        System.out.println(dropOffLocation);
        itemShops =  sortRestaurantPickups( itemShopsNoDuplicate, dropOffLocation);
//        System.out.println(itemShops.size());
//
//        System.out.println(itemShops.size());
        for(LongLat coord :  itemShops){
            coords.add(coord);
        }
        coords.add(dropOffLocation);

        System.out.println(coords.size());
    return coords;
    }


    private ArrayList<LongLat> sortRestaurantPickups( HashMap<String, LongLat> itemShopsNoDuplicate, LongLat dropOffLocation) {
        ArrayList<LongLat> itemShops = new ArrayList<>();
        ArrayList<LongLat> locations = new ArrayList<>();
        double dist = Double.POSITIVE_INFINITY;
        for(String restaurantLocation: itemShopsNoDuplicate.keySet()){
            locations.add(itemShopsNoDuplicate.get(restaurantLocation));
//            if(dropOffLocation.distanceTo(itemShopsNoDuplicate.get(restaurantLocation)) < dist){
//                dist = dropOffLocation.distanceTo(itemShopsNoDuplicate.get(restaurantLocation));
//                itemShops.add(0,itemShopsNoDuplicate.get(restaurantLocation));
//            }else{
//                itemShops.add(itemShopsNoDuplicate.get(restaurantLocation));
//            }
        }

        if(locations.size()>1){
            double distA = locations.get(0).distanceTo(locations.get(1)) + locations.get(1).distanceTo(dropOffLocation);
            double distB = locations.get(1).distanceTo(locations.get(0)) + locations.get(0).distanceTo(dropOffLocation);
//            System.out.println(distA);
//            System.out.println(distB);
            if(distA>distB){
                itemShops.add(locations.get(1));
                itemShops.add(locations.get(0));
            }
            else{
                itemShops.add(locations.get(0));
                itemShops.add(locations.get(1));
            }
        }else{
            itemShops.add(locations.get(0));
        }


//        System.out.println(itemShops);

        return itemShops;

    }

}
