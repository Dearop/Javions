package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;
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

import java.text.NumberFormat;
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
    private static TableColumn<ObservableAircraftState, String> longitude;
    private static TableColumn<ObservableAircraftState, String> latitude;
    private static TableColumn<ObservableAircraftState, String> altitude;
    private static TableColumn<ObservableAircraftState, String> velocity;
    private static NumberFormat format;
    // TODO: 5/7/2023 Need to add Constants for column sizes
    private static final int MAX_INTEGER_DECIMAL = 4;
    private static final int MIN_INTEGER_DECIMAL = 0;

    public AircraftTableController(ObservableSet<ObservableAircraftState> knownStates, ObjectProperty<ObservableAircraftState> currentSelectedState) {
        this.knownStates = knownStates;
        this.currentSelectedState = currentSelectedState;

        this.format = NumberFormat.getInstance();
        format.setMinimumFractionDigits(MIN_INTEGER_DECIMAL);
        format.setMaximumFractionDigits(MAX_INTEGER_DECIMAL);


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

            // Longitude
            longitude = new TableColumn<>("Longitude (°)");
            longitude.setPrefWidth(85);

            // Latitude
            latitude = new TableColumn<>("Latitude (°)");
            latitude.setPrefWidth(85);
            
            // Altitude
            altitude = new TableColumn<>("Altitude (m)");
            altitude.setPrefWidth(85);
            
            // Velocity
            velocity = new TableColumn<>("Velocity (km/h)");
            velocity.setPrefWidth(85);

            // TODO: 5/7/2023 Could be a for loop ? 
            scenegraph.getColumns().add(icaoAddress);
            scenegraph.getColumns().add(callSign);
            scenegraph.getColumns().add(registration);
            scenegraph.getColumns().add(model);
            scenegraph.getColumns().add(type);
            scenegraph.getColumns().add(description);
            scenegraph.getColumns().add(longitude);
            scenegraph.getColumns().add(latitude);
            scenegraph.getColumns().add(altitude);
            scenegraph.getColumns().add(velocity);
        }

        icaoAddress.setCellValueFactory(f -> {
            ReadOnlyObjectWrapper<IcaoAddress> icaoAddressWrapper =
                        new ReadOnlyObjectWrapper<>(f.getValue().icaoAddress());
                return icaoAddressWrapper.map(IcaoAddress::string);
        });

        callSign.setCellValueFactory(f -> f.getValue().callSignProperty().map(CallSign::string));

        registration.setCellValueFactory(f -> {
            if(f.getValue().getData() != null){
                ReadOnlyObjectWrapper<AircraftRegistration> registrationWrapper =
                        new ReadOnlyObjectWrapper<>(f.getValue().getData().registration());
                return registrationWrapper.map(AircraftRegistration::string);
            } else return null;
        });

        model.setCellValueFactory(f -> {
            if(f.getValue().getData() != null) {
                ReadOnlyObjectWrapper<String> modelWrapper =
                        new ReadOnlyObjectWrapper<>(f.getValue().getData().model());
                return modelWrapper.map(String::toString);
            }else return null;
        });
        type.setCellValueFactory(f -> {
            if(f.getValue().getData() != null) {
                ReadOnlyObjectWrapper<AircraftTypeDesignator> typeWrapper =
                        new ReadOnlyObjectWrapper<>(f.getValue().getData().typeDesignator());
                return typeWrapper.map(AircraftTypeDesignator::string);
            }
            return null;
        });
        description.setCellValueFactory(f -> {
            if (f.getValue().getData() != null) {
                ReadOnlyObjectWrapper<AircraftDescription> descriptionWrapper =
                        new ReadOnlyObjectWrapper<>(f.getValue().getData().description());
                return descriptionWrapper.map(AircraftDescription::string);
            }else return null;
        });

        longitude.setCellValueFactory(f ->
                        f.getValue().positionProperty().map(m ->
                                format.format(Units.convertTo(m.longitude(), Units.Angle.DEGREE))));
        latitude.setCellValueFactory(f ->
                f.getValue().positionProperty().map(m ->
                        format.format(Units.convertTo(m.latitude(), Units.Angle.DEGREE))));
        altitude.setCellValueFactory(f -> f.getValue().altitudeProperty().map(m -> format.format(m.doubleValue())));
        velocity.setCellValueFactory(f ->
                f.getValue().velocityProperty().map(m ->
                        format.format(Units.convertTo(m.doubleValue(), Units.Speed.KILOMETER_PER_HOUR))));

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
