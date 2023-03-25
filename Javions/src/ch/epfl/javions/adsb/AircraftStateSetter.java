package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

/**
 * @author Paul Quesnot (347572)
 */
public interface AircraftStateSetter {

    public abstract void setLastMessageTimeStampNs(long timeStampNs);

    public abstract void setCategory(int category);

    public abstract void setCallSign(CallSign callSign);

    public abstract void setPosition(GeoPos position);

    public abstract void setAltitude(double altitude);

    public abstract void setVelocity(double velocity);

    public abstract void setTrackOrHeading(double trackOrHeading);
}
