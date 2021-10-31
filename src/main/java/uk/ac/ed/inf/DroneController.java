package uk.ac.ed.inf;

import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

import java.util.ArrayList;
import java.util.List;

public class DroneController {

    LongLat current;
    LongLat next;

    public LineString getPath(List<LongLat> locations){
        ArrayList<Move> moves = new ArrayList<>();
        int movesLeft = 1500;

        current = locations.get(0);
        LongLat target = locations.get(1);


        int locationIndex = 1;
        while(locationIndex < locations.size()){
            while(current != target){
                int moveAngle = current.getAngle(target);
                next = current.nextPosition(moveAngle);

                Move move = new Move(current,next,moveAngle);
                moves.add(move);

                current = next;
            }


        }
        return null;

    }


}
