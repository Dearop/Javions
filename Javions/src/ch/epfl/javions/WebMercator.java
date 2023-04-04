package ch.epfl.javions;

/**
 * this class allows us to project geographical coordinates according to the WebMercator projection.
 *
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public enum WebMercator {
    ;

    /**
     * Returns the coordinates of points given the zoomLevel as well as the longitude
     *
     * @param zoomLevel the zoomLevel on the map
     * @param longitude longitude given in degrees
     * @return coordinates in the cartesian coordinate system.
     */
    public static double x(int zoomLevel, double longitude) {
        return Math.scalb(1, 8 + zoomLevel)
                * (Units.convertTo(longitude
                , Units.Angle.TURN) + (0.5));
    }

    /**
     * Returns the coordinates of points given the zoomLevel as well as the latitude
     *
     * @param zoomLevel the zoomLevel on the map
     * @param latitude  latitude given in degrees
     * @return coordinates in the cartesian coordinate system.
     */
    public static double y(int zoomLevel, double latitude) {
        return Math.scalb(1, 8 + zoomLevel)
                * (-Units.convertTo(Math2.asinh(Math.tan(latitude))
                , Units.Angle.TURN) + 0.5);
    }

}
