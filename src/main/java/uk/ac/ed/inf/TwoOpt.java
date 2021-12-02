package uk.ac.ed.inf;

import java.util.*;

/**
 * TwoOpt algorithm that could be used to find wanted journey
 * depending on a certain heuristic define below
 */

public class TwoOpt implements JourneyPlanner {
    //used from
    //https://www.technical-recipes.com/2017/applying-the-2-opt-algorithm-to-traveling-salesman-problems-in-java/

    OrderPlanner orderPlanner = new OrderPlanner();
    @Override

    /**
     * This function performs the TwoOpt algorithm to
     * order the orders using a specific heurtistic
     */
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

                    if (newDist < currentBestDist) {
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

    /**
     * TwoOpt swap
     * @param i swaps i with j
     * @param j swaps j with i
     * @param journey the current journey
     * @param newJourney the new journey after swap is done
     */

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

    /**
     * helper function to calculate the total cost of the journey
     * using the heuristic
     * @param orders list of orders
     * @return the cost of this journey
     */

    private double getJourneyCost(String[] orders){
        double totalCost = 0;
        for(int i=0; i< orders.length -1; i++){
            totalCost += getOrderHeuristic(orders[i], orders[i+1]);
        }
        return totalCost;
    }

    /**
     * A heuristic to be used when comparing the costs of the different Journeys
     * It basically calculates the distance done by and order plus the
     * distance from the previous dropoff to the first shop
     * Then calculates the distance/2*price
     * @param previousOrder previous order
     * @param currentOrder the current order
     * @return distance/2*price
     */

    private double getOrderHeuristic(String previousOrder, String currentOrder){
        HashMap<String,Order> orderList = DatabaseIO.getOrders();
        HashMap<String, OrderDetails> orderItems = DatabaseIO.getOrderDetails();
        orderList.get(previousOrder);

        ArrayList<LongLat> stops = orderPlanner.orderToStops(currentOrder);

        LongLat dropOffLocation = orderList.get(previousOrder).getDeliverTo();

        double totalDist = dropOffLocation.distanceTo(stops.get(0));

        for (int i = 0; i< stops.size()-1; i++){
            totalDist += stops.get(i).distanceTo(stops.get(i+1));
        }

        int price =  Menus.getDeliveryCost(orderItems.get(currentOrder).getItems());

        return (totalDist)/(price*2);

    }

}
