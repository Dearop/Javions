package ch.epfl.javions.adsb;
/**
 * The AircraftStateAccumulator class accumulates and updates state information of an aircraft based
 * on the received ADS-B messages. It is used to maintain the current state of an aircraft,
 * which includes its position, velocity, altitude, category, and call sign.
 * @param <T> generic type for the stateSetter.
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public class AircraftStateAccumulator<T extends AircraftStateSetter> {
    private final T stateSetter;
    private AirbornePositionMessage latestOddMessage;
    private AirbornePositionMessage latestEvenMessage;
    // 10 seconds in nanoseconds
    private static final double TIME_BETWEEN_TWO_MESSAGES = 1.0e10;

    /**
     * Constructs an AircraftStateAccumulator instance with the given state setter.
     *
     * @param stateSetter the state setter to be used to update the aircraft state
     * @throws NullPointerException if the given state setter is null
     */
    public AircraftStateAccumulator(T stateSetter) {
        if (null == stateSetter) throw new NullPointerException();
        this.stateSetter = stateSetter;
    }
    /**
     * Returns the state setter used by this accumulator.
     *
     * @return the state setter
     */
    public T stateSetter() {
        return this.stateSetter;
    }
    /**
     * Updates the aircraft state with the information contained in the given message. Depending on the message
     * we either update the identification (lambda aim) or the velocity and trackOrHeading (lambda avm) or at last
     * we update the position (lambda apm). Inside the apm also the last message gets stored, so it is possible to
     * see the difference in time between the messages. The messages have to be updated in a time interval of 10 seconds.
     *
     * @param message the message containing the information to update the state
     * @throws NullPointerException if the given message is null
     */
    public void update(Message message) {
        stateSetter.setLastMessageTimeStampNs(message.timeStampNs());
        switch (message) {
            case AircraftIdentificationMessage aim -> {
                stateSetter.setCategory(aim.category());
                stateSetter.setCallSign(aim.callSign());
            }
            case AirborneVelocityMessage avm -> {
                stateSetter.setVelocity(avm.speed());
                stateSetter.setTrackOrHeading(avm.trackOrHeading());
            }
            case AirbornePositionMessage apm -> {
                stateSetter.setAltitude(apm.altitude());
                if (0 == apm.parity()) {
                    latestEvenMessage = apm;
                } else {
                    latestOddMessage = apm;
                }
                if (latestEvenMessage != null && latestOddMessage != null) {
                    //this if works
                    if (Math.abs(latestOddMessage.timeStampNs() - latestEvenMessage.timeStampNs()) <=
                            TIME_BETWEEN_TWO_MESSAGES) {

                        double x0 = latestEvenMessage.x();
                        double y0 = latestEvenMessage.y();
                        double x1 = latestOddMessage.x();
                        double y1 = latestOddMessage.y();
                        stateSetter.setPosition(CprDecoder.decodePosition(x0, y0, x1, y1, apm.parity()));
                    }
                }

            }
            default -> throw new Error();
        }
    }
}