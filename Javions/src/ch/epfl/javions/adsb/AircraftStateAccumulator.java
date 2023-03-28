package ch.epfl.javions.adsb;

public class AircraftStateAccumulator<T extends AircraftStateSetter> {
    private final T stateSetter;
    private AirbornePositionMessage latestOddMessage;
    private AirbornePositionMessage latestEvenMessage;

    public AircraftStateAccumulator(T stateSetter) {
        if (null == stateSetter) throw new NullPointerException();
        this.stateSetter = stateSetter;
    }

    public T stateSetter() {
        return stateSetter;
    }

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
                    if (Math.abs(latestOddMessage.timeStampNs() - latestEvenMessage.timeStampNs()) <= 1e10) {
                        double x0, y0, x1, y1;
                        x0 = latestEvenMessage.x();
                        y0 = latestEvenMessage.y();
                        y1 = latestOddMessage.x();
                        x1 = latestOddMessage.y();
                        stateSetter.setPosition(CprDecoder.decodePosition(x0, y0, x1, y1, apm.parity()));
                    }
                }

            }
            default -> throw new NullPointerException();
            //setLast
        }
    }
}