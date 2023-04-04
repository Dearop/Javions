package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;

/**
 * This class decodes the geographic position from two local positions, x0 and y0 representing the even message
 * and x1 and y1 representing the odd message. It also takes an integer value of mostRecent to tell us which message
 * is the most recent.
 *
 * @author Paul Quesnot (347572)
 */

public class CprDecoder {

    private static final double zoneNumberLatitude0 = 60d;
    private static final double zoneNumberLatitude1 = 59d;

    /**
     * Decodes the geographic position based on the provided parameters.
     * The method first checks that the mostRecent value is either 0 or 1, then calculates the
     * variables A0 and A1. It then does a check to see if the even and odd zone locations are not equal and returns null.
     * Otherwise, it calculates the longitude and latitude values and converts them to t32 units. Finally,
     * it returns the geographic position as a GeoPos object.
     *
     * @param x0         double value representing the local longitude of the even message
     * @param y0         double value representing the local latitude of the even message
     * @param x1         double value representing the local longitude of the odd message
     * @param y1         double value representing the local latitude of the odd message
     * @param mostRecent integer value letting us know which message is the most recent (odd or even)
     * @return the latest geographic position as a GeoPos object or null if decoding failed
     */
    public static GeoPos decodePosition(final double x0, final double y0,
                                        final double x1, final double y1,
                                        final int mostRecent) {

        Preconditions.checkArgument((0 == mostRecent) || (1 == mostRecent));


        // Latitude
        final int zPhiLatitude = (int) Math.rint(y0 * zoneNumberLatitude1
                - y1 * zoneNumberLatitude0);

        final double phiEven = CprDecoder.currentZone(zoneNumberLatitude0, zPhiLatitude, y0);
        if (Math.PI / 2 < phiEven || -(Math.PI / 2) > phiEven) return null;

        final double phiOdd = CprDecoder.currentZone(zoneNumberLatitude1, zPhiLatitude, y1);
        if (Math.PI / 2 < phiOdd || -(Math.PI / 2) > phiOdd) return null;

        final double A0 = CprDecoder.AngleToZoneCalculator(zoneNumberLatitude0, phiEven);
        final double A1 = CprDecoder.AngleToZoneCalculator(zoneNumberLatitude0, phiOdd);

        final double evenZoneLocationLat0 = (Double.isNaN(A0)) ? 1 : Math.floor(2 * Math.PI / A0);
        final double evenZoneLocationLat1 = (Double.isNaN(A1)) ? 1 : Math.floor(2 * Math.PI / A1);

        // The two even zone locations for latitude need to have the same value, else null gets returned
        if (evenZoneLocationLat0 != evenZoneLocationLat1) return null;
        final double oddZoneLocationLat = evenZoneLocationLat0 - 1;

        //Longitude
        final int zPhiLongitude = (int) Math.rint(x0 * oddZoneLocationLat - x1 * evenZoneLocationLat0);

        final double zPhiLongitudeEven = (0 > zPhiLongitude) ? zPhiLongitude + evenZoneLocationLat0 : zPhiLongitude;
        final double zPhiLongitudeOdd = (0 > zPhiLongitude) ? zPhiLongitude + oddZoneLocationLat : zPhiLongitude;

        double lambdaEven = currentZone(evenZoneLocationLat0, zPhiLongitudeEven, x0);
        double lambdaOdd = currentZone(oddZoneLocationLat, zPhiLongitudeOdd, x1);

        if (1 == evenZoneLocationLat0) {
            lambdaEven = Units.convertFrom(CprDecoder.center(x0), Units.Angle.TURN);
            lambdaOdd = Units.convertFrom(CprDecoder.center(x1), Units.Angle.TURN);
        }

        // Getting the right Latitude or Longitude due to parity
        final double finalLongAngle = (0 == mostRecent) ? lambdaEven : lambdaOdd;
        final double finalLatAngle = (0 == mostRecent) ? phiEven : phiOdd;

        final double actualLatT32 = Units.convertTo(finalLatAngle, Units.Angle.T32);
        final double actualLongT32 = Units.convertTo(finalLongAngle, Units.Angle.T32);

        return new GeoPos((int) Math.rint(actualLongT32), (int) Math.rint(actualLatT32));
    }

    /**
     * This method calculates the angles, that we need to calculate the A0 and A1 value.
     *
     * @param numberOfZones We split earth into 60 and 59 zones, with that we can calculate a more precise position of the aircraft.
     * @param currentAngle  Is either Phi-even or Phi-odd
     * @return returns the arccosinus of formula seen in the course.
     */
    private static double AngleToZoneCalculator(final double numberOfZones, final double currentAngle) {
        return Math.acos(1 - ((1 - Math.cos(2 * Math.PI / numberOfZones))
                / Math.pow(Math.cos(currentAngle), 2)));
    }

    /**
     * This method returns the Phi or Lambda values, these will then be used in the AngleToZoneCalculator method.
     *
     * @param numberOfZones We split earth into 60 and 59 zones, with that we can calculate a more precise position of the aircraft.
     * @param currentZone   Current zone in which the aircraft is located.
     * @param position      position that is given in the parameter of the method decodePosition.
     * @return return Phi0, Phi1, Lambda0 and Lambda1 depending on the parameters.
     */
    private static double currentZone(double numberOfZones, double currentZone, double position) {
        currentZone = (0 > currentZone) ? (currentZone + numberOfZones) : currentZone;

        final double angle = (currentZone + position) / numberOfZones;
        return Units.convertFrom(CprDecoder.center(angle), Units.Angle.TURN);
    }

    private static double center(double angle){
        if (0.5 <= angle) angle -= 1;
        return angle;
    }
}