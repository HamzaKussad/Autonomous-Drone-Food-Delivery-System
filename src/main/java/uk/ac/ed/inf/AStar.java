package uk.ac.ed.inf;

import java.awt.geom.Line2D;
import java.util.*;

public class AStar {


    public AStarNode getPath(AStarNode start, AStarNode target){
        PriorityQueue<AStarNode> openList = new PriorityQueue<>();
        PriorityQueue<AStarNode> closedList = new PriorityQueue<>();
        HashMap<LongLat,AStarNode> nodes = new HashMap<>();

        start.g = 0;

        start.f = start.g + 0.8*start.chebyshevDist(target) + 1.55*start.distanceTo(target)   ;
        openList.add(start);

        while (!openList.isEmpty()){
            AStarNode path = openList.peek();
            if(path.closeTo(target)){
                return path;
            }

            ArrayList<AStarNode> nextMoves = new ArrayList<>();
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
                    double movesToTargetLeft = point.distanceTo(target) / Constants.MOVE_DIST;
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
//            int movesLeft =(int) (path.distanceTo(target)/Constants.MOVE_DIST);
//            if(movesLeft >10){
//                Collections.sort(nextMoves);
//                int n = (18/movesLeft)+1;
//                var result = new ArrayList<AStarNode>();
//                for(int i=0;i<n;i++){
//                    result.add(nextMoves.get(i));
//                }
//                nextMoves = result;
//            }
//
//            for(var move: nextMoves){
//                openList.add(move);
//            }

            openList.remove(path);
            closedList.add(path);

        }
        return null;
    }

    public ArrayList<LongLat> nodeToList(AStarNode path){
        ArrayList<LongLat> coords = new ArrayList<>();
        coords.add(path.toLongLat());
        while (path.parent != null){
            coords.add(path.toLongLat());
            path = path.parent;

        }
        Collections.reverse(coords);
        return coords;
    }
    public AStarNode toNode(LongLat point){
        return new AStarNode(point.longitude,point.latitude);
    }

}
