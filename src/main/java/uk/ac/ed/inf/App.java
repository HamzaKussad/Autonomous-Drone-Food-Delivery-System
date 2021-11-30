package uk.ac.ed.inf;

import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.Date;

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

    public static String webServerPort;
    public static String databasePort;
    public static void main( String[] args ) throws ParseException {
        String day = args[0];
        String month = args[1];
        String year = args[2];
        webServerPort = args[3];
        databasePort = args[4];


        String dateString = day + "-"+month +"-" + year + "";

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        long milliseconds =  sdf.parse(dateString).getTime();

        Date date = new Date(milliseconds);
        System.out.println(date);

        ServerClient client = new ServerClient("localhost", webServerPort);
        DatabaseIO orders = new DatabaseIO("localhost", databasePort);
        orders.storeOrders(date);
        orders.storeOrderDetails();
        Menus menus = new Menus("localhost", webServerPort);
        menus.storeItems();
        NoFlyZone noFlyZone = new NoFlyZone("localhost", "9898");
        noFlyZone.getNoFlyZone();

        AStar finder = new AStar();
        JourneyPlanner journeyPlanner = new CostPriority();


        Drone drone = new Drone(date,finder,journeyPlanner,client,orders);
        System.out.println( drone.percentageMoney());

        orders.creatingDeliveriesDatabase(drone.deliveryDataForDatabase());
        orders.creatingFlightpathDatabase(drone.flightpathsDataForDatabase());

        drone.outputGeoJsonFolder(day,month,year);


        LongLat test = new LongLat(-3.1907899184716935,55.94398750691308);
        System.out.println(test.nextPosition(320).longitude);
        System.out.println(test.nextPosition(320).latitude);



//        AStarNode beirut = new AStarNode(-3.186199,55.945734);
//        AStarNode soderberg = new AStarNode(-3.191594,55.943658);
//        AStarNode laby = new AStarNode(-3.1895,55.9438);
//        AStarNode rudis = new AStarNode(-3.191065,55.945626);
//        AStarNode greggs = new AStarNode(-3.1913,55.9456);
//        AStarNode bingTea = new AStarNode(	-3.1853,	55.9447);
//        AStarNode nile = new AStarNode(-3.1861,	55.9447);
//        AStarNode basket = new AStarNode(-3.1852,	55.9447);
//        AStarNode pickup1 = new AStarNode(	-3.1885,55.9440);
//        AStarNode pickup2 = new AStarNode(	-3.1887,	55.9459);
//        AStarNode pickup3 = new AStarNode(-3.1882, 	55.9436);
//        AStarNode pickup4 = new AStarNode(-3.1878,55.9435);
//        AStarNode pickup5 = new AStarNode(-3.1884,55.9454);
//        AStarNode pickup6 = new AStarNode(	-3.1893,55.9434 );
//        AStarNode appleton = new AStarNode(	-3.1869,	55.9445);



    }


}
