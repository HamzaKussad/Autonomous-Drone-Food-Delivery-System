package uk.ac.ed.inf;

import java.util.ArrayList;

public class Path implements Comparable<Path> {
    private ArrayList<LongLat> moves;
    LongLat target;
    LongLat next;

    public Path(LongLat target, ArrayList<LongLat> moves){
        this.target = target;
        this.moves = moves;
        this.next = moves.get(moves.size() -1); //Most recent addition to the list

    }

    @Override
    public int compareTo(Path otherPath){
        var current =this.getRouteCost();
        var other = otherPath.getRouteCost();
        return Double.compare(current, other);
    }

    public ArrayList<LongLat> getMoves(){
        return this.moves;
    }


    public double getRouteCost(){
        double cost = 0;
        if(moves.size()>0){
            double realDist = moves.size() * Constants.MOVE_DIST;
            double manhattanDist = getDistToTarget();
            cost = realDist + manhattanDist ;
        }
        return cost;
    }

    public double getDistToTarget(){
        return this.next.manhattanDist(this.target) + this.next.distanceTo(this.target);
    }

}
