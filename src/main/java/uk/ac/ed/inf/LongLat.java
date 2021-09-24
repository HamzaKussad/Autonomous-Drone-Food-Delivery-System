package uk.ac.ed.inf;

public class LongLat {
    public double longitude;
    public double latitude;

    public LongLat(double longitude, double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public boolean isConfined(){
        if((this.latitude < 55.942617 || this.latitude > 55.946233) &&
           (this.longitude < -3.192473 || this.longitude > -3.184319)){
            return false;
        }
        return true;
    }

    public double distanceTo(LongLat businessSchool) {
        //DO PYTHAGORAS FUNCTION FOR BETTER READABILITY
        double latDist = this.latitude - businessSchool.latitude;
        double longDist = this.longitude - businessSchool.longitude;
        double distance = latDist*latDist + longDist*longDist;

        return Math.sqrt(distance);
    }

    public boolean closeTo(LongLat alsoAppletonTower) {
        if (distanceTo(alsoAppletonTower) < 0.00015){
            return true;
        }
        return false;
    }

    public LongLat nextPosition(int i) {
        LongLat nextMove;

        if (i == -999){
            nextMove = new LongLat(this.longitude, this.latitude);
            return nextMove;
        }

        double longitude = this.longitude + (Math.cos(Math.toRadians(i)) * 0.00015);
        double latitude =  this.latitude + (Math.sin(Math.toRadians(i)) * 0.00015);
        System.out.println(longitude);
        System.out.println(latitude);
        nextMove = new LongLat(longitude, latitude);

        return nextMove;
    }
}
