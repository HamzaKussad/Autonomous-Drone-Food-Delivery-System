package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.mapbox.geojson.*;

import java.awt.geom.Line2D;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.Polygon;

public class NoFlyZone extends ServerClient {


    private static ArrayList<Line2D> obstacleLines = new ArrayList<>();

    public NoFlyZone(String name, String port) {
        super(name, port);

    }
    public void getNoFlyZone(){
        FeatureCollection noFlyZone = loadNoFlyZone();

        for(int i = 0; i<noFlyZone.features().size(); i++){
            var shape = (Polygon) noFlyZone.features().get(i).geometry();
            for (int j = 0; j< shape.coordinates().get(0).size() -1; j++) {
                var point1 = shape.coordinates().get(0).get(j).coordinates();
                var point2 = shape.coordinates().get(0).get(j+1).coordinates();

                obstacleLines.add(new Line2D.Double(point1.get(0),point1.get(1), point2.get(0),point2.get(1)));

            }

        }
        System.out.println(obstacleLines.size());
    }

    public static boolean intersect(Line2D move){
        for (Line2D obstacle: obstacleLines){
            if (move.intersectsLine(obstacle)){
                return true;
            }
        }
        return false;
    }



   private FeatureCollection loadNoFlyZone(){
        FeatureCollection noFlyZone = null;
        try{
            String endpoint = "/buildings/no-fly-zones.geojson";
            HttpResponse<String> response = doGetRequest(this.name,this.port,endpoint);
            noFlyZone = FeatureCollection.fromJson(response.body());
        }catch (IOException | InterruptedException e){
            e.printStackTrace();
        }

        return noFlyZone;
    }



}
