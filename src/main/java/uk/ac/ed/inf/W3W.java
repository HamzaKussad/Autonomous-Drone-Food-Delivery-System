package uk.ac.ed.inf;

/**
 * Class that represents the W3W Object
 * The class has been encapsulated
 */

public class W3W {
    private Coordinates coordinates;

    public Coordinates getCoordinates() {
        return this.coordinates;
    }

    class Coordinates{
        private double lng;
        private double lat;

        public double getLat() {
            return this.lat;
        }

        public double getLng() {
            return this.lng;
        }
    }
}
