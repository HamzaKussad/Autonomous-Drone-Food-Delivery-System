package uk.ac.ed.inf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.me.JSONArray;
import org.json.me.JSONObject;
import org.json.me.JSONException;

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
        List<Restaurant> responseRestaurants;
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:9898/menus/menus.json")).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Type listType = new TypeToken<List<Restaurant>>() {} .getType();
            responseRestaurants = new Gson().fromJson(response.body(), listType);

            for (Restaurant r: responseRestaurants){
                for (Menu m : r.menu)
                menus.put(m.item, m);
            }
            int price = 0;
            for(String restaurant: strings){

                price += menus.get(restaurant).pence;
            }
            return price +50;

        }catch (IOException  | InterruptedException e){
            e.printStackTrace();
        }

        return 0;
    }


    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONArray readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONArray json = new JSONArray(jsonText);
            return json;
        } finally {
            is.close();
        }
    }
}