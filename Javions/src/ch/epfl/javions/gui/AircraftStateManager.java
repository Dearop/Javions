package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.*;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.AircraftDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.io.IOException;
import java.util.*;

public final class AircraftStateManager {
    private List <AircraftStateAccumulator<ObservableAircraftState>> accumulators = new ArrayList<>();
    private Set<AircraftStateAccumulator<ObservableAircraftState>> knownPositionStates = new HashSet<>();
    private AircraftDatabase database;

    public AircraftStateManager(AircraftDatabase database){
        this.database = database;
    }

    // TODO: 4/9/2023 don't know if we should give it a copy or the set itself 
    public ObservableSet<AircraftStateAccumulator<ObservableAircraftState>> states(){
        return FXCollections.observableSet(Set.copyOf(knownPositionStates));
    }

    public void updateWithMessage(Message message)throws IOException {
        purge(message);
        AircraftData data = database.get(message.icaoAddress());
        ObservableAircraftState state = new ObservableAircraftState(message.icaoAddress(), data);
        if(accumulators.contains(state)) {
            accumulators.get(accumulators.indexOf(state)).update(message);
            if(message instanceof AirbornePositionMessage && knownPositionStates.contains(state)){
                Iterator<AircraftStateAccumulator<ObservableAircraftState>> i = knownPositionStates.iterator();
                while(i.hasNext()){
                    if(i.next().equals(state))
                        i.next().update(message);
                }
            }
        } else {
            AircraftStateAccumulator<ObservableAircraftState> newAccumulator = new AircraftStateAccumulator<>(state);
            newAccumulator.update(message);
            accumulators.add(newAccumulator);
            knownPositionStates.add(newAccumulator);
        }
    }

    public void purge(Message message){
        knownPositionStates.removeIf(observableAircraftState ->
                Math.abs(observableAircraftState.stateSetter().getLastMessageTimeStampNs() - message.timeStampNs()) > 6e10);
    }

    public List<AircraftStateAccumulator<ObservableAircraftState>> getAccumulators(){
        return Collections.unmodifiableList(accumulators);
    }

    public String toString(int index){
        return accumulators.get(index).toString();
    }
}
