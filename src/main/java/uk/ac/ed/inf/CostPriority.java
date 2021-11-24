package uk.ac.ed.inf;

import java.util.*;

public class CostPriority implements JourneyPlanner{
    @Override
    public ArrayList<String> planJourney(String[] orders) {
        HashMap<String,Integer> orderByCost = new HashMap<>();
        HashMap<String,OrderItems> orderItems = OrdersIO.getOrderItems();
        HashMap<String,Order> orderList = OrdersIO.getOrders();
        ArrayList<String> output = new ArrayList<>();
        for(String order:orders){
            orderByCost.put(orderList.get(order).orderNo, Menus.getDeliveryCost(orderItems.get(order).items));
        }
        LinkedHashMap<String, Integer> reverseSortedMap = new LinkedHashMap<>();

//Use Comparator.reverseOrder() for reverse ordering
        orderByCost.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

        for(String order: reverseSortedMap.keySet()){
            System.out.println(reverseSortedMap.get(order));
            output.add(order);
        }

        return output;
    }
}
