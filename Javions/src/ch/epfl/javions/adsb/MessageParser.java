package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;

public class MessageParser {

    private MessageParser() {
    }

    public static Message parse(RawMessage rawMessage) {
        int checkValue = rawMessage.typeCode();

        if (checkValue > 0 && checkValue < 5)
            return AircraftIdentificationMessage.of(rawMessage);
        if ((checkValue > 8 && checkValue < 19) || (checkValue > 19 && checkValue < 23))
            return AirbornePositionMessage.of(rawMessage);
        if (checkValue == 19) return AirborneVelocityMessage.of(rawMessage);

        // returns either one of the three Messages depending on type or null if all return null (makes it invalid)
        return null;
    }
}



