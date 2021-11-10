package uk.ac.ed.inf;

public class AStarNode extends LongLat implements Comparable<AStarNode>{
    public double f = Double.MIN_VALUE;
    public double g = Double.MIN_VALUE;
    public int numOfMoves = 0;
    public AStarNode parent;

    /**
     * Creates a LongLat instance
     *
     * @param longitude longitude
     * @param latitude  latitude
     */
    public AStarNode(double longitude, double latitude) {
        super(longitude, latitude);
    }

    public LongLat toLongLat(){
        return new LongLat(this.longitude,this.latitude);
    }




    @Override
    public int compareTo(AStarNode o) {
        return Double.compare(this.f, o.f);
    }

    @Override
    public AStarNode nextPosition(int angle){
        LongLat coord = super.nextPosition(angle);
        return new AStarNode(coord.longitude,coord.latitude);
    }
}
