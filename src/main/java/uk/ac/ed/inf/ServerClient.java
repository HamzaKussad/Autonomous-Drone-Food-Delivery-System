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

/**
 * A serverClient class that is responsible for getting information
 * from the Webserver
 */

public class ServerClient {

    public String name;
    public String port;


    /**
     * Creates a ServerClient instance with
     * a name and port that will be used for all the
     * classes that extend from this class
     */

    public ServerClient(String name, String port){
        this.name = name;
        this.port = port;

    }

    /** Static instance of the HttpClient as it's a heavy object */

    private static final HttpClient client = HttpClient.newHttpClient();


    /**
     * Performs a Http request to get data from the server
     * depending on the endpoint given to the function
     * @param name of the Webserver
     * @param port of the Webserver
     * @param endpoint the endpoint for the required data
     * @return a response that will contains the data from the request
     * @throws IOException
     * @throws InterruptedException
     */

    public static HttpResponse<String> doGetRequest(String name, String port, String endpoint) throws IOException, InterruptedException {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://" + name + ":" + port+ endpoint )).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }


}
