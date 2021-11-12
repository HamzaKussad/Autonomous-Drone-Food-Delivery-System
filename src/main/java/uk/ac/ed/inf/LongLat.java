package uk.ac.ed.inf;

import java.awt.geom.Line2D;

/**
 * Class that represent a point on the map
 * using Latitude and Longitude
 */
public class LongLat {
    public double longitude;
    public double latitude;


    /**
     * Creates a LongLat instance
     * @param longitude longitude
     * @param latitude latitude
     */
    public LongLat(double longitude, double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * Checks if the point coordinates are within the confined area
     * around main campus
     * @return True if the point is in the confined area
     */

    public boolean isConfined(){
        if((this.latitude >= Constants.LAT_MIN && this.latitude <= Constants.LAT_MAX) &&
           (this.longitude >= Constants.LONG_MIN && this.longitude <= Constants.LONG_MAX)){
            return true;
        }
        return false;
    }

    public boolean isValidMove(LongLat nextMove){

        var path = new Line2D.Double(this.longitude,this.latitude,nextMove.longitude,nextMove.latitude);
        for (Line2D obstacle: NoFlyZone.obstacleLines){
            if (path.intersectsLine(obstacle)){
                return false;
            }
        }
        return true;

    }

    /**
     * gets the Pythagorean distance between the point
     * and a different location
     * @param location location of a different point coordinate
     * @return the Pythagorean distance
     */

    public double distanceTo(LongLat location) {
        return getPythagoreanDist(location);
    }

    public double manhattanDist(LongLat location){
        return 4*Math.abs(this.latitude - location.latitude) + Math.abs(this.longitude - location.longitude);
    }
    public double chebyshevDist(LongLat location){
        return Math.min(Math.abs(this.latitude-location.latitude),Math.abs(this.longitude-location.longitude));
    }

    /**
     * Checks if a location is (close to) within a move from the
     * current location(point)
     * @param location location of a different point coordinate
     * @return True if the input location is close to the current point
     */

    public boolean closeTo(LongLat location) {
        if (distanceTo(location) < Constants.MOVE_DIST){
            return true;
        }
        return false;
    }

    /**
     * The function first checks if the drone is hovering,
     * if it is, the function will return the same position.
     * The function then makes sure the angle is a multiple
     * of 10, if it is not, it will throw an exception.
     * The function then calculates the longitude and latitude using
     * trigonometry rules
     * new longitude = (previous longitude + cos(angle)) * Move
     * new latitude = (previous latitude + sin(angle)) * Move
     * The function returns the next position after a "move"
     * depending on the angle the drone is facing
     *
     * @param facingAngle the angle that the drone is currently facing
     * @return next position after a "move"
     */

    public LongLat nextPosition(int facingAngle){
        LongLat nextMove = null;
        if (facingAngle == Constants.HOVERING_DRONE_VALUE){
            nextMove = new LongLat(this.longitude, this.latitude);
            return nextMove;

        }else if(facingAngle % 10 !=0){
            try {
                throw new Exception("Angle must be multiple of 10");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else{
            double longitude = this.longitude + (Math.cos(Math.toRadians(facingAngle)) * Constants.MOVE_DIST);
            double latitude =  this.latitude + (Math.sin(Math.toRadians(facingAngle)) * Constants.MOVE_DIST);
            nextMove = new LongLat(longitude, latitude);
        }
        return nextMove;
    }

    public int getAngle(LongLat targetLocation){
        double angleToTaregt =
                Math.toDegrees(Math.atan2(targetLocation.latitude-this.latitude,
                        targetLocation.longitude-this.longitude));
        int angleToMove = (10 * (int) Math.round(angleToTaregt / 10) + 360) % 360;

        return angleToMove;
    }

    public AStarNode toNode(){
        return new AStarNode(this.longitude,this.latitude);
    }


    //Helper functions

    /**
     * A helper function to calculate the Pythagorean distance
     * @param location distance to the location
     * @return Pythagorean Distance
     */

    private double getPythagoreanDist(LongLat location) {
        double latDist = this.latitude - location.latitude;
        double longDist = this.longitude - location.longitude;
        double distance = latDist*latDist + longDist*longDist;
        double pythagoreanDist = Math.sqrt(distance);
        return pythagoreanDist;
    }
}
