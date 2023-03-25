package ch.epfl.javions.adsb;

public class AircraftStateAccumulator<T> {

    private T stateSetter;
    AircraftStateSetter aircraft;

    public AircraftStateAccumulator(T stateSetter) {
        if (stateSetter == null) throw new NullPointerException();
        this.stateSetter = stateSetter;
    }

    public T stateSetter() {
        return stateSetter;
    }

    public void update(Message message) {
        //setLast
    }
}
