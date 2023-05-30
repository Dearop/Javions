package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;

/** This interface creates the main methods that define a Message object.
 *
 * @author Paul Quesnot (347572)
 */
public interface Message {

    long timeStampNs();

    IcaoAddress icaoAddress();
}
