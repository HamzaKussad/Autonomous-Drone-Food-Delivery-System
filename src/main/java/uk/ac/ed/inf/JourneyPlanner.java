package uk.ac.ed.inf;

import java.util.*;
import java.util.stream.Collectors;

public class JourneyPlanner {
    //used from
    //https://www.technical-recipes.com/2017/applying-the-2-opt-algorithm-to-traveling-salesman-problems-in-java/

    Menus menus;
    OrderPlanner orderPlanner = new OrderPlanner();

    public ArrayList<String> planJourney(String[] orders) {

        String[] newJourney = orders.clone();
        OrderPlanner planner = new OrderPlanner();
        System.out.println("Starting Dist is: " + getJourneyCost(orders));
        int iterations = 0;
        while (iterations <1) {
            double currentBestDist = getJourneyCost(orders);
            for (int i = 0; i < orders.length - 1; i++) {
                for (int j = i + 1; j < orders.length; j++) {
                    TwoOptSwap(i, j, orders, newJourney);
                    double newDist = getJourneyCost(newJourney);

                    if (newDist < currentBestDist) {
                        iterations = 0;
                        orders =  newJourney.clone();

                        currentBestDist = newDist;
                        System.out.println(currentBestDist);
                    }
                }
            }
            System.out.println(iterations);
            iterations++;
        }
        System.out.println(orders);
        return new ArrayList<>(Arrays.asList(orders));
    }

    private void TwoOptSwap(int i, int j, String[] journey, String[] newJourney) {
        for(int c = 0; c<= i-1; ++c){
            newJourney[c] = journey[c];
        }
        int dec = 0;
        for(int c = i; c<=j; ++c){
            newJourney[c] = journey[j-dec];
            dec++;
        }
        for(int c = j+1; c<newJourney.length; ++c){
            newJourney[c] = journey[c];
        }

    }



    public ArrayList getOrdersExp(String[] orders){

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
        String[] outputOrder = output.toArray(new String[output.size()]);
        return output;

    }

//    public ArrayList getItemsLocations(String orderNumber){
//
//    }

    public double getJourneyCost(String[] orders){
        double totalCost = 0;
        for(int i=0; i< orders.length -1; i++){
            totalCost += orderPlanner.getOrderHeuristic(orders[i], orders[i+1]);
        }
        return totalCost;
    }

}
