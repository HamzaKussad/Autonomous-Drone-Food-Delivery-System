package uk.ac.ed.inf;

import java.awt.geom.Line2D;
import java.util.*;


/**
 * A class that implements the Astar path finding algorithm
 */
public class AStar implements PathFInder{
    @Override

    /**
     * The main function of the class,
     */
    public Node getPath(Node start, Node target){
        PriorityQueue<Node> openList = new PriorityQueue<>();
        PriorityQueue<Node> closedList = new PriorityQueue<>();
        HashMap<LongLat, Node> nodes = new HashMap<>();

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
                Line2D possibleMove = new Line2D.Double(path.longitude, path.latitude,point.longitude,point.latitude);
                if(!NoFlyZone.intersect(possibleMove) && point.isConfined()){

                    if(nodes.containsKey(point.toLongLat())){
                        point = nodes.get(point.toLongLat());
                    }else {
                        nodes.put(point.toLongLat(),point);
                    }

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

    public ArrayList<LongLat> nodeToList(Node path){
        ArrayList<LongLat> coords = new ArrayList<>();
        coords.add(path.toLongLat());
        while (path.parent != null){
            path = path.parent;
            coords.add(path.toLongLat());
        }

        Collections.reverse(coords);
        return coords;
    }

   //look into to fix or move to Drone

    public ArrayList<Flightpath> nodeToFlightpath(Node path){
        ArrayList<Flightpath> flightpaths = new ArrayList<>();
        ArrayList<Node> nodes = new ArrayList<>();
        nodes.add(path);
        while (path.parent != null){
            path = path.parent;
            nodes.add(path);

//            System.out.println(path.angle);
        }
        Collections.reverse(nodes);

        for(int i=0; i< nodes.size()-1; i++){
            Flightpath flightpath = new Flightpath();
            flightpath.setFromLatitude(nodes.get(i).latitude);
            flightpath.setFromLongitude(nodes.get(i).longitude);
            flightpath.setAngle(nodes.get(i+1).angle);
            flightpath.setToLatitude(nodes.get(i+1).latitude);
            flightpath.setToLongitude(nodes.get(i+1).longitude);
            flightpaths.add(flightpath);

        }
        Node last = nodes.get(nodes.size()-1);
        Flightpath flightpath = new Flightpath();
        flightpath.setFromLatitude(last.latitude);
        flightpath.setFromLongitude(last.longitude);
        flightpath.setAngle(-999);
        flightpath.setToLatitude(last.latitude);
        flightpath.setToLongitude(last.longitude);
        flightpaths.add(flightpath);

        return flightpaths;
    }
}
