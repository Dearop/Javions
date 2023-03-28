package ch.epfl.javions.adsb;

public class AircraftStateAccumulator<T extends AircraftStateSetter> {
    private final T stateSetter;
    private AirbornePositionMessage latestOddMessage;
    private AirbornePositionMessage latestEvenMessage;

    public AircraftStateAccumulator(T stateSetter) {
        if (stateSetter == null) throw new NullPointerException();
        this.stateSetter = stateSetter;
    }

    public T stateSetter() {
        return stateSetter;
    }

    public void update(Message message) {
        stateSetter.setLastMessageTimeStampNs(message.timeStampNs());
        AirbornePositionMessage latestAccordingMessage;
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
                if(apm.parity() == 0){
                    latestAccordingMessage = latestOddMessage;
                }
                latestAccordingMessage = latestEvenMessage;
                if (message.timeStampNs() - latestAccordingMessage.timeStampNs() >= 1E10 &&
                        latestAccordingMessage.parity() != apm.parity()) {
                    double x0, y0, x1, y1;
                    if (apm.parity() == 0) {
                        x0 = apm.x();
                        y0 = apm.y();
                        y1 = latestAccordingMessage.x();
                        x1 = latestAccordingMessage.y();
                        latestEvenMessage = apm;
                    }
                    x0 = latestAccordingMessage.x();
                    y0 = latestAccordingMessage.y();
                    x1 = apm.x();
                    y1 = apm.y();
                    latestOddMessage = apm;
                    stateSetter.setPosition(CprDecoder.decodePosition(x0, y0, x1, y1, apm.parity()));
                }
            }
            default -> throw new NullPointerException();
            //setLast
        }
    }
}