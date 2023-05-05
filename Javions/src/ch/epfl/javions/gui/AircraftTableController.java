package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.AircraftRegistration;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import org.junit.jupiter.api.ClassOrdererContext;

import java.util.Collection;
import java.util.Locale;
import java.util.function.Consumer;

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


        knownStates.addListener((SetChangeListener<ObservableAircraftState>)  change -> {
            if(change.wasAdded()){
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

        if(scenegraph.getColumns().size() == 0){

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
            scenegraph.getColumns().addAll(registration);
            scenegraph.getColumns().addAll(model);
            scenegraph.getColumns().addAll(type);
            scenegraph.getColumns().addAll(description);
        }

        ObservableValue<IcaoAddress> icaoAddressConstant = new ReadOnlyObjectWrapper<>(currentSelectedState.get().icaoAddress());
        icaoAddress.setCellValueFactory(f -> icaoAddressConstant.flatMap(IcaoAddress::string);
        callSign.setCellValueFactory(f -> f.getValue().callSignProperty().map(CallSign::string));


        return scenegraph;
    }

    public Consumer<ObservableAircraftState> setOnDoubleClick() {
        scenegraph.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2 && MouseButton.PRIMARY == event.getButton());

        });

        // HMM
        return new Consumer<ObservableAircraftState>() {
            @Override
            public void accept(ObservableAircraftState observableAircraftState) {

            }
        };
    }
}
