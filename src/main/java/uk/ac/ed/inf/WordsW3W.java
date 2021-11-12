package uk.ac.ed.inf;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.http.HttpResponse;

public class WordsW3W extends ServerClient{
    public WordsW3W(String name, String port) {
        super(name, port);
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
}
