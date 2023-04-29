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

/**
 *  This class represents the AircraftStateManager, which is responsible for managing and updating the states of
 *  all aircraft tracked by the system. It has an accumulator map that maps each aircraft's IcaoAddress to its current
 *  AircraftStateAccumulator, an ObservableSet that stores the known aircraft positions, and an AircraftDatabase.
 *  It provides a method for updating the states of the aircraft with a new message, another method for purging
 *  outdated aircraft states, and a method for getting the state accumulator map.
 *
 * @author Paul Quesnot (347572)
 */
public final class AircraftStateManager {

    // A map that maps each aircraft's IcaoAddress to its current AircraftStateAccumulator.
    private Map <IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> accumulatorMap ;

    // An ObservableSet that stores the known aircraft positions.
    private ObservableSet<ObservableAircraftState> knownPositionStates;

    // An AircraftDatabase has all the information about the aircrafts available.
    private AircraftDatabase database;

    /**
     * Constructs an AircraftStateManager with the given AircraftDatabase.
     * @param database the AircraftDatabase to use
     */
    public AircraftStateManager(AircraftDatabase database){
        this.database = database;
        this.knownPositionStates = observableSet();
        this.accumulatorMap = new HashMap<>();
    }

    /**
     * @return an unmodifiable ObservableSet containing the known aircraft positions
     */
    public ObservableSet<ObservableAircraftState> states(){
        return observableSet(Set.copyOf(knownPositionStates));
    }

    /**
     * @return an unmodifiable Map containing the accumulator map
     */
    public Map<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> getAccumulatorMap(){
        return Collections.unmodifiableMap(accumulatorMap);
    }


    /**
     * Updates the states of the aircraft with the given Message.
     * @param message the Message to update the states with
     * @throws IOException if an error occurs while updating the states
     */
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

    /**
     * Purges outdated aircraft states. //TODO what's up with this method, do we still need it?
     * @param message the Message to use for purging outdated aircraft states
     */
    public void purge(Message message){
        knownPositionStates.removeIf(observableAircraftState ->
                message.timeStampNs() - observableAircraftState.getLastMessageTimeStampNs() > 6e10);
    }

    /**
     * Returns the String representation of the AircraftStateAccumulator for the given IcaoAddress.
     * @param adress the IcaoAddress of the aircraft to get the String representation for
     * @return the String representation of the AircraftStateAccumulator for the given IcaoAddress
     */
    public String toString(IcaoAddress adress){
        return accumulatorMap.get(adress).toString();
    }
}
