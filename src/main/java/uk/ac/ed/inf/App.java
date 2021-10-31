package uk.ac.ed.inf;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, InterruptedException {

//        DatabaseClient db = new DatabaseClient();
//        db.getOrders();
//        db.fillOrderDetails();
//        System.out.println(db.orderList.keySet());
//
//        System.out.println(db.orderDetail.get("f2aa3cf5").items);
//        System.out.println( "Hello World!!!!" );
        NoFlyZone test = new NoFlyZone();
        test.noFlyZone();
    }
}
