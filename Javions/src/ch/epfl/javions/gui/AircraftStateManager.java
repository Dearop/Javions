package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.*;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.io.IOException;
import java.util.*;

public final class AircraftStateManager {
    private Map <IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> accumulatorMap = new HashMap<>();
    private ObservableSet<ObservableAircraftState> knownPositionStates;
    private AircraftDatabase database;

    public AircraftStateManager(AircraftDatabase database){
        this.database = database;
        knownPositionStates = FXCollections.observableSet();
    }

    // TODO: 4/9/2023 don't know if we should give it a copy or the set itself 
    public ObservableSet<ObservableAircraftState> states(){
        return knownPositionStates;
    }

    public void updateWithMessage(Message message)throws IOException {
        purge(message);
        AircraftData data = database.get(message.icaoAddress());
        ObservableAircraftState state = new ObservableAircraftState(message.icaoAddress(), data);
        AircraftStateAccumulator<ObservableAircraftState> accumulator = new AircraftStateAccumulator<>(state);
        if(accumulatorMap.get(message.icaoAddress()) != null) {
            accumulatorMap.get(message.icaoAddress()).update(message);
            if(message instanceof AirbornePositionMessage && knownPositionStates.contains(state)){
                Iterator<ObservableAircraftState> i = knownPositionStates.iterator();
                while(i.hasNext()){
                    if(i.next().equals(state))

                }
            }
        } else {
            accumulator.update(message);
            accumulatorMap.put(message.icaoAddress(), accumulator);
            knownPositionStates.add(accumulator);
        }
    }

    public void purge(Message message){
        knownPositionStates.removeIf(observableAircraftState ->
                Math.abs(observableAircraftState.stateSetter().getLastMessageTimeStampNs() - message.timeStampNs()) > 6e10);
    }

    public List<AircraftStateAccumulator<ObservableAircraftState>> getAccumulatorMap(){
        return Collections.unmodifiableList(accumulatorMap);
    }

    public String toString(int index){
        return accumulatorMap.get(index).toString();
    }
}
