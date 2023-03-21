package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;

public class CprDecoder {
    private CprDecoder(){}

    /**
     *
     * @param x0 double value representing the local longitude of the even message
     * @param y0 double value representing the local latitude of the even message
     * @param x1 double value representing the local longitude of the odd message
     * @param y1 double value representing the local latitude of the odd message
     * @param mostRecent integer value letting us know which message is the most recent (odd or even)
     * @return the latest geographic position
     */
    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent){
        Preconditions.checkArgument((mostRecent == 0) || (mostRecent == 1));
        //variable declaration, can't do it out of method because it's a record
        double evenZoneLocationLat1;
        double oddZoneLocationLat1;
        double nbreZonesDeDecoupageLatitude0 = 60d;
        double nbreZonesDeDecoupageLatitude1 = 59d;
        // Latitude
        int zPhiLatitude = (int) Math.rint(y0 * nbreZonesDeDecoupageLatitude1 - y1 * nbreZonesDeDecoupageLatitude0);
        double phiEven = currentZone(nbreZonesDeDecoupageLatitude0, zPhiLatitude, y0);
        double phiOdd = currentZone(nbreZonesDeDecoupageLatitude1, zPhiLatitude, y1);
        double A0 = AngleToZoneCalculator(nbreZonesDeDecoupageLatitude0, phiEven);
        double A1 = AngleToZoneCalculator(nbreZonesDeDecoupageLatitude1, phiOdd);
        double evenZoneLocationLat0 = (Double.isNaN(A0)) ? 1 :  Math.floor(2*Math.PI/A0);
        double oddZoneLocationLat0 = (Double.isNaN(A0)) ? 1 :  Math.floor(2*Math.PI/A1);
        evenZoneLocationLat1 = evenZoneLocationLat0 -1;
        oddZoneLocationLat1 = oddZoneLocationLat0-1;
        if((evenZoneLocationLat0 != oddZoneLocationLat0) || (evenZoneLocationLat1 != oddZoneLocationLat1)) return null;

        //Longitude
        int zPhiLongitude =(int) Math.rint(x0*oddZoneLocationLat1 - x1*oddZoneLocationLat0);
        double lambdaEven = currentZone(oddZoneLocationLat0, zPhiLongitude, x0);
        double lambdaOdd = currentZone(oddZoneLocationLat1, zPhiLongitude, x1);

        // Getting the right Latitude or Longitude due to parity
        double evenZoneLocationLat = (mostRecent == 0) ? phiEven : phiOdd;
        double finalLongAngle = (mostRecent == 0) ? lambdaEven: lambdaOdd;
        int actualLatT32 = (int) Units.convert(evenZoneLocationLat, Units.Angle.DEGREE, Units.Angle.T32);
        int actualLongT32 = (int) Units.convert(finalLongAngle, Units.Angle.DEGREE, Units.Angle.T32);
        return new GeoPos( actualLongT32, actualLatT32);
    }

    private static double AngleToZoneCalculator(double numberOfZones, double currentAngle){
        return Math.acos(1-((1-Math.cos(2*Math.PI/numberOfZones))/Math.pow(Math.cos(currentAngle),2)));
    }

    private static double currentZone(double numberOfZones, double currentZone, double position){
        return Units.convert((currentZone + position)/
               numberOfZones, Units.Angle.TURN, Units.Angle.DEGREE);
    }
}

