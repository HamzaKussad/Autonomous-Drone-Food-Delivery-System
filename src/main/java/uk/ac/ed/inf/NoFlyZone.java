package uk.ac.ed.inf;

import com.mapbox.geojson.*;

import java.awt.geom.Line2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.Polygon;
import java.awt.*;


public class NoFlyZone {
    ServerClient client = new ServerClient("localhost","9898");

    public static ArrayList<Line2D> obstacleLines = new ArrayList<>();

    public void noFlyZone() {
        FeatureCollection noFlyZone =  client.loadNoFlyZone();

        for(int i = 0; i<noFlyZone.features().size(); i++){
            var shape = (Polygon) noFlyZone.features().get(i).geometry();
            for (int j = 0; j< shape.coordinates().get(0).size() -1; j++) {
                var point1 = shape.coordinates().get(0).get(j).coordinates();
                var point2 = shape.coordinates().get(0).get(j+1).coordinates();

                obstacleLines.add(new Line2D.Double(point1.get(0),point1.get(1), point2.get(0),point2.get(1)));

            }

        }

        obstacleLines.add(new Line2D.Double(-3.187975063920021,55.94476199783985,-3.188435733318329,55.945087182795454));
        obstacleLines.add(new Line2D.Double(-3.188435733318329, 55.945087182795454,-3.1893758475780487, 55.94553778099201));
        System.out.println(obstacleLines.size());

    }
    public boolean intersect(Line2D move){
        for (Line2D obstacle: obstacleLines){
            if (move.intersectsLine(obstacle)){
                return true;
            }
        }
        return false;
    }

}
