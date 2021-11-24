package uk.ac.ed.inf;

import java.awt.geom.Line2D;
import java.util.*;

public class AStar implements PathFInder{
    @Override
    public Node getPath(Node start, Node target){
        PriorityQueue<Node> openList = new PriorityQueue<>();
        PriorityQueue<Node> closedList = new PriorityQueue<>();
        HashMap<LongLat, Node> nodes = new HashMap<>();

        start.g = 0;

        start.f = start.g + 0.8*start.chebyshevDist(target) + 1.55*start.distanceTo(target);
        openList.add(start);

        while (!openList.isEmpty()){
            Node path = openList.peek();
            if(path.closeTo(target)){
                return path;
            }

            for (int angle =0; angle<360; angle+=30){
                var point = path.nextPosition(angle);
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
                        point.f = point.g + 0.75*point.chebyshevDist(target)+ 1.5*point.distanceTo(target)  ;
                        openList.add(point);
                    }else if(actualDistMoved < point.g){
                        point.parent = path;
                        point.g = actualDistMoved;
                        point.f = point.g + 0.75*point.chebyshevDist(target) + 1.5*point.distanceTo(target) ;
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

    public ArrayList<LongLat> nodeToList(Node path){
        ArrayList<LongLat> coords = new ArrayList<>();
        coords.add(path.toLongLat());
        while (path.parent != null){
            coords.add(path.toLongLat());
            path = path.parent;

        }
        Collections.reverse(coords);
        return coords;
    }
}
