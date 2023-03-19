package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;

public class CprDecoder {
    //non-instanciable!
    private CprDecoder(){

    }

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
    //     for now so might be a good idea to make it a bit tidier.
    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent){
        Preconditions.checkArgument((mostRecent == 0) || (mostRecent == 1));
        //variable declaration, can't do it out of method because it's a record
        double evenZoneLocationLat0;
        double evenZoneLocationLat1;
        double oddZoneLocationLat0;
        double oddZoneLocationLat1;
        // Latitude
        double zPhiLatitude = Math.rint(y0*59 - y1*60);
        double phi0 = (zPhiLatitude + y0)/60;
        double phi1 = (zPhiLatitude + y1)/59;
        double A0 = Math.acos(1-((1-Math.cos(2*Math.PI/60))/Math.pow(Math.cos(phi0),2)));
        double A1 = Math.acos(1-((1-Math.cos(2*Math.PI/60))/Math.pow(Math.cos(phi1),2)));
        if(Double.isNaN(A0)){
            //not sure
            evenZoneLocationLat0 = 1;
        } else{
            evenZoneLocationLat0 = Math.floor(2*Math.PI/A0);
        }
        evenZoneLocationLat1 = evenZoneLocationLat0 -1;
        if(Double.isNaN(A1)){
            oddZoneLocationLat0 = 1;
        } else {
            oddZoneLocationLat0 = Math.floor(2*Math.PI/A1);
        }
        oddZoneLocationLat1 = oddZoneLocationLat0-1;
        //not sure
        if((evenZoneLocationLat0 != oddZoneLocationLat0) || (evenZoneLocationLat1 != oddZoneLocationLat1)) return null;
        //Longitude
        double evenZoneLocationLong0 = 1;
        double oddZoneLocationLong0 = 1;
        double zPhiLongitude = Math.rint(x0*59 - x1*60);
        phi0 = (zPhiLongitude + y0)/60;
        phi1 = (zPhiLongitude + y1)/60;
        A0 = Math.acos(1-((1-Math.cos(2*Math.PI/60))/Math.pow(Math.cos(phi0),2)));
        A1 = Math.acos(1-((1-Math.cos(2*Math.PI/60))/Math.pow(Math.cos(phi1),2)));

        if(!Double.isNaN(A0))  evenZoneLocationLong0 = Math.floor(2*Math.PI/A0);
        if(!Double.isNaN(A0)) oddZoneLocationLong0 = Math.floor(2*Math.PI/A1);
        double evenZoneLocationLong1 = evenZoneLocationLong0-1;
        double oddZoneLocationLong1 = oddZoneLocationLong0-1;

        if((evenZoneLocationLong0 != oddZoneLocationLong0) || (evenZoneLocationLong1 != oddZoneLocationLong1))
            return null;

        int actualLatT32;
        int actualLongT32;
        double evenZoneLocationLat = (mostRecent == 0) ? evenZoneLocationLat0 - 0.5 : evenZoneLocationLat1 - 0.5;
        double evenZoneLocationLong = (mostRecent == 0) ? evenZoneLocationLong0 - 0.5 : evenZoneLocationLong1 - 0.5;
        actualLatT32 = (int) Units.convert(evenZoneLocationLat, Units.Angle.TURN, Units.Angle.T32);
        actualLongT32 = (int) Units.convert(evenZoneLocationLong, Units.Angle.TURN, Units.Angle.T32);
        return new GeoPos( actualLongT32, actualLatT32);
    }
}

