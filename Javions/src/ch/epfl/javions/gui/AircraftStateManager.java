package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.aircraft.AircraftDatabase;
import javafx.beans.Observable;
import javafx.beans.binding.ObjectExpression;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.io.IOException;
import java.util.*;

public final class AircraftStateManager {
    private List <AircraftStateAccumulator<ObservableAircraftState>> accumulators = new ArrayList<>();
    // TODO: 4/9/2023 This is guess work but because TreeSet orders things in increasing order it doesn't really
    // make sense to have it as our set
    private Set<ObservableAircraftState> knownPositionStates = new HashSet<>();

    public AircraftStateManager(AircraftDatabase database){

    }

    // TODO: 4/9/2023 don't know if we should give it a copy or the set itself 
    public ObservableSet states(){
        return FXCollections.observableSet(Set.copyOf(knownPositionStates));
    }

    public void updateWithMessage(Message message) {
        ObservableAircraftState state = new ObservableAircraftState(message.icaoAddress());
        if(accumulators.contains(state)) {
            for (AircraftStateAccumulator<ObservableAircraftState> accumulator : accumulators) {
                if (message.icaoAddress().equals(accumulator.stateSetter().icaoAddress()))
                    accumulator.update(message);
            }
        } else {
            accumulators.add(new AircraftStateAccumulator<>(state));
        }
    }

    public void purge(){
        Iterator<ObservableAircraftState> i = knownPositionStates.iterator();
        while(i.hasNext()){
            if(i.next().getLastMessageTimeStampNs() >= 6e10){
                i.remove();
            }
        }
    }
}
