package uk.ac.ed.inf;

import com.mapbox.geojson.LineString;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class AStarPathFinder {
    NoFlyZone noFlyZone = new NoFlyZone();
    public ArrayList<LongLat> findRoute(LongLat startLocation, LongLat targetLocation){

        PriorityQueue<Path> openList = new PriorityQueue<>();
        var initialMoves = new ArrayList<LongLat>();


        initialMoves.add(startLocation);
        var firstPath = new Path(targetLocation,initialMoves);
        openList.add(firstPath);
        while(!openList.isEmpty()){
            var path = openList.poll();
            if(path.next.closeTo(targetLocation)){
                System.out.println(path.getMoves().size());
                return path.getMoves();
            }
            ArrayList<Path> nextMoves;
            int movesToTargetLeft = (int) (path.getDistToTarget() / Constants.MOVE_DIST);

            nextMoves = getSurroundingMoves(path);
            if(movesToTargetLeft > 5){
                Collections.sort(nextMoves);
                int n = (18/movesToTargetLeft) +1 ;
                var result = new ArrayList<Path>();
                for (int i=0; i< n; i++){
                    result.add(nextMoves.get(i));
                }
                nextMoves = result;

            }
            for(Path move: nextMoves){
                openList.add(move);
            }

        }
        return null;

    }

    private ArrayList<Path> getSurroundingMoves(Path path) {
        ArrayList<Path> nextMoves = new ArrayList<>();
        for(int angle=0; angle< 360; angle+=10){
            LongLat point = path.next.nextPosition(angle);
            Line2D possibleMove = new Line2D.Double(path.next.longitude,path.next.latitude,point.longitude,point.latitude);
            if(!noFlyZone.intersect(possibleMove)&& point.isConfined()){
                ArrayList<LongLat> newPath = (ArrayList<LongLat>) path.getMoves().clone();
                newPath.add(point);
                nextMoves.add(new Path(path.target,newPath));
            }

        }
        return nextMoves;
    }


}
