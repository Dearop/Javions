package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

public interface AircraftStateSetter {

    public void setLastMessageTimeStampNs(long timeStampNs);

    public void setCategory(int category);

    public void setCallSign(CallSign callSign);

    public void setPosition(GeoPos position);

    public void setAltitude(double altitude);

    public void setVelocity(double velocity);

    public void setTrackOrHeading(double trackOrHeading);
}
