package uk.ac.ed.inf;


import com.mapbox.geojson.Point;

public class Move {
    public LongLat current;
    public LongLat next;
    public int facingAngle;

    public Move(LongLat current, LongLat next, int facingAngle){
        this.current = current;
        this.next = next;
        this.facingAngle = facingAngle;
    }

}
