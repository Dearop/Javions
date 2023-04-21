package ch.epfl.javions.adsb;

/**
 * This class is responsible for parsing a RawMessage into a corresponding Message object.
 * It contains only one static method that takes a RawMessage as argument and returns either an
 * AircraftIdentificationMessage, an AirbornePositionMessage, an AirborneVelocityMessage
 * or null if the rawMessage is not valid.
 * @author Henri Antal (339444)
 */
public class MessageParser {

    private static final int IDENTIFICATION_START = 0;
    private static final int IDENTIFICATION_END = 5;
    private static final int POSITION_START = 8;
    private static final int POSITION_END = 23;
    private static final int TYPE_19 = 19;

    private MessageParser(){}

    /**
     * This static method takes a RawMessage object and returns a corresponding Message object,
     * either an AircraftIdentificationMessage, an AirbornePositionMessage, an AirborneVelocityMessage
     * or null if the rawMessage is not valid.
     *
     * @param rawMessage the raw message to be parsed into a corresponding Message object
     * @return a Message object corresponding to the type of the raw message, or null if the raw message is not valid
     */
    public static Message parse(RawMessage rawMessage) {
        int checkValue = rawMessage.typeCode();
        if (IDENTIFICATION_START < checkValue && IDENTIFICATION_END > checkValue)
            return  AircraftIdentificationMessage.of(rawMessage);
        if ((POSITION_START < checkValue && TYPE_19 > checkValue) || (TYPE_19 < checkValue && POSITION_END > checkValue))
            return AirbornePositionMessage.of(rawMessage);
        if (TYPE_19 == checkValue)
            return AirborneVelocityMessage.of(rawMessage);
        // returns either one of the three Messages depending on type or null if all return null (makes it invalid)
        return null;
    }
}



