package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.aircraft.AircraftDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.util.*;

public final class AircraftStateManager {
    private List <ObservableAircraftState> aircraftStates = new ArrayList<>();
    private Set<ObservableAircraftState> knownPositionStates = new HashSet<>();

    public AircraftStateManager(AircraftDatabase database){

    }

    public ObservableSet states(){
        return FXCollections.observableSet(knownPositionStates);
    }

    public AircraftStateSetter updateWithMessage(Message message){

    }


}
