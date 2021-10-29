package uk.ac.ed.inf;

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

public class ServerClient {

    public String name;
    public String port;

    public ServerClient(String name, String port){
        this.name = name;
        this.port = port;

    }

    private static final HttpClient client = HttpClient.newHttpClient();

    //function to read the menus.json
    //function to read the w3w json
    //function to read geojson landmarks
    //function to read geojson noflyzones
    public Point getPointFrom3Words(String word) throws IOException, InterruptedException {
        String[] wordArr = word.split(".");
        String endpoint = "/words/" + wordArr[0] + "/" + wordArr[1] + "/" + wordArr[2];
        HttpResponse<String> response = doGetRequest(this.name,this.port,endpoint);
        W3W details = new Gson().fromJson(response.body(),W3W.class);
        return Point.fromLngLat(details.coordinates.lng,details.coordinates.lat);

    }

    public FeatureCollection loadGeoJSON(String endpoint) throws IOException, InterruptedException {
        HttpResponse<String> response = doGetRequest(this.name,this.port,endpoint);
        return FeatureCollection.fromJson(response.body());
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

    /**
     * Helper function to parse json string into java object
     * after getting the http response
     * @return
     */
    private List<Restaurant> getMenus(){
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

    private HttpResponse<String> doGetRequest(String name, String port, String endpoint) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://" + name + ":" + port+ endpoint )).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }
}
