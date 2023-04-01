package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

public class AircraftState implements AircraftStateSetter {
    @Override
    public void setLastMessageTimeStampNs(final long timeStampNs) {
        //System.out.println("timeStampNs : " + timeStampNs);
    }

    @Override
    public void setCategory(final int category) {
        //System.out.println("category : " + category);
    }

    @Override
    public void setCallSign(final CallSign callSign) {
        System.out.println("callsign : " + callSign);
    }


    @Override
    public void setAltitude(final double altitude) {
        //System.out.println("altitude : " + altitude);
    }

    @Override
    public void setVelocity(final double velocity) {
        //System.out.println("velocity : " + velocity);
    }

    @Override
    public void setTrackOrHeading(final double trackOrHeading) {
        //System.out.println("track or heading : " + trackOrHeading);
    }

    @Override
    public void setPosition(final GeoPos position) {
        System.out.println("position : " + position);
    }

}