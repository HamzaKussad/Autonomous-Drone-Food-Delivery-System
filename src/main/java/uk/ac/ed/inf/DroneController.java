package uk.ac.ed.inf;

import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;


import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.graph.AsUndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class DroneController {

    LongLat current;
    LongLat next;


    public LineString getRoute(List<LongLat> locations){
        ArrayList<Move> moves = new ArrayList<>();
        List<Point> points = new ArrayList<>();
        int movesLeft = 1500;

        NoFlyZone noFlyZone = new NoFlyZone();
        noFlyZone.noFlyZone();


        current = locations.get(0);
        points.add(Point.fromLngLat(current.longitude,current.latitude));
        int locationIndex = 1;


        while(locationIndex < locations.size()){
            LongLat target = locations.get(locationIndex);
            while(!current.closeTo(target)){

                int moveAngle = current.getAngle(target);

                next = current.nextPosition(moveAngle);

                if(!next.isConfined()){
                    break;
                }

                Line2D movePath = new Line2D.Double(current.longitude,current.latitude,next.longitude,next.latitude);

                if(noFlyZone.intersect(movePath)){
                    double distance = Double.POSITIVE_INFINITY;
                    for(int angle=0; angle< 360; angle+=10){
                        LongLat possibleNext = current.nextPosition( angle);
                        Line2D possibleMove = new Line2D.Double(current.longitude,current.latitude,possibleNext.longitude,possibleNext.latitude);
                        if(!noFlyZone.intersect(possibleMove) || !possibleNext.isConfined()){
                            if(possibleNext.distanceTo(target) < distance && moveAngle - angle >=90){
                                distance = possibleNext.distanceTo(target);
                                next = possibleNext;
                                moveAngle = angle;
                            }
                        }

                    }
                }

                System.out.println(moveAngle);

                Move move = new Move(current,next,moveAngle);
                points.add(Point.fromLngLat(next.longitude,next.latitude));
                moves.add(move);
                System.out.println(moves.size());
                System.out.println(Point.fromLngLat(next.longitude,next.latitude));


                current = next;
                movesLeft -=1;

                if(movesLeft ==0){
                    break;
                }

            }
            locationIndex +=1;
            current = target;



        }
        return LineString.fromLngLats(points);

    }




}
