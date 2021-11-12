package uk.ac.ed.inf;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App {

    public static LineString toLineString(ArrayList<LongLat> routes){
        ArrayList<Point> points = new ArrayList<>();
        for(LongLat route: routes){

            points.add(Point.fromLngLat(route.longitude,route.latitude));
        }
        return LineString.fromLngLats(points);
    }
    public static void main( String[] args ) throws ParseException {
        String day = args[0];
        String month = args[1];
        String year = args[2];
        String webServerPort = args[3];
        String databasePort = args[4];

        System.out.println(databasePort);

        String dateString = day + "-"+month +"-" + year + "";

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        long milliseconds =  sdf.parse(dateString).getTime();



        Date date = new Date(milliseconds);
        System.out.println(date);

        ServerClient client = new ServerClient("localhost", webServerPort);
        OrdersIO orders = new OrdersIO("localhost", databasePort);
        orders.storeOrders(date);
        orders.storeOrderDetails();
        Menus menus = new Menus("localhost", webServerPort);
        menus.storeItems();
        NoFlyZone noFlyZone = new NoFlyZone("localhost", webServerPort);
        noFlyZone.getNoFlyZone();


        AStar finder = new AStar();
        //AStarPathFinder finder = new AStarPathFinder();
        JourneyPlanner opt = new JourneyPlanner();


        Drone drone = new Drone(date,finder,opt,client,orders);
        System.out.println(drone.getPlan().toJson());

//        AStarNode beirut = new AStarNode(-3.186199,55.945734);
//        AStarNode soderberg = new AStarNode(-3.191594,55.943658);
//        AStarNode laby = new AStarNode(-3.1895,55.9438);
//        AStarNode rudis = new AStarNode(-3.191065,55.945626);
//        AStarNode pickup1 = new AStarNode(	-3.1885,55.9440);
//        AStarNode pickup2 = new AStarNode(	-3.1887,	55.9459);
//        AStarNode pickup3 = new AStarNode(-3.1882, 	55.9436);
//
//        System.out.println(toLineString(finder.nodeToList(finder.getPath(rudis,pickup1))).toJson());
//        DatabaseClient db = new DatabaseClient();
//        db.getOrders();
//        db.fillOrderDetails();
//        System.out.println();


//        System.out.println( "Hello World!!!!" );

//
//        LongLat beirut = new LongLat(-3.186199,55.945734);
//        LongLat soderberg = new LongLat(-3.191594,55.943658);
//        LongLat rudis = new LongLat(-3.191065,55.945626);
//        LongLat test1 = new LongLat(-3.1832,55.9461);

//        LongLat ting = new LongLat(-3.1900,55.9457);
//        LongLat pressCoffee = new LongLat(-3.1846940,55.9429 );
//
////        ArrayList<LongLat> testre = new ArrayList<>();
////        testre.add(beirut);
////        testre.add(soderberg);
////
////        DroneController heh = new DroneController();
////        System.out.println( heh.getRoute(testre).toJson());
//
//        AStarPathFinder test = new AStarPathFinder();
//        var routes = test.findRoute(rudis,pressCoffee);
//        System.out.println(toLineString(routes).toJson());



    }


}
