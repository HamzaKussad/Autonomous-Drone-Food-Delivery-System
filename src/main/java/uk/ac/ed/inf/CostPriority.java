package uk.ac.ed.inf;

import java.util.*;

/**
 * CostPriority is a JourneyPlanner algorithm that orders the delivery orders based
 * on the cost of an orders (most expensive orders first)
 */

public class CostPriority implements JourneyPlanner{

    /**
     * The function first get all the necessary data from the database
     * (List of items "orderList" and list of items in an order "orderItems")
     * it then places each order with its corresponding in a hashmap, then
     * sorts the hashmap according to the prices (higher first)
     * then the function gets there orderId sorted and collects them
     * in an array and returns them
     * @param orders array of orderIds
     * @return ArrayList of orderIds sorted based on order cost (higher first)
     */

    @Override
    public ArrayList<String> planJourney(String[] orders) {
        HashMap<String,Integer> orderByCost = new HashMap<>();
        ArrayList<String> orderedByPrice = new ArrayList<>();

        HashMap<String, OrderDetails> orderItems = DatabaseIO.getOrderDetails();
        HashMap<String,Order> orderList = DatabaseIO.getOrders();

        for(String order:orders){
            orderByCost.put(orderList.get(order).getOrderNo(), Menus.getDeliveryCost(orderItems.get(order).getItems()));
        }
        LinkedHashMap<String, Integer> reverseSortedMap = new LinkedHashMap<>();

        //Use Comparator.reverseOrder() for reverse ordering
        orderByCost.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

        for(String order: reverseSortedMap.keySet()){
            System.out.println(reverseSortedMap.get(order));
            orderedByPrice.add(order);
        }

//        for (int i =0; i< orderedByPrice.size()-1;i++){
//            double orderDist = planner.getOrderDistance(orderedByPrice.get(i),orderedByPrice.get(i+1));
//            double orderCost = Menus.getDeliveryCost(orderItems.get(orderedByPrice.get(i)).getItems());
//
//            orderByCostAndPrice.put(orderedByPrice.get(i), 400000*orderDist/orderCost);
//
//        }
//        orderByCostAndPrice.put(orderedByPrice.get(orderedByPrice.size()-1), (double) Menus.getDeliveryCost(orderItems.get(orderedByPrice.get(orderedByPrice.size()-1)).getItems()));
//
//
//        LinkedHashMap<String, Double> reverseSortedMapByCostAndPrice = new LinkedHashMap<>();
//
////Use Comparator.reverseOrder() for reverse ordering
//        orderByCostAndPrice.entrySet()
//                .stream()
//                .sorted(Map.Entry.comparingByValue())
//                .forEachOrdered(x -> reverseSortedMapByCostAndPrice.put(x.getKey(), x.getValue()));
//
//        for(String order: reverseSortedMapByCostAndPrice.keySet()){
//            System.out.println(reverseSortedMapByCostAndPrice.get(order));
//            orderedByPriceAndDist.add(order);
//        }

        return orderedByPrice;
    }

}
