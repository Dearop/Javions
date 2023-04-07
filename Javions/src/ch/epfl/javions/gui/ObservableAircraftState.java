package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.aircraft.IcaoAddress;

public final class ObservableAircraftState implements AircraftStateSetter {

    AircraftStateAccumulator accumulator;

//    public ObservableAircraftState(IcaoAddress icaoAddress, ){
//        this.accumulator = accumulator;
//    }
    @Override
    public void setLastMessageTimeStampNs(long timeStampNs) {

    }

    @Override
    public void setCategory(int category) {

    }

    @Override
    public void setCallSign(CallSign callSign) {

    }

    @Override
    public void setPosition(GeoPos position) {

    }

    @Override
    public void setAltitude(double altitude) {

    }

    @Override
    public void setVelocity(double velocity) {

    }

    @Override
    public void setTrackOrHeading(double trackOrHeading) {

    }


}
