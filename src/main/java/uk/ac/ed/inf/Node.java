package uk.ac.ed.inf;

public class Node extends LongLat implements Comparable<Node>{
    public double f = Double.MAX_VALUE;
    public double g = Double.MAX_VALUE;
    public int angle;
    public Node parent;

    /**
     * Creates a LongLat instance
     *
     * @param longitude longitude
     * @param latitude  latitude
     */
    public Node(double longitude, double latitude) {
        super(longitude, latitude);
    }

    public LongLat toLongLat(){
        return new LongLat(this.longitude,this.latitude);
    }

    public int getAngle() {
        return angle;
    }

    @Override
    public int compareTo(Node o) {
        return Double.compare(this.f, o.f);
    }

    @Override
    public Node nextPosition(int angle){
        LongLat coord = super.nextPosition(angle);
        return new Node(coord.longitude,coord.latitude);
    }
}
