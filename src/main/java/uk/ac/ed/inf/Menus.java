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

/**
 * Class that represents all Menus
 */

public class Menus {

    public String name;
    public String port;

    /** creates client to be used in class*/
    private static final HttpClient client = HttpClient.newHttpClient();
    /** Hashmap used to store all Menu items*/
    private HashMap<String, Menu> menus = new HashMap<>();

    /**
     * Creates a Menus instant
     * @param name
     * @param port
     */
    public Menus(String name, String port) {
        this.name = name;
        this.port = port;

    }

    /**
     * Function that gets the cost of each item
     * depending on the strings given in the input
     * @param strings
     * @return
     */

    public int getDeliveryCost(String... strings) {
        storeItemsInHashmap();
        int price = 0;
        for(String restaurant: strings){
            price += menus.get(restaurant).getPence();
        }
        return price + Constants.DELIVERY_COST;
    }



    //------Helper Functions

    /**
     * Helper function to fill up the Hashmap "menu"
     * from all the menus from all different restaurants
     */
    private void storeItemsInHashmap() {
        List<Restaurant> responseRestaurants = getRestaurants();
        for (Restaurant restaurant: responseRestaurants){
            for (Menu menu : restaurant.getMenu()){
                menus.put(menu.getItem(), menu);
            }
        }
    }

    /**
     * Helper function to parse json string into java object
     * after getting the http response
     * @return
     */
    private List<Restaurant> getRestaurants()  {
        List<Restaurant> responseRestaurants = new ArrayList<>() {};
        try{
            HttpResponse<String> response = doGetRequest(this.name,this.port);
            Type listType = new TypeToken<List<Restaurant>>() {} .getType();
            responseRestaurants = new Gson().fromJson(response.body(), listType);

        }catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
        return responseRestaurants;
    }

    /**
     * Helper function for http request to access the
     * json file from the webserver and store it
     * in a responce
     * @param name of website
     * @param port of website
     * @return http response
     * @throws IOException
     * @throws InterruptedException
     */

    private HttpResponse<String> doGetRequest(String name, String port) throws IOException, InterruptedException {
        String endpoint = "/menus/menus.json";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://" + name + ":" + port+ endpoint )).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

}