package uk.ac.ed.inf;

public class LongLat {
    public double longitude;
    public double latitude;

    public LongLat(double longitude, double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public boolean isConfined(){
        if((this.latitude >= Constants.LAT_MIN && this.latitude <= Constants.LAT_MAX) &&
           (this.longitude >= Constants.LONG_MIN && this.longitude <= Constants.LONG_MAX)){
            return true;
        }
        return false;
    }

    public double distanceTo(LongLat location) {
        return getPythagoreanDist(location);
    }

    public boolean closeTo(LongLat location) {
        if (distanceTo(location) < Constants.MOVE_DIST){
            return true;
        }
        return false;
    }

    public LongLat nextPosition(int i) {
        LongLat nextMove;
        if (i == -999){
            nextMove = new LongLat(this.longitude, this.latitude);
        }else{
            double longitude = this.longitude + (Math.cos(Math.toRadians(i)) * Constants.MOVE_DIST);
            double latitude =  this.latitude + (Math.sin(Math.toRadians(i)) * Constants.MOVE_DIST);
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
