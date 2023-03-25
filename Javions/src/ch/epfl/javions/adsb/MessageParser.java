package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;

public class MessageParser {

    private MessageParser() {
    }

    public static Message parse(RawMessage rawMessage) {
        if (AircraftIdentificationMessage.of(rawMessage) != null) return AircraftIdentificationMessage.of(rawMessage);
        if (AirbornePositionMessage.of(rawMessage) != null) return AirbornePositionMessage.of(rawMessage);
        if (AirborneVelocityMessage.of(rawMessage) != null) return AirborneVelocityMessage.of(rawMessage);
        return null; // returns either one of the three Messages depending on type or null if all return null (makes it invalid)
    }
}



