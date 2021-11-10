package uk.ac.ed.inf;

import java.beans.FeatureDescriptor;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import org.mortbay.util.IO;

public class ServerClient {

    public String name;
    public String port;

    public ServerClient(String name, String port){
        this.name = name;
        this.port = port;

    }

    public static final HttpClient client = HttpClient.newHttpClient();

    //function to read the menus.json
    //function to read the w3w json
    //function to read geojson landmarks
    //function to read geojson noflyzones
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

    public FeatureCollection loadNoFlyZone(){
        FeatureCollection noFlyZone = null;
        try{
            String endpoint = "/buildings/no-fly-zones.geojson";
            HttpResponse<String> response = doGetRequest(this.name,this.port,endpoint);
            noFlyZone = FeatureCollection.fromJson(response.body());
        }catch (IOException | InterruptedException e){
            e.printStackTrace();
        }

        return noFlyZone;
    }


    /**
     * Helper function to fill up the Hashmap "menu"
     * from all the menus from all different restaurants
     */
    public HashMap storeItemsInHashmap() {
        HashMap<String, Menu> menus = new HashMap<>();
        List<Restaurant> responseRestaurants = getMenus();
        for (Restaurant restaurant: responseRestaurants){
            for (Menu menu : restaurant.getMenu()){
                menus.put(menu.getItem(), menu);
            }
        }
        return menus;
    }

    public LongLat getLocationOfMenuItem(String item){
        List<Restaurant> responseRestaurants = getMenus();
        for (Restaurant restaurant: responseRestaurants){
            for (Menu menu : restaurant.getMenu()){
                if (menu.getItem().equals(item)){
                    return getLongLatFrom3Words(restaurant.getLocation());
                }

            }
        }
        return null;
    }

    /**
     * Helper function to parse json string into java object
     * after getting the http response
     * @return
     */
    public List<Restaurant> getMenus(){
        List<Restaurant> responseRestaurants = new ArrayList<>() {};
        String endpoint = "/menus/menus.json";
        try{
            HttpResponse<String> response = doGetRequest(this.name,this.port,endpoint);
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

    public static HttpResponse<String> doGetRequest(String name, String port, String endpoint) throws IOException, InterruptedException {
        HttpResponse<String> response = null;
        try{
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://" + name + ":" + port+ endpoint )).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }catch (IOException | InterruptedException e){
            e.printStackTrace();
        }

        return response;
    }
}
