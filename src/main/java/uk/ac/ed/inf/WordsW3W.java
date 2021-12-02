package uk.ac.ed.inf;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.http.HttpResponse;

/**
 * Class that represents the W3W and extends
 * from the ServerClient
 */

public class WordsW3W extends ServerClient{

    /**
     * Creates a WordsW3W instance
     * @param name
     * @param port
     */
    public WordsW3W(String name, String port) {
        super(name, port);
    }

    /**
     * This function accesses the Server to get the location
     * of a W3W in LongLat
     * @param word location W3W format input
     * @return Location in LongLat
     */


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
}
