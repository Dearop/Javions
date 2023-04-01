package ch.epfl.javions.adsb;

/**
 * This class is responsible for parsing a RawMessage into a corresponding Message object.
 * It contains only one static method that takes a RawMessage as argument and returns either an
 * AircraftIdentificationMessage, an AirbornePositionMessage, an AirborneVelocityMessage
 * or null if the rawMessage is not valid.
 * @author Henri Antal (339444)
 */
public enum MessageParser {
    ;

    /**
     * This static method takes a RawMessage object and returns a corresponding Message object,
     * either an AircraftIdentificationMessage, an AirbornePositionMessage, an AirborneVelocityMessage
     * or null if the rawMessage is not valid.
     *
     * @param rawMessage the raw message to be parsed into a corresponding Message object
     * @return a Message object corresponding to the type of the raw message, or null if the raw message is not valid
     */
    public static Message parse(final RawMessage rawMessage) {
        final int checkValue = rawMessage.typeCode();

        if (0 < checkValue && 5 > checkValue)
            return AircraftIdentificationMessage.of(rawMessage);
        if ((8 < checkValue && 19 > checkValue) || (19 < checkValue && 23 > checkValue))
            return AirbornePositionMessage.of(rawMessage);
        if (19 == checkValue) return AirborneVelocityMessage.of(rawMessage);

        // returns either one of the three Messages depending on type or null if all return null (makes it invalid)
        return null;
    }
}



