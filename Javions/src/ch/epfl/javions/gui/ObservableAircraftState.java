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
// TODO still stands?

/**
 *  This class represents the state of an aircraft that can be observed by external entities.
 *  It maintains the aircraft's position, velocity, and acceleration, and allows external entities to
 *  set the aircraft's trajectory and update its state based on elapsed time.
 *
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
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
    private ObjectProperty<AircraftData> dataProperty = new SimpleObjectProperty<>();
    private long previousMessageTimeStampNs;

    /**
     * Creates an ObservableAircraftState object with the given IcaoAddress and AircraftData.
     * @param icaoAddress the IcaoAddress of the aircraft
     * @param data the AircraftData associated with the aircraft
     */
    public ObservableAircraftState(IcaoAddress icaoAddress, AircraftData data) {
        this.icaoAddress = icaoAddress;
        this.data = data;
        dataProperty.set(data);
    }

    /**
     * Sets the last message timestamp in nanoseconds.
     * @param timeStampNs the timestamp in nanoseconds
     */
    @Override
    public void setLastMessageTimeStampNs(long timeStampNs) {
        this.lastMessageTimeStampNs.set(timeStampNs);
    }

    /**
     * Sets the category of the aircraft.
     * @param category the category of the aircraft
     */
    @Override
    public void setCategory(int category) {
        this.category.set(category);
    }

    /**
     * Sets the call sign of the aircraft.
     * @param callSign the call sign of the aircraft
     */
    @Override
    public void setCallSign(CallSign callSign) {
        this.callSign.set(callSign);
    }

    /**
     * Sets the position of the aircraft.
     * @param position the GeoPos position of the aircraft
     */
    @Override
    public void setPosition(GeoPos position) {
        this.position.set(position);
        setTrajectory(altitude.get(), position);
    }

    /**
     * Sets the altitude of the aircraft.
     * @param altitude the altitude of the aircraft
     */
    @Override
    public void setAltitude(double altitude) {
        this.altitude.set(altitude);
        setTrajectory(altitude, position.get());
    }

    /**
     * Sets the velocity of the aircraft.
     * @param velocity the velocity of the aircraft
     */
    @Override
    public void setVelocity(double velocity) {
        this.velocity.set(velocity);
    }

    /**
     * Sets the track or heading of the aircraft.
     * @param trackOrHeading the track or heading of the aircraft
     */
    @Override
    public void setTrackOrHeading(double trackOrHeading) {
        this.trackOrHeading.set(trackOrHeading);
    }

    /**
     * Updates the trajectory list with a new position and altitude based on the received aircraft state message.
     * If the trajectory list is not empty and the last position is different from the new position,
     * a new trajectory point is added to the list. If the last message timestamp is equal to the current message timestamp,
     * then the last trajectory point is updated with the new altitude and position.
     * @param altitude the altitude of the aircraft
     * @param position the position of the aircraft
     */
    private void setTrajectory(double altitude, GeoPos position) {
        if (!trajectories.isEmpty()) {
            if (!trajectories.get(trajectories.size() - 1).position().equals(position))
                trajectories.add(new AirbornePos(position, altitude));
            else if (previousMessageTimeStampNs == lastMessageTimeStampNs.get())
                trajectories.set(trajectories.size() - 1, new AirbornePos(position, altitude));
        }
        previousMessageTimeStampNs = lastMessageTimeStampNs.get();
    }

    /**
     * @return the IcaoAddress of the aircraft
     */
    public IcaoAddress icaoAddress() {
        return icaoAddress;
    }

    /**
     * @return the last message timestamp in nanoseconds
     */
    public long getLastMessageTimeStampNs() {
        return lastMessageTimeStampNs.get();
    }

    /**
     * @return the category of the aircraft
     */
    public int getCategory() {
        return category.get();
    }

    /**
     * @return the call sign of the aircraft
     */
    public CallSign getCallSign() {
        return callSign.get();
    }

    /**
     * @return the latest trajectory of the aircraft
     */
    public AirbornePos getTrajectory() {
        if (trajectories.size() != 0)
            return trajectories.get(trajectories.size() - 1);
        return null;
    }

    /**
     * @return the GeoPos position of the aircraft
     */
    public GeoPos getPosition() {
        return position.get();
    }

    /**
     * @return the altitude of the aircraft
     */
    public double getAltitude() {
        return altitude.get();
    }

    /**
     * @return the velocity of the aircraft
     */
    public double getVelocity() {
        return velocity.get();
    }

    /**
     * @return the track or heading of the aircraft
     */
    public double getTrackOrHeading() {
        return trackOrHeading.get();
    }

    /**
     * @return a read-only property representing the timestamp of the last received message for the aircraft state,
     * in nanoseconds
     */
    public ReadOnlyLongProperty lastMessageTimeStampNsProperty() {
        return lastMessageTimeStampNs;
    }

    /**
     * @return a read-only property representing the category of the aircraft
     */
    public ReadOnlyIntegerProperty categoryProperty() {
        return category;
    }

    /**
     * @return a read-only property representing the call sign of the aircraft
     */
    public ReadOnlyObjectProperty<CallSign> callSignProperty() {
        return callSign;
    }

    /**
     * @return an unmodifiable view of the list of airborne positions of the aircraft
     */
    public ObservableList<AirbornePos> trajectoryProperty() {
        return FXCollections.unmodifiableObservableList(trajectories);
    }

    /**
     * @return a read-only property representing the current altitude of the aircraft
     */
    public ReadOnlyDoubleProperty altitudeProperty() {
        return altitude;
    }

    /**
     * @return a read-only property representing the current velocity of the aircraft
     */
    public ReadOnlyDoubleProperty velocityProperty() {
        return velocity;
    }

    /**
     * @return a read-only property representing the current track or heading of the aircraft
     */
    public ReadOnlyDoubleProperty trackOrHeadingProperty() {
        return trackOrHeading;
    }

    /**
     *
     * @return a read-only property representing the current position of the aircraft
     */
    public ReadOnlyObjectProperty<GeoPos> positionProperty(){
        return position;
    }

    /**
     * Returns the aircraft data associated with this observable state.
     * @return the aircraft data
     */
    public AircraftData getData(){
        return this.data;
    }

    public ReadOnlyObjectProperty<AircraftData> dataProperty(){
        return this.dataProperty;
    }
    /**
     * Represents the position and altitude of an aircraft at a specific point in time.
     * This record is used internally to track an aircraft's trajectory.
     */
    private record AirbornePos(GeoPos position, double altitude) {}
}
