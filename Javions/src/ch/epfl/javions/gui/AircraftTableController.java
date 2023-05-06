package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftDescription;
import ch.epfl.javions.aircraft.AircraftRegistration;
import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;

import java.util.function.Consumer;

import static javafx.beans.binding.Bindings.when;

public final class AircraftTableController {
    private static ObservableSet<ObservableAircraftState> knownStates; //not sure todo
    private static ObjectProperty<ObservableAircraftState> currentSelectedState;
    private static TableView scenegraph = new TableView<>();
    private static TableColumn<ObservableAircraftState, String> icaoAddress;
    private static TableColumn<ObservableAircraftState, String> callSign;
    private static TableColumn<ObservableAircraftState, String> registration;
    private static TableColumn<ObservableAircraftState, String> model;
    private static TableColumn<ObservableAircraftState, String> type;
    private static TableColumn<ObservableAircraftState, String> description;

    public AircraftTableController(ObservableSet<ObservableAircraftState> knownStates, ObjectProperty<ObservableAircraftState> currentSelectedState) {
        this.knownStates = knownStates;
        this.currentSelectedState = currentSelectedState;


        knownStates.addListener((SetChangeListener<ObservableAircraftState>) change -> {
            if (change.wasAdded()) {
                scenegraph.getItems().addAll(change.getElementAdded());
            } else {
                scenegraph.getItems().remove(change.getElementRemoved());
            }
            scenegraph.sort();
        });

        setOnDoubleClick();


        //currentSelectedState.bind();
    }

    public static Node pane() {
        scenegraph.getStyleClass().add("table.css");
        scenegraph.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        scenegraph.setTableMenuButtonVisible(true);

        if (scenegraph.getColumns().size() == 0) {

            // ICAOAddress
            icaoAddress = new TableColumn<>("IcaoAdress");
            icaoAddress.setPrefWidth(60);

            // CallSign
            callSign = new TableColumn<>("CallSign");
            callSign.setPrefWidth(70);

            // Registration
            registration = new TableColumn<>("Registration");
            registration.setPrefWidth(90);

            // Model
            model = new TableColumn<>("Model");
            model.setPrefWidth(230);

            //Type
            type = new TableColumn<>("Type");
            type.setPrefWidth(50);

            // Description
            description = new TableColumn<>("Description");
            description.setPrefWidth(70);

            scenegraph.getColumns().add(icaoAddress);
            scenegraph.getColumns().add(callSign);
            scenegraph.getColumns().add(registration);
            scenegraph.getColumns().add(model);
            scenegraph.getColumns().add(type);
            scenegraph.getColumns().add(description);
        }

        icaoAddress.setCellValueFactory(f -> f.getValue().icaoAddressObservableValue().map(IcaoAddress::string));
        callSign.setCellValueFactory(f -> f.getValue().callSignProperty().map(CallSign::string));

        registration.setCellValueFactory(f -> {
            if(f.getValue().getData() != null)
                return f.getValue().registrationObservableValue().map(AircraftRegistration::string);
            else return null;
        });

        model.setCellValueFactory(f -> {
            if(f.getValue().getData() != null)
                return f.getValue().modelObservableValue().map(String::toString);
            else return null;
        });
        type.setCellValueFactory(f -> {
            if(f.getValue().getData() != null) {
                if (f.getValue().getData().typeDesignator() != null) {
                    return f.getValue().aircraftTypeDesignatorObservableValue().map(AircraftTypeDesignator::string);
                }
            }
            return null;
        });
        description.setCellValueFactory(f -> {
            if (f.getValue().getData() != null)
                return f.getValue().aircraftDescriptionObservableValue().map(AircraftDescription::string);
            else return null;
        });

        return scenegraph;
    }

    public Consumer<ObservableAircraftState> setOnDoubleClick() {
        scenegraph.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && MouseButton.PRIMARY == event.getButton()) ;

        });

        // HMM
        return new Consumer<ObservableAircraftState>() {
            @Override
            public void accept(ObservableAircraftState observableAircraftState) {

            }
        };
    }
}
