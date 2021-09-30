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

public class Menus {

    public String name;
    public String port;

    private static final HttpClient client = HttpClient.newHttpClient();
    private HashMap<String, Menu> menus = new HashMap<>();

    public Menus(String name, String port) {
        this.name = name;
        this.port = port;
    }

    public int getDeliveryCost(String... strings) {
        List<Restaurant> responseRestaurants = getRestaurants();
        for (Restaurant restaurant: responseRestaurants){
            for (Menu menu : restaurant.getMenu()){
                menus.put(menu.getItem(), menu);
            }
        }
        int price = 0;
        for(String restaurant: strings){
            price += menus.get(restaurant).getPence();
        }
        return price + Constants.DELIVERY_COST;
    }

    //------Helper Functions

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

    private HttpResponse<String> doGetRequest(String name, String port) throws IOException, InterruptedException {
        String endpoint = "/menus/menus.json";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://" + name + ":" + port+ endpoint )).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

}