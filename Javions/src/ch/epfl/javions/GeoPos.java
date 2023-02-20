package ch.epfl.javions;

public record GeoPos(int longitudeT32, int latitudeT32) {
    /*Checks if a given latitude is possible :
        by checking the lower bound, if the latitude is lower than the lower bound, it is not valid
        by checking the upper bound, if the latitude is higher than the lower bound, it is not valid
        if the two are false than the latitude must be valid
     */
    public static boolean isValidLatitudeT32(int latitudeT32){
        if (latitudeT32 < Math.scalb(-1,-30)) return false;
        if (latitudeT32 > Math.scalb(1, 30)) return false;
        return true;
    }


}
