package uk.ac.ed.inf;

/**
 * Class used to save all the constants used
 * in the program
 */
public class Constants {
    /** The upper latitude boundary */
    public static final double LAT_MAX = 55.946233;
    /** The lower latitude boundary */
    public static final double LAT_MIN = 55.942617;
    /** The upper longitude boundary*/
    public static final double LONG_MAX = -3.184319;
    /** The lower longitude boundary */
    public static final double LONG_MIN = -3.192473;
    /** The distance per move in angles*/
    public static final double MOVE_DIST = 0.00015;
    /** The delivery cost per order */
    public static final int DELIVERY_COST = 50;
    /** The constant used to indicate that the drone is "hovering"*/
    public static final int HOVERING_DRONE_VALUE = -999;
}
