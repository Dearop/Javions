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
    private CprDecoder(){}

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
    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent){
        Preconditions.checkArgument((mostRecent == 0) || (mostRecent == 1));
        //variable declaration, can't do it out of method because it's a record
        double nbreZonesDeDecoupageLatitude0 = 60d;
        double nbreZonesDeDecoupageLatitude1 = 59d;
        // Latitude
        int zPhiLatitude = (int) Math.rint(y0 * nbreZonesDeDecoupageLatitude1 - y1 * nbreZonesDeDecoupageLatitude0);
        double phiEven = currentZone(nbreZonesDeDecoupageLatitude0, zPhiLatitude, y0);
        if(phiEven > Math.PI/2 || phiEven < -(Math.PI/2)) return null;
        double phiOdd = currentZone(nbreZonesDeDecoupageLatitude1, zPhiLatitude, y1);
        if(phiOdd > Math.PI/2 || phiOdd < -(Math.PI/2)) return null;
        double A0 = AngleToZoneCalculator(nbreZonesDeDecoupageLatitude0, phiEven);
        double A1 = AngleToZoneCalculator(nbreZonesDeDecoupageLatitude0, phiOdd);
        double evenZoneLocationLat0 = (Double.isNaN(A0)) ? 1 :  Math.floor(2*Math.PI/A0);
        double evenZoneLocationLat1 = (Double.isNaN(A1)) ? 1 :  Math.floor(2*Math.PI/A1);
        if (evenZoneLocationLat0 != evenZoneLocationLat1) return null;
        double oddZoneLocationLat = evenZoneLocationLat0 - 1;

        //Longitude
        int zPhiLongitude =(int) Math.rint(x0*oddZoneLocationLat - x1*evenZoneLocationLat0);
        double lambdaEven = currentZone(evenZoneLocationLat0, zPhiLongitude, x0);
        double lambdaOdd = currentZone(oddZoneLocationLat, zPhiLongitude, x1);
        if(evenZoneLocationLat0 == 1) {
            lambdaEven = x0;
            lambdaOdd = x1;
        }

        if(evenZoneLocationLat0 == 1){
            lambdaEven = x0;
            lambdaOdd = x1;
        }

        // Getting the right Latitude or Longitude due to parity
        double finalLongAngle = (mostRecent == 0) ? lambdaEven : lambdaOdd;
        double finalLatAngle = (mostRecent == 0) ? phiEven : phiOdd;

        double actualLatT32 = Units.convertTo(finalLatAngle, Units.Angle.T32);
        double actualLongT32 = Units.convertTo(finalLongAngle, Units.Angle.T32);
        return new GeoPos((int) Math.rint(actualLongT32), (int) Math.rint(actualLatT32));
    }

    /**
     * This method calculates the angles, that we need to calculate the A0 and A1 value.
     *
     * @param numberOfZones We split earth into 60 and 59 zones, with that we can calculate a more precise position of the aircraft.
     * @param currentAngle  Is either Phi-even or Phi-odd
     * @return returns the arccos of formula seen in the course.
     */
    private static double AngleToZoneCalculator(double numberOfZones, double currentAngle){
        return Math.acos(1-((1-Math.cos(2*Math.PI/numberOfZones))/Math.pow(Math.cos(currentAngle),2)));
    }

    /**
     * This method returns the Phi or Lambda values, these will then be used in the AngleToZoneCalculator method.
     *
     * @param numberOfZones We split earth into 60 and 59 zones, with that we can calculate a more precise position of the aircraft.
     * @param currentZone Current zone in which the aircraft is located.
     * @param position position that is given in the parameter of the method decodePosition.
     * @return return Phi0, Phi1, Lambda0 and Lambda1 depending on the parameters.
     */
    private static double currentZone(double numberOfZones, double currentZone, double position){
        currentZone = (currentZone < 0) ? (currentZone + numberOfZones) : currentZone;
        double angle = (currentZone + position)/ numberOfZones;
        if (angle >= 0.5) angle -= 1;
        return Units.convertFrom(angle, Units.Angle.TURN);
    }
}

