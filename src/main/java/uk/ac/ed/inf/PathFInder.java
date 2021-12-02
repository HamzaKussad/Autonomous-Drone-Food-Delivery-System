package uk.ac.ed.inf;

/**
 * Interface that is responsible for the path finding algirithms
 * The optimum path between 2 points
 */

public interface PathFInder {
    public Node getPath(Node start, Node target);
}
