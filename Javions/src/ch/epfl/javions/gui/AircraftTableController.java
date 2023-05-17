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

/**
 * The AircraftTableController class is the controller for the aircraft table view in the JavaFX user interface. It provides
 * functionalities to display the aircraft data in a table view.
 *
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public final class AircraftTableController {

    // set of all known aircraft states
    private static ObservableSet<ObservableAircraftState> knownStates;

    // currently selected aircraft state
    private static ObjectProperty<ObservableAircraftState> currentSelectedState;

    // table view that displays aircraft state information
    private static TableView <ObservableAircraftState> scenegraph = new TableView<>();

    // table column that displays ICAO address
    private static TableColumn<ObservableAircraftState, String> icaoAddress;

    // table column that displays call sign
    private static TableColumn<ObservableAircraftState, String> callSign;

    // table column that displays registration
    private static TableColumn<ObservableAircraftState, String> registration;

    // table column that displays model
    private static TableColumn<ObservableAircraftState, String> model;

    // table column that displays type
    private static TableColumn<ObservableAircraftState, String> type;

    // table column that displays description
    private static TableColumn<ObservableAircraftState, String> description;

    // table column that displays longitude
    private static TableColumn<ObservableAircraftState, String> longitude;

    // table column that displays latitude
    private static TableColumn<ObservableAircraftState, String> latitude;

    // table column that displays altitude
    private static TableColumn<ObservableAircraftState, String> altitude;

    // table column that displays velocity
    private static TableColumn<ObservableAircraftState, String> velocity;

    // number formatter for altitude and velocity
    private static NumberFormat format;

    // function that handles selected state changes
    private Consumer<ObservableAircraftState> selectedState;
    // TODO: 5/7/2023 Need to add Constants for column sizes

    // Constants for column sizes, and the formatting of the decimal numbers shown
    private static final int NUMBER_COLUMN_SIZE = 85;
    private static final int MAX_INTEGER_DECIMAL = 4;
    private static final int MIN_INTEGER_DECIMAL = 0;

    /**
     * This constructor is responsible for creating an instance of the AircraftTableController class
     * which controls the table of aircraft states displayed in the user interface.
     * @param knownStates contains a set of all the known aircraft states that will be displayed in the table.
     * @param currentSelectedState is used to keep track of the currently selected state in the table.
     */
    public AircraftTableController(ObservableSet<ObservableAircraftState> knownStates,
                                   ObjectProperty<ObservableAircraftState> currentSelectedState) {

        // Initialize variables
        this.knownStates = knownStates;
        this.currentSelectedState = currentSelectedState;
        this.format = NumberFormat.getInstance();
        format.setMinimumFractionDigits(MIN_INTEGER_DECIMAL);
        format.setMaximumFractionDigits(MAX_INTEGER_DECIMAL);


        // Add listener to knownStates for adding or removing items from the table
        knownStates.addListener((SetChangeListener<ObservableAircraftState>) change -> {
            if (change.wasAdded()) {
                scenegraph.getItems().addAll(change.getElementAdded());
            } else {
                scenegraph.getItems().remove(change.getElementRemoved());
            }
            scenegraph.sort();
        });

        // Add listener to table for handling double-click events
        scenegraph.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && MouseButton.PRIMARY == event.getButton()) {
                if(selectedState != null)
                    selectedState.accept(scenegraph.getSelectionModel().getSelectedItem());
            }
        });

        // Add listener to currentSelectedState for scrolling to and selecting the corresponding row in the table
        currentSelectedState.addListener(e -> {
            if(knownStates.contains(currentSelectedState.get())){
                scenegraph.scrollTo(currentSelectedState.getValue());
                scenegraph.getSelectionModel().select(currentSelectedState.getValue());
            }
        });
    }

    /**
     * Returns a JavaFX Node that displays the aircraft table with columns for each piece of aircraft data,
     * including the aircraft's ICAO address, call sign, registration, model, type, description, longitude, latitude,
     * altitude, and velocity. Each column is configured with a set preferred width, and the table is set to resize
     * all columns according to available space. The table includes a menu button that can be used to show/hide columns.
     * The data displayed in the table is obtained from an ObservableSet<ObservableAircraftState> object passed
     * in when constructing an instance of AircraftTableController.
     *
     * The data in each column is populated using a cell value factory, which maps the data in an
     * ObservableAircraftState object to the appropriate value for the corresponding column. For example,
     * the ICAO address column is populated using the ICAO address property of the aircraft state object.
     *
     * The longitude, latitude, altitude, and velocity columns display numeric data and are created using a private
     * helper method that creates a number column with a set preferred width and cell value factory that formats the
     * numeric data according to the appropriate units and precision.
     *
     * @return a JavaFX Node representing the aircraft table
     */
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

    //TODO still needs to be done
    public void setOnDoubleClick (Consumer<ObservableAircraftState> selectedState) {
        this.selectedState = selectedState;
    }

    /**
     * The createNumberColumn method creates a new TableColumn for numerical data and adds it to the scenegraph.
     * It takes as input a Set of existing TableColumn objects, a TableColumn object, and the name of the column as a String.
     * The method adds the new TableColumn to the scenegraph, sets its width to NUMBER_COLUMN_SIZE, and adds it to the Set of
     * existing TableColumn objects. It also sets the TableColumn's comparator to sort based on numerical values,
     * and sets the alignment style to right-justified. The method returns the new TableColumn object.
     * @param numberColumns a Set of existing TableColumn objects.
     * @param column a TableColumn object.
     * @param columnName the name of the column as a String.
     * @return the new TableColumn object.
     */
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
