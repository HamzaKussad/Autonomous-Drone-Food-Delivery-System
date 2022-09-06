package uk.ac.ed.inf;

import java.io.IOException;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.http.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class that represents all Menus and extends
 * from the ServerClient
 */

public class Menus extends ServerClient {

    /**
     * Creates a Menus instant and uses the doGetRequest function from
     * the serverClient to fill up the responseRestaurants from the data
     * in the server
     * @param name
     * @param port
     */
    public Menus(String name, String port) {
        super(name,port);
        String endpoint = "/menus/menus.json";
        try{
            HttpResponse<String> response = doGetRequest(this.name,this.port,endpoint);
            Type listType = new TypeToken<List<Restaurant>>() {} .getType();
            responseRestaurants = new Gson().fromJson(response.body(), listType);

        }catch (IOException | InterruptedException e){
            e.printStackTrace();
        }

    }

    /**
     * A WordsW3W instance to be able to get the LongLat of a W3W location
     */

    WordsW3W wordsW3W = new WordsW3W(name,port);

    /**
     *  this stores the list restaurants as obtained from the webserver using
     *  a Restaurant Object
     */
    private static List<Restaurant> responseRestaurants = new ArrayList<>() {};

    /**
     * this instance stores all the items in all the menus by name as key, and
     * the Menu Object as value
     */
    private static HashMap<String, Menu> menuItems = new HashMap<>();


    /**
     * A function to fill up the Hashmap "menuItems"
     * from all the menus from all different restaurants
     */
    public void storeItems() {
        for (Restaurant restaurant: responseRestaurants){
            restaurant.setLongLatLocation(wordsW3W.getLongLatFrom3Words(restaurant.getW3WLocation()));
            for (Menu menu : restaurant.getMenu()){
                menuItems.put(menu.getItem(), menu);
            }
        }
    }

    /**
     * A static function that can be called to obtain the LongLat location
     * of a specific item in the menu
     * @param item item name
     * @return location of the restaurant that serves this item in LongLat
     */

    public static LongLat getLocationOfMenuItem(String item){
        for (Restaurant restaurant: responseRestaurants){
            for (Menu menu : restaurant.getMenu()){
                if (menu.getItem().equals(item)){
                    return restaurant.getLongLatLocation();
                }

            }
        }
        return null;
    }

    /**
     * A static function that can be called to obtain the W3W location
     * of a specific item in the menu
     * @param item item name
     * @return location of the restaurant that serves this item in W3w
     */

    public static String getW3WOfMenuItem(String item){
        for (Restaurant restaurant: responseRestaurants){
            for (Menu menu : restaurant.getMenu()){
                if (menu.getItem().equals(item)){
                    return restaurant.getW3WLocation();
                }

            }
        }
        return null;
    }

    /**
     * Function that gets the cost of each item
     * depending on the strings given in the input
     * @param strings List of strings
     * @return the price of an order
     */

    public static int getDeliveryCost(ArrayList<String> strings) {

        int price = 0;
        for(String restaurant: strings){
            price += menuItems.get(restaurant).getPence();
        }
        return price + Constants.DELIVERY_COST;
    }



}