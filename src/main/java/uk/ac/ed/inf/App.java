package uk.ac.ed.inf;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{

    public static LineString toLineString(ArrayList<LongLat> routes){
        ArrayList<Point> points = new ArrayList<>();
        for(LongLat route: routes){

            points.add(Point.fromLngLat(route.longitude,route.latitude));
        }
        return LineString.fromLngLats(points);
    }
    public static void main( String[] args ) {

//        DatabaseClient db = new DatabaseClient();
//        db.getOrders();
//        db.fillOrderDetails();
//        System.out.println();


        System.out.println( "Hello World!!!!" );
        NoFlyZone noFlyZone = new NoFlyZone();
        noFlyZone.noFlyZone();

        LongLat beirut = new LongLat(-3.186199,55.945734);
        LongLat soderberg = new LongLat(-3.191594,55.943658);
        LongLat rudis = new LongLat(-3.191065,55.945626);
        LongLat test1 = new LongLat(-3.1832,55.9461);
        LongLat laby = new LongLat(-3.1895,55.9438);
        LongLat ting = new LongLat(-3.1900,55.9457);
        LongLat pressCoffee = new LongLat(-3.1846940,55.9429 );

//        ArrayList<LongLat> testre = new ArrayList<>();
//        testre.add(beirut);
//        testre.add(soderberg);
//
//        DroneController heh = new DroneController();
//        System.out.println( heh.getRoute(testre).toJson());

        AStarPathFinder test = new AStarPathFinder();
        var routes = test.findRoute(rudis,pressCoffee);
        System.out.println(toLineString(routes).toJson());



    }


}
