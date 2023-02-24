package ch.epfl.javions;


import static ch.epfl.javions.Units.*;

/**This is a public class representing geographic coordinates, i.e. a longitude/latitude pair.
 * These coordinates are expressed in t32 and stored as 32-bit integers (type int).
 *
 * @param longitudeT32
 * @param latitudeT32
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public record GeoPos(int longitudeT32, int latitudeT32) {

    /**
     * This compact constructor checks that the given arguments are in bound, if they are not then
     * this constructor will throw an exception
     *
     * @param longitudeT32 is the longitude written in 32 bits
     * @param latitudeT32 is the latitude written in 32 bits
     * @throws IllegalArgumentException if the given latitude is not valid
     */
    public GeoPos {
        if(!isValidLatitudeT32(latitudeT32)) throw new IllegalArgumentException();
    }

    /**Checks if the value passed, interpreted as a latitude expressed in t32, is valid.
     * A value is considered valid if it is between -2^30 (inclusive) and 2^30 (inclusive),
     * This corresponds to a range of -90° to +90° in degrees.
     *
     * @param latitudeT32 value of the latitude to check expressed in T32
     * @return true if the value checks out with our boundaries, false otherwise
     */
    public static boolean isValidLatitudeT32(int latitudeT32) {
        return (latitudeT32 >= -Math.pow(2, 30)) && (latitudeT32 <= Math.pow(2, 30));
    }

    /**
     * @return longitude in radians
     */
    public double longitude(){
        return convert(longitudeT32, Angle.T32, Angle.RADIAN);
    }

    /**
     * @return latitude in radians
     */
    public double latitude(){
        return convert(latitudeT32, Angle.T32, Angle.RADIAN);
    }

    /**
     * Returns a text representation of the position, with the longitude and latitude given in degrees
     * and separated by a comma.
     *
     * @return string representation of the GeoPos object, with the longitude and latitude in degrees
     */
    @Override
    public String toString() {
        return "("+convert(longitudeT32, Angle.T32, Angle.DEGREE)+"°, "+convert(latitudeT32, Angle.T32, Angle.DEGREE)+"°)";
    }
}
