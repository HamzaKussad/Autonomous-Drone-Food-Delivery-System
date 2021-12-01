package uk.ac.ed.inf;

import java.util.ArrayList;
import java.util.HashMap;

public class OrderPlanner {

    public ArrayList<LongLat> orderToPath(ArrayList<String> orders){
        ArrayList<LongLat> coords = new ArrayList<>();
        HashMap<String,Order> ordersDB = DatabaseIO.getOrders();
        ArrayList<Flightpath> flightpaths = new ArrayList<>();
        LongLat appleton = new LongLat(-3.186874,55.944494 );
        coords.add(appleton);
        HashMap<String,OrderItems> orderItems = DatabaseIO.getOrderItems();

        ArrayList<LongLat> itemShops;

        HashMap<String,LongLat> itemShopsNoDuplicate = new HashMap<>();

        for(String item : orderItems.get(orders.get(0)).getItems() ){
            String w3wRestaurantLocation = Menus.getW3WOfMenuItem(item);
            LongLat restaurantLocation = Menus.getLocationOfMenuItem(item);
            itemShopsNoDuplicate.put(w3wRestaurantLocation, restaurantLocation);
        }

        itemShops =  sortRestaurantPickups( itemShopsNoDuplicate, appleton);

        for(LongLat coord :  itemShops){
            coords.add(coord);
        }

        for(int i =0; i< orders.size()-1;i++){

            System.out.println(orders.get(i));

            LongLat dropOffLocation = (ordersDB.get(orders.get(i)).getDeliverTo());
            coords.add(dropOffLocation);

            itemShopsNoDuplicate = new HashMap<>();
            for(String item :orderItems.get(orders.get(i+1)).getItems() ){
                System.out.println(item);
                String w3wRestaurantLocation = Menus.getW3WOfMenuItem(item);
                System.out.println(w3wRestaurantLocation);
                LongLat restaurantLocation = Menus.getLocationOfMenuItem(item);
                itemShopsNoDuplicate.put(w3wRestaurantLocation, restaurantLocation);
            }

            itemShops =  sortRestaurantPickups( itemShopsNoDuplicate, dropOffLocation);

            for(LongLat coord :  itemShops){
                System.out.println(coord.latitude);
                System.out.println(coord.longitude);
                coords.add(coord);
            }

        }
        LongLat lastOrderDropOff = (ordersDB.get(orders.get(orders.size()-1)).getDeliverTo());

        coords.add(lastOrderDropOff);
        coords.add(appleton);
        return coords;

    }

    public ArrayList<LongLat> orderToPathTest(String order){
        ArrayList<LongLat> coords = new ArrayList<>();
        HashMap<String,Order> ordersDB = DatabaseIO.getOrders();
        LongLat appletone = new LongLat(-3.186874,55.944494 );

        HashMap<String,OrderItems> orderItems = DatabaseIO.getOrderItems();
        ArrayList<LongLat> itemShops;
        HashMap<String,LongLat> itemShopsNoDuplicate = new HashMap<>();


        for(String item : orderItems.get(order).getItems() ){
            System.out.println(order);
            System.out.println(item);
            String w3wRestaurantLocation = Menus.getW3WOfMenuItem(item);
            System.out.println( w3wRestaurantLocation);
            LongLat restaurantLocation = Menus.getLocationOfMenuItem(item);

            itemShopsNoDuplicate.put(w3wRestaurantLocation, restaurantLocation);
        }
        LongLat dropOffLocation = (ordersDB.get(order).getDeliverTo());
        System.out.println(dropOffLocation);
        itemShops =  sortRestaurantPickups( itemShopsNoDuplicate, dropOffLocation);
        System.out.println(itemShops.size());

        System.out.println(itemShops.size());
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
            System.out.println(distA);
            System.out.println(distB);
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


        System.out.println(itemShops);

        return itemShops;

    }

}
