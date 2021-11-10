package uk.ac.ed.inf;

import java.util.ArrayList;

public class JourneyPlanner {
    //used from
    //https://www.technical-recipes.com/2017/applying-the-2-opt-algorithm-to-traveling-salesman-problems-in-java/

    public String[] planJourney(String[] orders) {
        String[] newJourney = orders.clone();
        OrderPlanner planner = new OrderPlanner();
        System.out.println("Starting Dist is: " + planner.getJourneyCost(orders));
        int iterations = 0;
        while (iterations < 1) {
            double currentBestDist = planner.getJourneyCost(orders);
            for (int i = 0; i < orders.length - 1; i++) {
                for (int j = i + 1; j < orders.length; j++) {
                    TwoOptSwap(i, j, orders, newJourney);
                    double newDist = planner.getJourneyCost(newJourney);

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
        return orders;
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

}
