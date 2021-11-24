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
 * Class that represents all Menus
 */

public class Menus extends ServerClient {

    /**
     * Creates a Menus instant
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

    private static HashMap<String, Menu> menus = new HashMap<>();
    private static List<Restaurant> responseRestaurants = new ArrayList<>() {};

    private static HashMap<String,Menu> getMenus(){
        return menus;
    }


    public LongLat getLongLatFrom3Words(String word) {
        W3W details = null;
        try{

            String[] wordArr = word.split("\\.");
            String endpoint = "/words/" + wordArr[0] + "/" + wordArr[1] + "/" + wordArr[2] + "/details.json";
            HttpResponse<String> response = doGetRequest(this.name,this.port,endpoint);
            details = new Gson().fromJson(response.body(),W3W.class);
        }catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
        LongLat coords = new LongLat(details.coordinates.lng,details.coordinates.lat);
        return coords;

    }

    /**
     * Helper function to fill up the Hashmap "menu"
     * from all the menus from all different restaurants
     */
    public void storeItems() {
        for (Restaurant restaurant: responseRestaurants){
            restaurant.longLatLocation = getLongLatFrom3Words(restaurant.getLocation());
            for (Menu menu : restaurant.getMenu()){
                menus.put(menu.getItem(), menu);
            }
        }
    }


    public static LongLat getLocationOfMenuItem(String item){
        for (Restaurant restaurant: responseRestaurants){
            for (Menu menu : restaurant.getMenu()){
                if (menu.getItem().equals(item)){
                    return restaurant.longLatLocation;
                }

            }
        }
        return null;
    }
    public static String getW3WOfMenuItem(String item){
        for (Restaurant restaurant: responseRestaurants){
            for (Menu menu : restaurant.getMenu()){
                if (menu.getItem().equals(item)){
                    return restaurant.getLocation();
                }

            }
        }
        return null;
    }


    /**
     * Function that gets the cost of each item
     * depending on the strings given in the input
     * @param strings
     * @return
     */

    public static int getDeliveryCost(ArrayList<String> strings) {

        int price = 0;
        for(String restaurant: strings){
            price += getMenus().get(restaurant).getPence();
        }
        return price + Constants.DELIVERY_COST;
    }



}