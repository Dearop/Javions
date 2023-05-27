package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

/**
 * This class represents the state of an aircraft that can be observed by external entities.
 * It maintains the aircraft's position, velocity, and acceleration, and allows external entities to
 * set the aircraft's trajectory and update its state based on elapsed time.
 *
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public final class ObservableAircraftState extends Observable implements AircraftStateSetter {
    private AircraftStateAccumulator accumulator;
    private AircraftData data;
    private final ObservableList<AirbornePos> trajectories;
    private final ObservableList<AirbornePos> trajectoryProperty;
    private final LongProperty lastMessageTimeStampNs = new SimpleLongProperty();
    private final IntegerProperty category = new SimpleIntegerProperty();
    private final ObjectProperty<CallSign> callSign = new SimpleObjectProperty<>();
    private final ObjectProperty<GeoPos> position = new SimpleObjectProperty<>();
    private final DoubleProperty altitude = new SimpleDoubleProperty(Double.NaN);
    private final DoubleProperty velocity = new SimpleDoubleProperty(Double.NaN);
    private final DoubleProperty trackOrHeading = new SimpleDoubleProperty();
    private final IcaoAddress icaoAddress;

    /**
     * Creates an ObservableAircraftState object with the given IcaoAddress and AircraftData.
     *
     * @param icaoAddress the IcaoAddress of the aircraft
     * @param data        the AircraftData associated with the aircraft
     */
    public ObservableAircraftState(IcaoAddress icaoAddress, AircraftData data) {
        this.icaoAddress = icaoAddress;
        this.data = data;
        trajectories = FXCollections.observableArrayList();
        trajectoryProperty = FXCollections.unmodifiableObservableList(trajectories);
    }

    /**
     * Sets the last message timestamp in nanoseconds.
     *
     * @param timeStampNs the timestamp in nanoseconds
     */
    @Override
    public void setLastMessageTimeStampNs(long timeStampNs) {
        this.lastMessageTimeStampNs.set(timeStampNs);
    }

    /**
     * Sets the category of the aircraft.
     *
     * @param category the category of the aircraft
     */
    @Override
    public void setCategory(int category) {
        this.category.set(category);
    }

    /**
     * Sets the call sign of the aircraft.
     *
     * @param callSign the call sign of the aircraft
     */
    @Override
    public void setCallSign(CallSign callSign) {
        this.callSign.set(callSign);
    }

    /**
     * Sets the position of the aircraft.
     *
     * @param position the GeoPos position of the aircraft
     */
    @Override
    public void setPosition(GeoPos position) {
        this.position.set(position);
        if (!Double.isNaN(altitude.get()))
            trajectories.add(new AirbornePos(position, altitude.get()));
    }

    /**
     * Sets the altitude of the aircraft.
     *
     * @param altitude the altitude of the aircraft
     */
    @Override
    public void setAltitude(double altitude) {
        this.altitude.set(altitude);
        if (position.get() != null) {
            if (trajectories.isEmpty())
                trajectories.add(new AirbornePos(position.get(), altitude));
            else
                trajectories.set(trajectories.size() - 1, new AirbornePos(position.get(), altitude));
        }
    }

    /**
     * Sets the velocity of the aircraft.
     *
     * @param velocity the velocity of the aircraft
     */
    @Override
    public void setVelocity(double velocity) {
        this.velocity.set(velocity);
    }

    /**
     * Sets the track or heading of the aircraft.
     *
     * @param trackOrHeading the track or heading of the aircraft
     */
    @Override
    public void setTrackOrHeading(double trackOrHeading) {
        this.trackOrHeading.set(trackOrHeading);
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


    public List<AirbornePos> getTrajectories() {
        return trajectories;
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
        return trajectoryProperty;
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
     * @return a read-only property representing the current position of the aircraft
     */
    public ReadOnlyObjectProperty<GeoPos> positionProperty() {
        return position;
    }

    /**
     * Returns the aircraft data associated with this observable state.
     *
     * @return the aircraft data
     */
    public AircraftData getData() {
        return this.data;
    }

    /**
     * Represents the position and altitude of an aircraft at a specific point in time.
     * This record is used internally to track an aircraft's trajectory.
     */
    public record AirbornePos(GeoPos position, double altitude) {
    }
}