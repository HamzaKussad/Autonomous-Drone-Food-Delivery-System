package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.mapbox.geojson.*;

import java.awt.geom.Line2D;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.Polygon;

/**
 * A class that represent the NoFlyZone from the WebServer
 * and extends the ServerClient
 */

public class NoFlyZone extends ServerClient {
    /**
     * creates a NoFlyZone instance
     * @param name
     * @param port
     */
    public NoFlyZone(String name, String port) {
        super(name, port);

    }
    /** this variable stores all the obstacle lines from the noFlyZone */
    private static ArrayList<Line2D> obstacleLines = new ArrayList<>();

    /**
     * This function reads the geojson file that contains the no-fly-zones as
     * a FeatureCollection.
     *
     * The function goes through all the shapes (Polygons) in the no-fly-zone
     * and gets all the corner of that object and then uses Line2D to get
     * the lines between to points (edges) and adds that to the no-fly-zone
     *
     * This way I have all the lines in which the drone cannot cross or
     * intersect with
     */

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
    }

    /**
     * This function checks if a move intersects
     * with the obstacle lines
     * @param move drone move
     * @return False if it doesnt intersect, True if it does
     */

    public static boolean intersect(Line2D move){
        for (Line2D obstacle: obstacleLines){
            if (move.intersectsLine(obstacle)){
                return true;
            }
        }
        return false;
    }


    /**
     * Method to load the data from the server from a geojson folder
     * as a Feature collection to be used by the getNoFlyZone function
     * @return A FeatureCollection of the no-fly-zone
     */
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
