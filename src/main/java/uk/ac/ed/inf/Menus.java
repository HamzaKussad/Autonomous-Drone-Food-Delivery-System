package uk.ac.ed.inf;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.http.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mapbox.geojson.Point;

/**
 * Class that represents all Menus
 */

public class Menus {

    public String name;
    public String port;


    /**
     * Creates a Menus instant
     * @param name
     * @param port
     */
    public Menus(String name, String port) {
        this.name = name;
        this.port = port;

    }
    ServerClient client = new ServerClient("localhost","9898");

    /**
     * Function that gets the cost of each item
     * depending on the strings given in the input
     * @param strings
     * @return
     */

    public int getDeliveryCost(String... strings) {
        HashMap<String, Menu> menus = client.storeItemsInHashmap();
        int price = 0;
        for(String restaurant: strings){
            price += menus.get(restaurant).getPence();
        }
        return price + Constants.DELIVERY_COST;
    }




}