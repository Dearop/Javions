package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.aircraft.AircraftDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.util.*;

public final class AircraftStateManager {
    private List <ObservableAircraftState> aircraftStates = new ArrayList<>();
    // TODO: 4/9/2023 This is guess work but because TreeSet orders things in increasing order it doesn't really
    // make sense to have it as our set
    private Set<ObservableAircraftState> knownPositionStates = new HashSet<>();

    public AircraftStateManager(AircraftDatabase database){

    }

    // TODO: 4/9/2023 don't know if we should give it a copy or the set itself 
    public ObservableSet states(){
        return FXCollections.observableSet(Set.copyOf(knownPositionStates));
    }

    public AircraftStateSetter updateWithMessage(Message message){

    }


}
