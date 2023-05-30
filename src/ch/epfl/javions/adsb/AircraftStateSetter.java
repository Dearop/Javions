package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

/** This interface describes all the methods that are needed to set and update the information for each aircraft
 *
 * @author Paul Quesnot (347572)
 */
public interface AircraftStateSetter {

    void setLastMessageTimeStampNs(long timeStampNs);

    void setCategory(int category);

    void setCallSign(CallSign callSign);

    void setPosition(GeoPos position);

    void setAltitude(double altitude);

    void setVelocity(double velocity);

    void setTrackOrHeading(double trackOrHeading);
}
