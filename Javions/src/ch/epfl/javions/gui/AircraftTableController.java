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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static javafx.beans.binding.Bindings.when;

public final class AircraftTableController {
    private static ObservableSet<ObservableAircraftState> knownStates; //not sure todo
    private static ObjectProperty<ObservableAircraftState> currentSelectedState;
    private static TableView <ObservableAircraftState> scenegraph = new TableView<>();
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
    private Consumer<ObservableAircraftState> selectedState;
    // TODO: 5/7/2023 Need to add Constants for column sizes
    private static final int NUMBER_COLUMN_SIZE = 85;
    private static final int MAX_INTEGER_DECIMAL = 4;
    private static final int MIN_INTEGER_DECIMAL = 0;

    public AircraftTableController(ObservableSet<ObservableAircraftState> knownStates,
                                   ObjectProperty<ObservableAircraftState> currentSelectedState) {
        this.knownStates = knownStates;
        this.currentSelectedState = currentSelectedState;
        this.selectedState = selectedState;
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

        scenegraph.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && MouseButton.PRIMARY == event.getButton()) {
                if(selectedState != null)
                    selectedState.accept(scenegraph.getSelectionModel().getSelectedItem());
            }
        });

        currentSelectedState.addListener(e -> {
            if(scenegraph.getColumns().contains(currentSelectedState)){
                scenegraph.scrollTo(currentSelectedState.getValue());
                scenegraph.getSelectionModel().select(currentSelectedState.getValue());
            }
        });
    }

    public static Node pane() {
        scenegraph.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        scenegraph.setTableMenuButtonVisible(true);

        if (scenegraph.getColumns().size() == 0) {

            Set<TableColumn<ObservableAircraftState, String>> numberColumns = new HashSet<>();

            // ICAOAddress
            icaoAddress = new TableColumn<>("IcaoAddress");
            icaoAddress.setPrefWidth(60);
            scenegraph.getColumns().add(icaoAddress);

            // CallSign
            callSign = new TableColumn<>("CallSign");
            callSign.setPrefWidth(70);
            scenegraph.getColumns().add(callSign);

            // Registration
            registration = new TableColumn<>("Registration");
            registration.setPrefWidth(90);
            scenegraph.getColumns().add(registration);

            // Model
            model = new TableColumn<>("Model");
            model.setPrefWidth(230);
            scenegraph.getColumns().add(model);

            //Type
            type = new TableColumn<>("Type");
            type.setPrefWidth(50);
            scenegraph.getColumns().add(type);

            // Description
            description = new TableColumn<>("Description");
            description.setPrefWidth(70);
            scenegraph.getColumns().add(description);

            // Longitude
            longitude = createNumberColumn(numberColumns, longitude, "Longitude (°)");

            // Latitude
            latitude = createNumberColumn(numberColumns, latitude, "Latitude (°)");

            // Altitude
            altitude = createNumberColumn(numberColumns, altitude, "Altitude (m)");

            
            // Velocity
            velocity = createNumberColumn(numberColumns, velocity,"Velocity (km/h)");
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
        altitude.setCellValueFactory(f ->
                f.getValue().altitudeProperty().map(m ->
                        format.format(m.doubleValue())));
        velocity.setCellValueFactory(f ->
                f.getValue().velocityProperty().map(m ->
                        format.format(Units.convertTo(m.doubleValue(), Units.Speed.KILOMETER_PER_HOUR))));

        return scenegraph;
    }

    public void setOnDoubleClick (Consumer<ObservableAircraftState> selectedState) {
        this.selectedState = selectedState;
    }

    public static TableColumn<ObservableAircraftState, String> createNumberColumn(
            Set<TableColumn<ObservableAircraftState, String>> numberColumns,
            TableColumn<ObservableAircraftState, String> column, String columnName){
        column = new TableColumn<>(columnName);
        column.setPrefWidth(NUMBER_COLUMN_SIZE);
        scenegraph.getColumns().add(column);
        numberColumns.add(column);

        column.setComparator((s1, s2) -> {
            try {
                return Double.compare((double) format.parse(s1),
                        (double) format.parse(s2));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
        column.setStyle("-fx-alignment: baseline-right");
        return column;
    }
}
