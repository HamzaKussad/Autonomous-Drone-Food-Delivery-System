package uk.ac.ed.inf;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A class to that is responsible to implement the algorithm that
 * gets the optimum ordering of the shops that the drone should fly to
 */

public class OrderPlanner implements OrderPlannerAlgorithm {

    /**
     * This function takes in an order and return the LongLat
     * locations in a specific ordering which corresponds to the
     * restaurants and the drop-off location at the end
     * @param order orderID
     * @return ArrayList of LongLat coordinates
     */

    public ArrayList<LongLat> orderToStops(String order){
        ArrayList<LongLat> coords = new ArrayList<>();
        HashMap<String,Order> ordersDB = DatabaseIO.getOrders();
        HashMap<String, OrderDetails> orderItems = DatabaseIO.getOrderDetails();
        HashMap<String,LongLat> itemShopsNoDuplicate = new HashMap<>();

        for(String item : orderItems.get(order).getItems() ){

            String w3wRestaurantLocation = Menus.getW3WOfMenuItem(item);
            LongLat restaurantLocation = Menus.getLocationOfMenuItem(item);
            itemShopsNoDuplicate.put(w3wRestaurantLocation, restaurantLocation);

        }
        LongLat dropOffLocation = (ordersDB.get(order).getDeliverTo());

        ArrayList<LongLat> itemShops =  sortRestaurantPickups( itemShopsNoDuplicate, dropOffLocation);

        for(LongLat coord :  itemShops){
            coords.add(coord);
        }
        coords.add(dropOffLocation);

        System.out.println(coords.size());
    return coords;
    }

    /**
     * Helper function to sort the restaurants in a specific order
     * depending on the distance from the drop-off location
     * @param itemShopsNoDuplicate Hashmap with W3W as key and its the corresponding LongLat as value
     * @param dropOffLocation drop-off location of the order
     * @return restaurants LongLat locations sorted
     */

    private ArrayList<LongLat> sortRestaurantPickups( HashMap<String, LongLat> itemShopsNoDuplicate, LongLat dropOffLocation) {
        ArrayList<LongLat> itemShops = new ArrayList<>();
        ArrayList<LongLat> locations = new ArrayList<>();

        for(String restaurantLocation: itemShopsNoDuplicate.keySet()){
            locations.add(itemShopsNoDuplicate.get(restaurantLocation));
        }

        if(locations.size()>1){
            double distA = locations.get(0).distanceTo(locations.get(1)) + locations.get(1).distanceTo(dropOffLocation);
            double distB = locations.get(1).distanceTo(locations.get(0)) + locations.get(0).distanceTo(dropOffLocation);

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

        return itemShops;

    }

}
