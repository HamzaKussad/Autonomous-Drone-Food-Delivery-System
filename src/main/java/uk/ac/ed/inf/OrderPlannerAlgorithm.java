package uk.ac.ed.inf;

import java.util.ArrayList;

public interface OrderPlannerAlgorithm {
    ArrayList<LongLat> orderToStops(String order);
}
