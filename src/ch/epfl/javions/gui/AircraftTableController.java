package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftDescription;
import ch.epfl.javions.aircraft.AircraftRegistration;
import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import ch.epfl.javions.aircraft.IcaoAddress;
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
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;


/**
 * The AircraftTableController class is the controller for the aircraft table view in the JavaFX user interface.
 * It provides functionalities to display the aircraft data in a table view.
 *
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public final class AircraftTableController {

    private final ObservableSet<ObservableAircraftState> knownStates;

    // currently selected aircraft state
    private static ObjectProperty<ObservableAircraftState> currentSelectedState;

    // table view that displays aircraft state information
    private static TableView<ObservableAircraftState> scenegraph = new TableView<>();

    // number formatter for altitude and velocity
    private static NumberFormat format;

    // lambda function redefining the accept method of the anonymous Consumer class that handles selected state changes.
    private Consumer<ObservableAircraftState> selectedState = oas -> currentSelectedState.set(oas);

    // Constants for column sizes, and the formatting of the decimal numbers shown
    private static final int NUMBER_COLUMN_SIZE = 85;
    private static final int MAX_INTEGER_DECIMAL = 4;
    private static final int MIN_INTEGER_DECIMAL = 0;
    private static final int WIDTH_ICAOADRESS = 60;
    private static final int WIDTH_CALLSIGN_AND_DESCRIPTION = 70;
    private static final int WIDTH_REGISTRATION = 90;
    private static final int WIDTH_MODEL = 230;
    private static final int WIDTH_TYPE = 50;
    private static final String NUMERIC_COLUMN_STYLING = "-fx-alignment: baseline-right";

    /**
     * This constructor is responsible for creating an instance of the AircraftTableController class
     * which controls the table of aircraft states displayed in the user interface. It also creates
     * the actual TableView and adds the columns using a helper method.
     *
     * It also calls the addListeners and addEvent methods.
     *
     * @param knownStates          contains a set of all the known aircraft states that will be displayed in the table.
     * @param currentSelectedState is used to keep track of the currently selected state in the table.
     */
    public AircraftTableController(ObservableSet<ObservableAircraftState> knownStates,
                                   ObjectProperty<ObservableAircraftState> currentSelectedState) {
        // Initialize variables
        this.knownStates = Objects.requireNonNull(knownStates);
        this.currentSelectedState = Objects.requireNonNull(currentSelectedState);
        this.format = NumberFormat.getInstance();
        format.setMinimumFractionDigits(MIN_INTEGER_DECIMAL);
        format.setMaximumFractionDigits(MAX_INTEGER_DECIMAL);
        scenegraph.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        scenegraph.setTableMenuButtonVisible(true);

        addListeners();
        createColumns();
    }

    /**
     * Adds the appropriate Listeners to enable us to :
     *
     *  - Add a row with the data of from instance of an ObservableAircraftState whenever one gets added
     *  to the Set of ObservableAircraftStates of which we know the position.
     *  - Select an aircraft on the map and have the corresponding selected state be selected in the table
     *
     *  - Call the accept method from the selectedState Consumer, which then enables us to center the
     *  map around the state on which we have clicked.
     */
    private void addListeners(){
        // Adds listener to knownStates for adding or removing items from the table
        knownStates.addListener((SetChangeListener<ObservableAircraftState>) change -> {
            if (change.wasAdded()) {
                scenegraph.getItems().addAll(change.getElementAdded());
            } else {
                scenegraph.getItems().remove(change.getElementRemoved());
            }
            scenegraph.sort();
        });

        // Adds listener to currentSelectedState for scrolling to and selecting the corresponding row in the table.
        currentSelectedState.addListener(e -> {
            if (knownStates.contains(currentSelectedState.get()) &&
                    scenegraph.getSelectionModel().getSelectedItem() != currentSelectedState.get()) {
                scenegraph.scrollTo(currentSelectedState.getValue());
                scenegraph.getSelectionModel().select(currentSelectedState.getValue());
            }
        });

        // Adds listener to table for handling double-click events.
        scenegraph.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && MouseButton.PRIMARY == event.getButton()) {
                if (selectedState != null && scenegraph.getSelectionModel().getSelectedItem() != null) {
                    selectedState.accept(scenegraph.getSelectionModel().getSelectedItem());
                }
            } else {
                currentSelectedState.set(scenegraph.getSelectionModel().getSelectedItem());
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
     * @return a JavaFX Node representing the aircraft table
     */
    public Node pane() {
        return scenegraph;
    }

    /**
     * Creates and adds columns to the table.
     *
     * Each column represents a specific property of the aircraft state.
     *
     * The data in each column is populated using a helper method, which creates the columns and
     * maps the data of an ObservableAircraftState instance to the appropriate value for the corresponding
     * column. For example, the ICAO address column is populated by creating an ObjectWrapper
     * containing the IcaoAddress of each aircraft state instance.
     */
    private void createColumns() {
        // IcaoAddress
        createColumn("IcaoAddress", false, WIDTH_ICAOADRESS, f -> {
            ReadOnlyObjectWrapper<IcaoAddress> icaoAddressWrapper =
                    new ReadOnlyObjectWrapper<>(f.getValue().icaoAddress());
            return icaoAddressWrapper.map(IcaoAddress::string);
        });

        // CallSign
        createColumn("CallSign", false, WIDTH_CALLSIGN_AND_DESCRIPTION, f ->
                f.getValue().callSignProperty().map(CallSign::string));

        // Registration
        createColumn("Registration", false, WIDTH_REGISTRATION, f -> {
            if (f.getValue().getData() != null) {
                ReadOnlyObjectWrapper<AircraftRegistration> registrationWrapper =
                        new ReadOnlyObjectWrapper<>(f.getValue().getData().registration());
                return registrationWrapper.map(AircraftRegistration::string);
            } else return null;
        });

        // Model
        createColumn("Model", false, WIDTH_MODEL, f -> {
            if (f.getValue().getData() != null) {
                ReadOnlyObjectWrapper<String> modelWrapper =
                        new ReadOnlyObjectWrapper<>(f.getValue().getData().model());
                return modelWrapper.map(String::toString);
            } else return null;
        });

        //Type
        createColumn("Type", false, WIDTH_TYPE, f -> {
            if (f.getValue().getData() != null) {
                ReadOnlyObjectWrapper<AircraftTypeDesignator> typeWrapper =
                        new ReadOnlyObjectWrapper<>(f.getValue().getData().typeDesignator());
                return typeWrapper.map(AircraftTypeDesignator::string);
            } else return null;
        });

        // Description
        createColumn("Description",false, WIDTH_CALLSIGN_AND_DESCRIPTION , f -> {
            if (f.getValue().getData() != null) {
                ReadOnlyObjectWrapper<AircraftDescription> descriptionWrapper =
                        new ReadOnlyObjectWrapper<>(f.getValue().getData().description());
                return descriptionWrapper.map(AircraftDescription::string);
            } else return null;
        });

        // Longitude
        createColumn("Longitude (°)", true, NUMBER_COLUMN_SIZE, f ->
                f.getValue().positionProperty().map(m ->
                        format.format(Units.convertTo(m.longitude(), Units.Angle.DEGREE))));

        // Latitude
        createColumn("Latitude (°)", true, NUMBER_COLUMN_SIZE, f ->
                f.getValue().positionProperty().map(m ->
                        format.format(Units.convertTo(m.latitude(), Units.Angle.DEGREE))));

        // Altitude
        createColumn("Altitude (m)", true, NUMBER_COLUMN_SIZE, f ->
                f.getValue().altitudeProperty().map(m ->
                        format.format(m.doubleValue())));

        // Velocity
        createColumn("Velocity (km/h)", true, NUMBER_COLUMN_SIZE, f ->
                f.getValue().velocityProperty().map(m ->
                        format.format(Units.convertTo(m.doubleValue(), Units.Speed.KILOMETER_PER_HOUR))));

    }


    /**
     * @param selectedState the Consumer to be set as the double-clicked state
     */
    public void setOnDoubleClick(Consumer<ObservableAircraftState> selectedState) {
        this.selectedState = selectedState;
    }


    /**
     * Creates a TableColumn with the specified properties and adds it to the scenegraph.
     *
     * @param columnName The name of the column.
     *
     * @param isANumberColumn A boolean indicating whether the column should support numeric comparisons.
     *
     * @param columnWidth The preferred width of the column.
     *
     * @param cellFunction A function that provides the cell value for each row in the column.
     */
    public static void createColumn(String columnName, boolean isANumberColumn, int columnWidth,
            Function<TableColumn.CellDataFeatures
                    <ObservableAircraftState, String>, ObservableValue<String>> cellFunction) {
        TableColumn<ObservableAircraftState, String> column = new TableColumn<>(columnName);
        if (isANumberColumn) {
            column.setComparator((s1, s2) -> {
                try {
                    return (s1.isEmpty() || s2.isEmpty())
                            ? s1.compareTo(s2)
                            : Double.compare(format.parse(s1).doubleValue(), format.parse(s2).doubleValue());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            });
            column.setStyle(NUMERIC_COLUMN_STYLING);
        }
        column.setPrefWidth(columnWidth);
        column.setCellValueFactory(cellFunction::apply);
        scenegraph.getColumns().add(column);
    }
}
