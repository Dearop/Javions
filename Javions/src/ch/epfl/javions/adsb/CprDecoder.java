package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;

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
    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent){
        Preconditions.checkArgument((mostRecent == 0) || (mostRecent == 1));
        if(mostRecent == 0){
            y0
        }
    }
}
