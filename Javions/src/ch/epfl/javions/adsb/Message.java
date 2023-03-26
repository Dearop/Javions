package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;

/**
 * @author Paul Quesnot (347572)
 */
public interface Message {

    long timeStampNs();

    IcaoAddress icaoAddress();
}
