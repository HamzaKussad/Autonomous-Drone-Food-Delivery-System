package uk.ac.ed.inf;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class AStar {
    NoFlyZone noFlyZone = new NoFlyZone();

    public AStarNode getPath(AStarNode start, AStarNode target){
        PriorityQueue<AStarNode> openList = new PriorityQueue<>();
        PriorityQueue<AStarNode> closedList = new PriorityQueue<>();

        start.f = start.g + start.distanceTo(target);
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
                if(!noFlyZone.intersect(possibleMove) && point.isConfined()){
                    double actualDistMoved = path.g + Constants.MOVE_DIST;
                    double movesToTargetLeft = point.distanceTo(target) / Constants.MOVE_DIST;
                    if(!openList.contains(point) && !closedList.contains(point)){
                        point.parent = path;
                        point.g = actualDistMoved;
                        point.f = point.g + point.heuristicDist(target) + 0.5*point.distanceTo(target);
                        openList.add(point);
                    }else if(actualDistMoved < point.g){
                        point.parent = path;
                        point.g = actualDistMoved;
                        point.f = point.g + point.heuristicDist(target) + 0.5*point.distanceTo(target);
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
