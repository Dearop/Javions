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

import java.util.function.Consumer;

public final class AircraftTableController {

    private static ObservableSet<ObservableAircraftState> knownStates; //not sure todo

    private static ObjectProperty<ObservableAircraftState> currentSelectedState;

    private static TableView scenegraph = new TableView<>();


    public AircraftTableController(ObservableSet<ObservableAircraftState> knownStates, ObjectProperty<ObservableAircraftState> currentSelectedState) {
        this.knownStates = knownStates;
        this.currentSelectedState = currentSelectedState;
        scenegraph = (TableView) pane();

        knownStates.addListener((SetChangeListener<ObservableAircraftState>)  change -> {
            if(change.wasAdded()){
                scenegraph.getItems().addAll(change);
                System.out.println("add");
            } else {
                scenegraph.getItems().remove(change);
                System.out.println("remove");
            }
            scenegraph.sort();
        });

        setOnDoubleClick();


        //currentSelectedState.bind();
    }

    public static Node pane() {

        TableView<ObservableAircraftState> scenegraph = new TableView<>();
        scenegraph.getStyleClass().add("table.css");
        scenegraph.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        scenegraph.setTableMenuButtonVisible(true);

        // this is needed for constants this example!
        ObservableValue<String> icaoAddressConstant = new ReadOnlyObjectWrapper<>(currentSelectedState.get().icaoAddress().string());

        TableColumn<ObservableAircraftState, String> icaoAddress = new TableColumn<>("IcaoAdress");
        icaoAddress.setPrefWidth(60);

//        icaoAddress.setCellValueFactory(f ->
//            f.getValue().icaoAddress().toString().map(icaoAddressConstant::string   );


        TableColumn<ObservableAircraftState, String> callSign = new TableColumn<>("CallSign");
        callSign.setPrefWidth(70);

        // callSign here gets called and put into a string with the lambda
        callSign.setCellValueFactory(f ->
                f.getValue().callSignProperty().map(CallSign::string));

        scenegraph.getColumns().addAll(callSign);


        TableColumn<ObservableAircraftState, String> registration = new TableColumn<>("Registration");
        callSign.setPrefWidth(90);

        // callSign here gets called and put into a string with the lambda
//        callSign.setCellValueFactory(f ->
//                f.getValue().getData().registration().string(AircraftRegistration::string));

        scenegraph.getColumns().addAll(callSign);


        return scenegraph;
    }

    private void setCellValueFactory() {

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
