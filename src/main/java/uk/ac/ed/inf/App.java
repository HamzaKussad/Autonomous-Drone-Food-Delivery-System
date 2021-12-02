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

//        System.out.println(DatabaseIO.getOrders().size());
        AStar finder = new AStar();
        JourneyPlanner journeyPlanner = new CostPriority();


        Drone drone = new Drone(date,finder,journeyPlanner,client,orders);
        System.out.println( drone.percentageMoney());

        orders.creatingDeliveriesDatabase(drone.deliveryDataForDatabase());
        orders.creatingFlightpathDatabase(drone.flightpathsDataForDatabase());

        drone.outputGeoJsonFolder(day,month,year);


        LongLat test = new LongLat(-3.1907899184716935,55.94398750691308);
        System.out.println(test.nextPosition(320).getLongitude());
        System.out.println(test.nextPosition(320).getLatitude());



    }


}
