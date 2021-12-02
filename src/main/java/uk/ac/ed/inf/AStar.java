package uk.ac.ed.inf;

import java.awt.geom.Line2D;
import java.util.*;


/**
 * A class that implements the Astar path finding algorithm
 */
public class AStar implements PathFinder {

    /**
     * AStar algorithm to calculate the path between 2 points
     * @param start starting node
     * @param target target node
     * @return the path between the start and target as a Node Object
     */

    @Override
    public Node getPath(Node start, Node target){
        PriorityQueue<Node> openList = new PriorityQueue<>();
        PriorityQueue<Node> closedList = new PriorityQueue<>();

        start.g = 0;
        start.f = start.g + 0.75*start.chebyshevDist(target) + 1.6*start.distanceTo(target);

        openList.add(start);
        while (!openList.isEmpty()){
            Node path = openList.peek();
            if(path.closeTo(target)){
                return path;
            }

            for (int angle =0; angle<360; angle+=60){
                var point = path.nextPosition(angle);
                point.angle = angle;
                Line2D possibleMove = new Line2D.Double(path.getLongitude(), path.getLatitude(),point.getLongitude(),point.getLatitude());
                if(!NoFlyZone.intersect(possibleMove) && point.isConfined()){

                    double actualDistMoved = path.g + Constants.MOVE_DIST;
                    if(!openList.contains(point) && !closedList.contains(point)){
                        point.parent = path;
                        point.g = actualDistMoved;
                        point.f = point.g + 0.75*point.chebyshevDist(target)+ 1.6*point.distanceTo(target);
                        openList.add(point);
                    }else if(actualDistMoved < point.g){
                        point.parent = path;
                        point.g = actualDistMoved;
                        point.f = point.g + 0.75*point.chebyshevDist(target) + 1.6*point.distanceTo(target);
                        if(closedList.contains(point)){
                            closedList.remove(point);
                            openList.add(point);
                        }
                    }

                }
            }
            openList.remove(path);
            closedList.add(path);

        }
        return null;
    }
    /**
     * This function takes the result node and breaks it down to an
     * array of Coordinates with each LongLat being a move away from
     * the previous one
     * @param path Result node of the Astar algorithm
     * @return ArrayList of LongLats
     */



}
