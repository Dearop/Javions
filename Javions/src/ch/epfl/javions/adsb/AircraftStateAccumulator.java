package ch.epfl.javions.adsb;

public class AircraftStateAccumulator<T extends AircraftStateSetter> {

    private final T stateSetter;
    private long lastTimeStampNs;
    private int lastValidParity;
    private double previousX;
    private double previousY;
    public AircraftStateAccumulator(T stateSetter) {
        if (stateSetter == null) throw new NullPointerException();
        this.stateSetter = stateSetter;
    }

    public T stateSetter() {
        return stateSetter;
    }

    public void update(Message message) {
        lastTimeStampNs = message.timeStampNs();
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
                if (lastTimeStampNs - message.timeStampNs() >= 10 && lastValidParity != apm.parity()) {
                    lastValidParity = apm.parity();
                    double x0, y0, x1, y1;
                    if (lastValidParity == 0) {
                        x0 = apm.x();
                        y0 = apm.y();
                        y1 = previousY;
                        x1 = previousX;
                    }
                    x0 = previousX;
                    y0 = previousY;
                    x1 = apm.x();
                    y1 = apm.y();
                    stateSetter.setPosition(CprDecoder.decodePosition(x0, y0, x1, y1, apm.parity()));
                    previousX = apm.x();
                    previousY = apm.y();
                }
            }
            default -> throw new NullPointerException();
            //setLast
        }
    }
}
