package uk.ac.ed.inf;

import java.util.*;

public class CostPriority implements JourneyPlanner{
    OrderPlanner planner = new OrderPlanner();
    HashMap<String,Integer> orderByPrice = new HashMap<>();
    HashMap<String,OrderItems> orderItems = DatabaseIO.getOrderItems();
    HashMap<String,Order> orderList = DatabaseIO.getOrders();
    ArrayList<String> orderedByPrice = new ArrayList<>();
    @Override
    public ArrayList<String> planJourney(String[] orders) {
        for(String order:orders){
            orderByPrice.put(orderList.get(order).getOrderNo(), Menus.getDeliveryCost(orderItems.get(order).getItems()));
        }
        LinkedHashMap<String, Integer> reverseSortedMap = new LinkedHashMap<>();

//Use Comparator.reverseOrder() for reverse ordering
        orderByPrice.entrySet()
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
