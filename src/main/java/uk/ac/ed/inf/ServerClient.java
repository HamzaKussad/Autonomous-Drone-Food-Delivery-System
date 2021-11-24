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

    private static final HttpClient client = HttpClient.newHttpClient();


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
