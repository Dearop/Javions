package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.aircraft.AircraftDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import java.util.*;

public final class AircraftStateManager {
    private List <AircraftStateAccumulator<ObservableAircraftState>> accumulators = new ArrayList<>();
    private Set<ObservableAircraftState> knownPositionStates = new HashSet<>();

    // TODO: 4/9/2023 I don't get what we're supposed to do in the Constructor
    public AircraftStateManager(AircraftDatabase database){

    }

    // TODO: 4/9/2023 don't know if we should give it a copy or the set itself 
    public ObservableSet states(){
        return FXCollections.observableSet(Set.copyOf(knownPositionStates));
    }

    public void updateWithMessage(Message message) {
        purge(message);
        ObservableAircraftState state = new ObservableAircraftState(message.icaoAddress());
        if(accumulators.contains(state)) {
                accumulators.get(accumulators.indexOf(state)).update(message);

        } else {
            accumulators.add(new AircraftStateAccumulator<>(state));
        }
    }

    public void purge(Message message){
        Iterator<ObservableAircraftState> i = knownPositionStates.iterator();
        while(i.hasNext()){
            if(Math.abs(i.next().getLastMessageTimeStampNs() - message.timeStampNs()) >= 6e10){
                i.remove();
            }
        }
    }
}
