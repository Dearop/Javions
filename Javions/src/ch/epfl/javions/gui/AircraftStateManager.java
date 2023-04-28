package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.*;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.io.IOException;
import java.util.*;

import static javafx.collections.FXCollections.observableSet;

public final class AircraftStateManager {
    private Map <IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> accumulatorMap ;
    private ObservableSet<ObservableAircraftState> knownPositionStates;
    private AircraftDatabase database;

    public AircraftStateManager(AircraftDatabase database){
        this.database = database;
        this.knownPositionStates = observableSet();
        this.accumulatorMap = new HashMap<>();
    }

    public ObservableSet<ObservableAircraftState> states(){
        return (ObservableSet<ObservableAircraftState>) Set.copyOf(knownPositionStates);
    }

    public Set<ObservableAircraftState> knownStates(){
        return Set.copyOf(knownPositionStates);
    }

    public void updateWithMessage(Message message)throws IOException {
        if(message != null){
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
