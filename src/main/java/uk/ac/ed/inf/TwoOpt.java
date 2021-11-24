package uk.ac.ed.inf;

import java.util.*;
import java.util.stream.Collectors;

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


    public double getJourneyCost(String[] orders){
        double totalCost = 0;
        for(int i=0; i< orders.length -1; i++){
            totalCost += orderPlanner.getOrderHeuristic(orders[i], orders[i+1]);
        }
        return totalCost;
    }

}
