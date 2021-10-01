package uk.ac.ed.inf;

import java.util.List;

/**
 * Class that represents a Restaurant Object
 * Class has been encapsulated
 */

public class Restaurant {
    private String name;
    private String location;
    private List<Menu> menu;

    public String getName(){
        return this.name;
    }
    public String getLocation(){
        return this.location;
    }
    public List<Menu> getMenu(){
        return this.menu;
    }
}
