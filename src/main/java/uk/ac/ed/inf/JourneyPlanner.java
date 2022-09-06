package uk.ac.ed.inf;

import java.util.ArrayList;

/**
 * Interface for the JourneyPlanner Algorithm
 *
 * Interface created in-case new algorithms were to be used
 */

public interface JourneyPlanner {
    /**
     * This function takes in an array of orderIDs, apply a certain algorithm to
     * the order array to order the orders in a way that achieves the best
     * "sampled average percentage monetary value"
     * @param orders array of orderIDs
     * @return ArrayList of ordered orderIds to be used
     */
    ArrayList<String> planJourney(String[] orders);
}
