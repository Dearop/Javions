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
        return stateSetter;
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
        this.stateSetter.setLastMessageTimeStampNs(message.timeStampNs());
        switch (message) {
            case AircraftIdentificationMessage aim -> {
                this.stateSetter.setCategory(aim.category());
                this.stateSetter.setCallSign(aim.callSign());
            }
            case AirborneVelocityMessage avm -> {
                this.stateSetter.setVelocity(avm.speed());
                this.stateSetter.setTrackOrHeading(avm.trackOrHeading());
            }
            case AirbornePositionMessage apm -> {
                this.stateSetter.setAltitude(apm.altitude());
                if (0 == apm.parity()) {
                    this.latestEvenMessage = apm;
                } else {
                    this.latestOddMessage = apm;
                }
                if (null != latestEvenMessage && null != latestOddMessage) {
                    //this if works
                    if (TIME_BETWEEN_TWO_MESSAGES >= Math.abs(latestOddMessage.timeStampNs() - latestEvenMessage.timeStampNs())) {

                        final double x0 = this.latestEvenMessage.x();
                        final double y0 = this.latestEvenMessage.y();
                        final double x1 = this.latestOddMessage.x();
                        final double y1 = this.latestOddMessage.y();
                        if(CprDecoder.decodePosition(x0, y0, x1, y1, apm.parity()) != null)
                            this.stateSetter.setPosition(CprDecoder.decodePosition(x0, y0, x1, y1, apm.parity()));
                    }
                }

            }
            default -> throw new Error();
        }
    }
}