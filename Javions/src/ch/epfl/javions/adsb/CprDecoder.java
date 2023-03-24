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
        double nbreZonesDeDecoupageLatitude0 = 60d;
        double nbreZonesDeDecoupageLatitude1 = 59d;
        // Latitude
        int zPhiLatitude = (int) Math.rint(y0 * nbreZonesDeDecoupageLatitude1 - y1 * nbreZonesDeDecoupageLatitude0);
        double phiEven = currentZone(nbreZonesDeDecoupageLatitude0, zPhiLatitude, y0);
        double phiOdd = currentZone(nbreZonesDeDecoupageLatitude1, zPhiLatitude, y1);
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

        // Getting the right Latitude or Longitude due to parity
        double finalLongAngle = (mostRecent == 0) ? lambdaEven : lambdaOdd;
        double finalLatAngle = (mostRecent == 0) ? phiEven : phiOdd;

        double actualLatT32 = (int) Units.convertTo(finalLatAngle, Units.Angle.T32);
        double actualLongT32 = (int) Units.convertTo(finalLongAngle, Units.Angle.T32);
        return new GeoPos((int) Math.rint(actualLongT32), (int) Math.rint(actualLatT32));
    }

    private static double AngleToZoneCalculator(double numberOfZones, double currentAngle){
        return Math.acos(1-((1-Math.cos(2*Math.PI/numberOfZones))/Math.pow(Math.cos(currentAngle),2)));
    }

    private static double currentZone(double numberOfZones, double currentZone, double position){
        currentZone = (currentZone < 0) ? (currentZone + numberOfZones) : currentZone;
        double angle = (currentZone + position)/ numberOfZones;
        if (angle >= 0.5) angle -= 1;
        return Units.convertFrom(angle, Units.Angle.TURN);
    }
}

