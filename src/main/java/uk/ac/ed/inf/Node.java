package uk.ac.ed.inf;

/**
 * A Node class to be used in my Astar algorithm
 * It extends from the LongLat class as many of the
 * functionalities provided there will be helpful when
 * running the algorithm
 * (or future algorithms too, such as greedy)
 */

// inspired by https://stackabuse.com/graphs-in-java-a-star-algorithm/

public class Node extends LongLat implements Comparable<Node>{
    /** f is the cost function */
    public double f = Double.MAX_VALUE;
    /** g is the cost function of the path from the start to node n */
    public double g = Double.MAX_VALUE;
    /** this stores the current angle the drone is facing */
    public int angle;
    /** This is a node parent to store the data of the previous nodes */
    public Node parent;

    /**
     * Creates a Node instance using the longitude and latitude
     * of the LongLat class
     *
     * @param longitude longitude
     * @param latitude  latitude
     */
    public Node(double longitude, double latitude) {
        super(longitude, latitude);
    }

    /**
     * function to get the LongLat from the node
     * @return LongLat
     */
    public LongLat toLongLat(){
        return new LongLat(this.longitude,this.latitude);
    }

    /**
     * A comparator to be used in the priority queue
     * @param node to compare against
     * @return an Int32 value that determines if node is
     * greater than, less than or equal to the current node
     */

    @Override
    public int compareTo(Node node) {
        return Double.compare(this.f, node.f);
    }

    /**
     * Overrides the main function to
     * return a Node with the next position
     * @param facingAngle the angle that the drone is currently facing
     * @return next position after a "move" as a Node
     */
    @Override
    public Node nextPosition(int facingAngle){
        LongLat coord = super.nextPosition(facingAngle);
        return new Node(coord.longitude,coord.latitude);
    }
}
