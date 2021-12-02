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
    public LongLat LongLatLocation;

    public String getName(){
        return this.name;
    }
    public String getW3WLocation(){return this.location;}
    public LongLat getLongLatLocation() {return LongLatLocation;}
    public List<Menu> getMenu(){
        return this.menu;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String W3Wlocation) {
        this.location = W3Wlocation;
    }

    public void setLongLatLocation(LongLat LongLatLocation) {
        this.LongLatLocation = LongLatLocation;
    }

    public void setMenu(List<Menu> menu) {
        this.menu = menu;
    }
}
