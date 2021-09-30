package uk.ac.ed.inf;

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

    /**
     * gets the Pythagorean distance between the point
     * and a different location
     * @param location location of a different point coordinate
     * @return the Pythagorean distance
     */

    public double distanceTo(LongLat location) {
        return getPythagoreanDist(location);
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
     *
     * @param i
     * @return
     */

    public LongLat nextPosition(int facingAngle) throws Exception {
        LongLat nextMove;
        if(facingAngle % 10 !=0){
            throw new Exception("Angle must be multiple of 10");
        }
        if (facingAngle == Constants.HOVERING_DRONE_VALUE){
            nextMove = new LongLat(this.longitude, this.latitude);
        }else{
            double longitude = this.longitude + (Math.cos(Math.toRadians(facingAngle)) * Constants.MOVE_DIST);
            double latitude =  this.latitude + (Math.sin(Math.toRadians(facingAngle)) * Constants.MOVE_DIST);
            System.out.println(longitude);
            System.out.println(latitude);
            nextMove = new LongLat(longitude, latitude);

        }
        return nextMove;
    }

    //Helper functions

    private double getPythagoreanDist(LongLat location) {
        double latDist = this.latitude - location.latitude;
        double longDist = this.longitude - location.longitude;
        double distance = latDist*latDist + longDist*longDist;
        double pythagoreanDist = Math.sqrt(distance);
        return pythagoreanDist;
    }

}
