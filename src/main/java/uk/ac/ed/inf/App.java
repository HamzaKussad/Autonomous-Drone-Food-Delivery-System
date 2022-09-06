package uk.ac.ed.inf;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

        long milliseconds = dateFormatter(day, month, year);

        Date date = new Date(milliseconds);

        DatabaseIO orders = new DatabaseIO("localhost", databasePort);
        Menus menus = new Menus("localhost", webServerPort);
        NoFlyZone noFlyZone = new NoFlyZone("localhost", webServerPort);

        orders.storeOrders(date);
        orders.storeOrderDetails();

        menus.storeItems();

        noFlyZone.getNoFlyZone();

        PathFinder pathFinder = new AStar();
        JourneyPlanner journeyPlanner = new CostPriority();
        OrderPlannerAlgorithm orderPlanner = new OrderPlanner();

        Drone drone = new Drone(pathFinder, journeyPlanner, orderPlanner);

        System.out.println( drone.percentageMoney());

        orders.creatingDeliveriesDatabase(drone.deliveryDataForDatabase());
        orders.creatingFlightpathDatabase(drone.flightpathsDataForDatabase());

        drone.outputGeoJsonFolder(day,month,year);


        LongLat test = new LongLat(-3.1865739999999994,55.94475380762113);
        System.out.println(test.nextPosition(240).getLongitude());
        System.out.println(test.nextPosition(240).getLatitude());



    }

    private static long dateFormatter(String day, String month, String year) throws ParseException {
        String dateString = day + "-"+ month +"-" + year + "";

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        long milliseconds =  sdf.parse(dateString).getTime();
        return milliseconds;
    }


}
