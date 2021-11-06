package uk.ac.ed.inf;

import java.util.ArrayList;

public class JourneyPlanner {
    //used from
    //https://www.technical-recipes.com/2017/applying-the-2-opt-algorithm-to-traveling-salesman-problems-in-java/

    public LongLat[] planJourney(LongLat[] journey) {
        LongLat[] newJourney = journey.clone();
        OrderPlanner planner = new OrderPlanner();
        System.out.println("Starting Dist is: " + planner.getJourneyDistance(journey));
        int iterations = 0;
        while (iterations < 400) {
            double currentBestDist = planner.getJourneyDistance(journey);
            for (int i = 0; i < journey.length - 1; i++) {
                for (int j = i + 1; j < journey.length; j++) {
                    TwoOptSwap(i, j, journey, newJourney);
                    double newDist = planner.getJourneyDistance(newJourney);

                    if (newDist < currentBestDist) {
                        iterations = 0;
                        journey =  newJourney.clone();

                        currentBestDist = newDist;
                    }
                }
            }
            iterations++;
        }
        return journey;
    }

    private void TwoOptSwap(int i, int j, LongLat[] journey, LongLat[] newJourney) {
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
