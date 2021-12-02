package uk.ac.ed.inf;

import java.util.*;

public class TwoOpt implements JourneyPlanner {
    //used from
    //https://www.technical-recipes.com/2017/applying-the-2-opt-algorithm-to-traveling-salesman-problems-in-java/

    OrderPlanner orderPlanner = new OrderPlanner();
    @Override
    public ArrayList<String> planJourney(String[] orders) {

        String[] newJourney = orders.clone();
        System.out.println("Starting Cost is: " + getJourneyCost(orders));
        int iterations = 0;
        while (iterations <200) {
            double currentBestDist = getJourneyCost(orders);
            for (int i = 0; i < orders.length - 1; i++) {
                for (int j = i + 1; j < orders.length; j++) {
                    TwoOptSwap(i, j, orders, newJourney);
                    double newDist = getJourneyCost(newJourney);

                    if (newDist > currentBestDist) {
                        iterations = 0;
                        orders =  newJourney.clone();

                        currentBestDist = newDist;
                        System.out.println(currentBestDist);
                    }
                }
            }
            iterations++;
        }

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


    private double getJourneyCost(String[] orders){
        double totalCost = 0;
        for(int i=0; i< orders.length -1; i++){
            totalCost += getOrderHeuristic(orders[i], orders[i+1]);
        }
        return totalCost;
    }


    private double getOrderHeuristic(String currentOrder, String nextOrder){
        HashMap<String,Order> orderList = DatabaseIO.getOrders();
        HashMap<String, OrderDetails> orderItems = DatabaseIO.getOrderDetails();
        orderList.get(currentOrder);

        HashMap<String,LongLat> itemShops = new HashMap<>();
        for(String item :orderItems.get(nextOrder).getItems() ){
            String w3wRestautantLocation = Menus.getW3WOfMenuItem(item);
            LongLat restaurantLocation = Menus.getLocationOfMenuItem(item);
            itemShops.put(w3wRestautantLocation, restaurantLocation);
        }

        double totalDist = 0.0;

        totalDist += orderList.get(currentOrder).getDeliverTo().distanceTo(itemShops.get(0));

        for(int i=0; i< itemShops.size()-1;i++){
            totalDist+= itemShops.get(i).distanceTo(itemShops.get(i+1));
        }

        totalDist+= itemShops.get(itemShops.size()-1).distanceTo(orderList.get(nextOrder).getDeliverTo());

        int price =  Menus.getDeliveryCost(orderItems.get(nextOrder).getItems());

        return totalDist/price;

    }

}
