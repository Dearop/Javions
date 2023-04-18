package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.*;

// TODO: 4/9/2023 I don't know if it should extend Observable or if we should create a Subject interface. 
public final class ObservableAircraftState extends Observable implements AircraftStateSetter {
    private AircraftStateAccumulator accumulator;
    private AircraftData data;
    private IcaoAddress icaoAddress;
    private ObservableList<AirbornePos> trajectories = FXCollections.observableArrayList();
    private LongProperty lastMessageTimeStampNs = new SimpleLongProperty();
    private IntegerProperty category = new SimpleIntegerProperty();
    private ObjectProperty<CallSign> callSign = new SimpleObjectProperty<>();
    private ObjectProperty<GeoPos> position = new SimpleObjectProperty<>();
    private DoubleProperty altitude = new SimpleDoubleProperty();
    private DoubleProperty velocity = new SimpleDoubleProperty();
    private DoubleProperty trackOrHeading = new SimpleDoubleProperty();
    private long previousMessageTimeStampNs;

    // TODO: 4/9/2023 actually am kinda puzzled about this, are we supposed to put AircraftData or AircraftDatabase??
    public ObservableAircraftState(IcaoAddress icaoAddress, AircraftData data) {
        this.icaoAddress = icaoAddress;
        this.data = data;
    }

    @Override
    public void setLastMessageTimeStampNs(long timeStampNs) {
        this.lastMessageTimeStampNs.set(timeStampNs);
    }

    @Override
    public void setCategory(int category) {
        this.category.set(category);
    }

    @Override
    public void setCallSign(CallSign callSign) {
        this.callSign.set(callSign);
    }

    @Override
    public void setPosition(GeoPos position) {
        this.position.set(position);
        setTrajectory(altitude.get(), position);
    }

    // TODO: 4/10/2023 Not sure about all this.
    @Override
    public void setAltitude(double altitude) {
        this.altitude.set(altitude);
        setTrajectory(altitude, position.get());
    }

    @Override
    public void setVelocity(double velocity) {
        this.velocity.set(velocity);
    }

    @Override
    public void setTrackOrHeading(double trackOrHeading) {
        this.trackOrHeading.set(trackOrHeading);
    }

    private void setTrajectory(double altitude, GeoPos position) {
        if (!trajectories.isEmpty()) {
            if (trajectories.isEmpty() || !trajectories.get(trajectories.size() - 1).position().equals(position))
                trajectories.add(new AirbornePos(position, altitude));
            else if (previousMessageTimeStampNs == lastMessageTimeStampNs.get())
                trajectories.set(trajectories.size() - 1, new AirbornePos(position, altitude));
        }
        previousMessageTimeStampNs = lastMessageTimeStampNs.get();
    }

    public IcaoAddress icaoAddress() {
        return icaoAddress;
    }

    public long getLastMessageTimeStampNs() {
        return lastMessageTimeStampNs.get();
    }

    public int getCategory() {
        return category.get();
    }

    public CallSign getCallSign() {
        return callSign.get();
    }

    public AirbornePos getTrajectory() {
        if (trajectories.size() != 0)
            return trajectories.get(trajectories.size());
        return null;
    }

    public GeoPos getPosition() {
        return position.get();
    }

    public double getAltitude() {
        return altitude.get();
    }

    public double getVelocity() {
        return velocity.get();
    }

    public double getTrackOrHeading() {
        return trackOrHeading.get();
    }

    public ReadOnlyLongProperty lastMessageTimeStampNsProperty() {
        return lastMessageTimeStampNs;
    }

    public ReadOnlyIntegerProperty categoryProperty() {
        return category;
    }

    public ReadOnlyObjectProperty<CallSign> callSignProperty() {
        return callSign;
    }

    // TODO: 4/8/2023 I feel by not making the unmodifiableObservableList an attribute the code is cleaner, lmk. 
    public ObservableList<AirbornePos> trajectoryProperty() {
        return FXCollections.unmodifiableObservableList(trajectories);
    }

    public ReadOnlyDoubleProperty altitudeProperty() {
        return altitude;
    }

    public ReadOnlyDoubleProperty velocityProperty() {
        return velocity;
    }

    public ReadOnlyDoubleProperty trackOrHeadingProperty() {
        return trackOrHeading;
    }

    // TODO: 4/13/2023 delete this when we figure out how to do the test correctly
    public String toString() {
        StringBuilder b = new StringBuilder();
        return b.append(getLastMessageTimeStampNs()).append(getCategory()).append(getCallSign()).append(getTrajectory()).append(getVelocity()).append(getTrackOrHeading()).toString();
    }

    // TODO: 4/10/2023 adding the timeStamp is guesswork
    private record AirbornePos(GeoPos position, double altitude) {
    }
}
