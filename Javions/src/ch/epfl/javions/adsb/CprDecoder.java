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
    // TODO: 3/18/2023  this whole function is pretty long because we have loads of steps. It seems pretty clumsy
    //     for now so might be a good idea to make it a bit tidier and optimise a little.
    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent){
        Preconditions.checkArgument((mostRecent == 0) || (mostRecent == 1));
        //variable declaration, can't do it out of method because it's a record
        double evenZoneLocationLat0 = 1;
        double evenZoneLocationLat1;
        double oddZoneLocationLat0 = 1;
        double oddZoneLocationLat1;
        double nbreZonesDeDecoupageLatitude0 = 60;
        double nbreZonesDeDecoupageLatitude1 = 59;
        // Latitude
        double zPhiLatitude = Math.rint(y0*59 - y1*60);
        double phi0 = Units.convert((zPhiLatitude + y0)/60, Units.Angle.TURN, Units.Angle.DEGREE);
        double phi1 = Units.convert((zPhiLatitude + y1)/59, Units.Angle.TURN, Units.Angle.DEGREE);
        // we get the right number for A0
        double A0 = Math.acos(1-((1-Math.cos(2*Math.PI/60))/Math.pow(Math.cos(phi0),2)));
        double A1 = Math.acos(1-((1-Math.cos(2*Math.PI/59))/Math.pow(Math.cos(phi1),2)));
        if(!Double.isNaN(A0))
            evenZoneLocationLat0 = Math.floor(2*Math.PI/A0);
        evenZoneLocationLat1 = evenZoneLocationLat0 -1;
        if(!Double.isNaN(A1))
            oddZoneLocationLat0 = Math.floor(2*Math.PI/A1);
        oddZoneLocationLat1 = oddZoneLocationLat0-1;
        if((evenZoneLocationLat0 != oddZoneLocationLat0) || (evenZoneLocationLat1 != oddZoneLocationLat1)) return null;
        //Longitude
        double zPhiLongitude = Math.rint(x0*oddZoneLocationLat1 - x1*oddZoneLocationLat0);
        phi0 = Units.convert((zPhiLongitude + x0)/oddZoneLocationLat0, Units.Angle.TURN, Units.Angle.DEGREE);
        phi1 = Units.convert((zPhiLongitude + x1)/oddZoneLocationLat1, Units.Angle.TURN, Units.Angle.DEGREE);

        int actualLatT32;
        int actualLongT32;
        double evenZoneLocationLat = (mostRecent == 0) ? evenZoneLocationLat0 - 0.5 : evenZoneLocationLat1 - 0.5;
        double finalLongAngle = (mostRecent == 0) ? phi0 - Math.PI/2 : phi1 - Math.PI/2;
        actualLatT32 = (int) Units.convert(evenZoneLocationLat, Units.Angle.TURN, Units.Angle.T32);
        actualLongT32 = (int) Units.convert(finalLongAngle, Units.Angle.DEGREE, Units.Angle.T32);
        return new GeoPos( actualLongT32, actualLatT32);
    }
}

