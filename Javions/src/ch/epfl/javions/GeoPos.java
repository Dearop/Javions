package ch.epfl.javions;


import static ch.epfl.javions.Units.*;

/**
 * This record represents GeoPos
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
    public GeoPos { // constructeur compact
        if(!isValidLatitudeT32(latitudeT32)) throw new IllegalArgumentException();

    }

    /**
     * Checks if a given latitude is possible :
     * by checking the lower bound, if the latitude is lower than the lower bound, it is not valid
     * by checking the upper bound, if the latitude is higher than the lower bound, it is not valid
     * if the two are false than the latitude must be valid
     *
     * @param latitudeT32
     * @return
     */
    public static boolean isValidLatitudeT32(int latitudeT32) {
        return !(latitudeT32 < -Math.pow(2, 30)) && !(latitudeT32 > Math.pow(2, 30));
    }

    /**
     * This function lets you convert the longitude from the 32 bits information into radians
     * @return double Radians
     */
    public double longitude(){
        return convert(longitudeT32, Angle.T32, Angle.RADIAN);
    }

    /**
     * This function lets you convert the latitude from the 32 bits information into radians
     * @return double Radians
     */
    public double latitude(){
        return convert(latitudeT32, Angle.T32, Angle.RADIAN);
    }

    /**
     * this function gives back a String that shows the given coordinates of an object in degrees
     * @return String in the form of (82.784228855744°, 10.34802851267159°) where the numbers are translated from
     * an Object.
     */
    @Override
    public String toString() {
        return "("+convert(longitudeT32, Angle.T32, Angle.DEGREE)+"°, "+convert(latitudeT32, Angle.T32, Angle.DEGREE)+"°)";
    }
}
