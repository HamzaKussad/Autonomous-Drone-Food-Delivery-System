package uk.ac.ed.inf;

import com.mapbox.geojson.*;

import java.io.IOException;
import com.mapbox.geojson.Polygon;


public class NoFlyZone {
    ServerClient client = new ServerClient("localhost","9898");

    public boolean noFlyZone() throws IOException, InterruptedException {
        FeatureCollection redZone =  client.loadGeoJSON();
        var test = (Polygon) redZone.features().get(1).geometry();


        System.out.println(test.coordinates().get(0));

        return true;
    }
}
