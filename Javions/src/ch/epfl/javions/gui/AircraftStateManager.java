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
    private Map <IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> accumulatorMap ;
    private ObservableSet<ObservableAircraftState> knownPositionStates;
    private AircraftDatabase database;

    public AircraftStateManager(AircraftDatabase database){
        this.database = database;
        this.knownPositionStates = FXCollections.observableSet();
        this.accumulatorMap = new HashMap<>();
    }

    // TODO: 4/9/2023 don't know if we should give it a copy or the set itself 
    public ObservableSet<ObservableAircraftState> states(){
        return (ObservableSet<ObservableAircraftState>) Set.copyOf(knownPositionStates);
    }

    public void updateWithMessage(Message message)throws IOException {;
        purge(message);
        IcaoAddress messageIcaoddress = message.icaoAddress();
        AircraftData data = database.get(messageIcaoddress);
        if(!accumulatorMap.containsKey(messageIcaoddress)) {
            ObservableAircraftState state = new ObservableAircraftState(messageIcaoddress, data);
            accumulatorMap.put(messageIcaoddress,new AircraftStateAccumulator<>(state));
        }
            AircraftStateAccumulator<ObservableAircraftState> desiredAccumulator = accumulatorMap.get(messageIcaoddress);
            desiredAccumulator.update(message);
        if(desiredAccumulator.stateSetter().getPosition() != null)
            knownPositionStates.add(desiredAccumulator.stateSetter());
    }

    public void purge(Message message){
        knownPositionStates.removeIf(observableAircraftState ->
                message.timeStampNs() - observableAircraftState.getLastMessageTimeStampNs() > 6e10);
    }

    public Map<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> getAccumulatorMap(){
        return Collections.unmodifiableMap(accumulatorMap);
    }

    public String toString(IcaoAddress adress){
        return accumulatorMap.get(adress).toString();
    }
}
