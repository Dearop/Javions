package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

// TODO: 4/9/2023 I don't know if it should extend Observable or if we should create a Subject interface. 
public final class ObservableAircraftState extends Observable implements AircraftStateSetter{
    private AircraftStateAccumulator accumulator;
    private IcaoAddress icaoAddress;
    private ObservableList<AirbornePos> trajectories = FXCollections.observableArrayList();
    private LongProperty lastMessageTimeStampNs = new SimpleLongProperty();
    private IntegerProperty category = new SimpleIntegerProperty();
    private ObjectProperty<CallSign> callSign = new SimpleObjectProperty<>();
    private ObjectProperty<GeoPos> position = new SimpleObjectProperty<>();
    private DoubleProperty altitude = new SimpleDoubleProperty();
    private DoubleProperty velocity = new SimpleDoubleProperty();
    private DoubleProperty trackOrHeading = new SimpleDoubleProperty();

    // TODO: 4/9/2023 this is bollocks 
    public ObservableAircraftState(IcaoAddress icaoAddress) {
        this.icaoAddress = icaoAddress;
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
        if(trajectories.size() == 0 && altitude.get() != 0 || !trajectories.get(trajectories.size()-1).position().equals(position))
            this.trajectories.add(new AirbornePos(position, altitude.get()));
    }

    // TODO: 4/10/2023 Not sure about all this.
    @Override
    public void setAltitude(double altitude) {
        this.altitude.set(altitude);
        if(trajectories.size() != 0 && position != null && trajectories.get(trajectories.size()-1).altitude() != altitude)
            this.trajectories.add(new AirbornePos(position.get(), altitude));
    }

    @Override
    public void setVelocity(double velocity) {
        this.velocity.set(velocity);
    }

    @Override
    public void setTrackOrHeading(double trackOrHeading) {
        this.trackOrHeading.set(trackOrHeading);
    }

    public IcaoAddress icaoAddress(){
        return icaoAddress;
    }

    public long getLastMessageTimeStampNs(){
        return lastMessageTimeStampNs.get();
    }

    public int getCategory(){
        return category.get();
    }

    public CallSign getCallSign(){
        return callSign.get();
    }

    public AirbornePos trajectory(){
        return trajectories.get(trajectories.size()-1);
    }

    public GeoPos getPosition(){
        return position.get();
    }

    public double getAltitude(){
        return altitude.get();
    }

    public double getVelocity(){
        return velocity.get();
    }

    public double getTrackOrHeading(){
        return trackOrHeading.get();
    }

    public ReadOnlyLongProperty lastMessageTimeStampNsProperty(){
        return lastMessageTimeStampNs;
    }

    public ReadOnlyObjectProperty<CallSign> callSignProperty(){
        return callSign;
    }

    // TODO: 4/8/2023 I feel by not making the unmodifiableObservableList an attribute the code is cleaner, lmk. 
    public ObservableList<AirbornePos> trajectoryProperty(){
        ObservableList<AirbornePos> trajectoriesProperty = FXCollections.unmodifiableObservableList(trajectories);
        return trajectoriesProperty;
    }

    public ReadOnlyIntegerProperty categoryProperty(){
        return category;
    }

    record AirbornePos(GeoPos position,double altitude){}
}
