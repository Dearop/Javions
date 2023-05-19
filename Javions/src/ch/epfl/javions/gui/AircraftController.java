package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;
import ch.epfl.javions.WebMercator;

import ch.epfl.javions.aircraft.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

import static javafx.scene.paint.CycleMethod.NO_CYCLE;

/**
 * The AircraftController class is the main controller for the aircraft. The class uses JavaFX to show all the aircraft
 * as well as their trajectory and color. It has a number of private variables including
 * parameters of type MapParameters and currentSelectedState,
 * which keeps track of the currently selected aircraft. aircraftPane is the pane on which the aircraft are displayed.
 * CurrentZoom, which is the current zoom level.
 *
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public final class AircraftController {
    // stores the parameters used to render the map.
    private MapParameters parameters;

    // This property is used to remember the selected aircraft
    private ObjectProperty<ObservableAircraftState> currentSelectedState;

    // a Pane that contains all the aircraft icons and labels. This is the pane that is displayed on the map.
    private Pane aircraftPane;

    // represents the current zoom level of the map.
    private ReadOnlyIntegerProperty currentZoom;

    // a constant integer value that represents the maximum altitude of the color scheme for the aircraft.
    private static final int ALTITUDE_CEILING = 12000;

    private static final int ZOOM_HURDLE = 11;

    /**
     * Constructor for AircraftController class that initializes the private variables parameters and currentSelectedState
     * then it creates a new Pane that uses the aircraft.css for styling purposes. The constructor also adds a listener
     * on the ObservableSet of the ObservableAircraftState called knownStates. The listener adds and removes the aircraft
     * from the pane.
     *
     * @param parameters           - a map of parameters for aircraft control
     * @param knownStates          - an ObservableSet of ObservableAircraftState representing the known aircraft states
     * @param currentSelectedState - an ObjectProperty representing the current selected aircraft state
     */
    public AircraftController(MapParameters parameters, ObservableSet<ObservableAircraftState> knownStates,
                              ObjectProperty<ObservableAircraftState> currentSelectedState) {
        this.parameters = parameters;
        this.currentSelectedState = currentSelectedState;

        this.aircraftPane = new Pane();
        this.aircraftPane.setPickOnBounds(false);
        this.aircraftPane.getStylesheets().add("aircraft.css");
        this.currentZoom = new SimpleIntegerProperty();

        knownStates.addListener((SetChangeListener<ObservableAircraftState>) c -> {
            if (c.wasAdded()) {
                Group addedAircraft = individualAircraftGroup(c.getElementAdded());
                addedAircraft.viewOrderProperty().bind(c.getElementAdded().altitudeProperty().negate());
                aircraftPane.getChildren().add(addedAircraft);
            } else if (c.wasRemoved()) {
                aircraftPane.getChildren().removeIf(p -> p.getId().equals(c.getElementRemoved().icaoAddress().string()));
            }
        });
        currentZoom = parameters.zoomProperty();
    }

    /**
     * The pane() method returns the aircraft pane.
     *
     * @return the aircraft pane.
     */
    public Pane pane() {
        return aircraftPane;
    }

    /**
     * Creates a group containing an aircraft icon and label, and binds their position to the aircraft state.
     * Also includes a trajectory group which is created and updated in the method buildTrajectory.
     *
     * @param oas The ObservableAircraftState object representing the aircraft along with its state
     *            for which we are creating the group.
     * @return A Group object representing the aircraft icon, label, and trajectory.
     */
    private Group individualAircraftGroup(ObservableAircraftState oas) {
        Group iconAndLabel = new Group(aircraftIconInitialisation(oas), aircraftLabelInitialisation(oas));

        Group trajectory = new Group(aircraftTrajectory(oas));
        aircraftLabelAndIconPositioning(oas, iconAndLabel);

        Group aircraftGroup = new Group(trajectory, iconAndLabel);
        aircraftGroup.setId(oas.icaoAddress().string());
        setGroupBindings(oas, iconAndLabel);

        return aircraftGroup;
    }

    /**
     * Returns a Group representing the trajectory of a given ObservableAircraftState object.
     * The trajectory is built based on the airborne positions of the given ObservableAircraftState object.
     * The visibility of the trajectory is tied to the currentSelectedState ObjectProperty, meaning that the
     * trajectory will only be visible if the aircraft seen on the map gets clicked.
     * The zoom level of the map is also taken into account when building the trajectory.
     *
     * @param oas The ObservableAircraftState object whose trajectory is to be represented
     * @return A Group representing the trajectory of the given ObservableAircraftState object
     */
    private Group aircraftTrajectory(ObservableAircraftState oas) {
        Group trajectory = new Group();
        trajectory.getStyleClass().add("trajectory");
        trajectory.setVisible(false);

        oas.trajectoryProperty().addListener((ListChangeListener<ObservableAircraftState.AirbornePos>) c -> {
            if (currentSelectedState.get() != null && currentSelectedState.getValue().equals(oas))
                buildTrajectory(trajectory, oas);
        });

        parameters.zoomProperty().addListener(e -> {
            if (currentSelectedState.get() != null &&
                    currentSelectedState.getValue().equals(oas))
                buildTrajectory(trajectory, oas);
        });

        currentSelectedState.addListener(e -> {
            trajectory.setVisible(currentSelectedState.getValue().equals(oas));
            buildTrajectory(trajectory, oas);
        });

        trajectory.visibleProperty().addListener(e -> trajectory.getChildren().clear());
        trajectory.layoutXProperty().bind(parameters.minXProperty().negate());
        trajectory.layoutYProperty().bind(parameters.minYProperty().negate());

        return trajectory;
    }

    /**
     * Builds the trajectory of a given ObservableAircraftState object based on its airborne positions.
     * The visibility of the trajectory is checked before rebuilding the trajectory.
     * The trajectory is built by creating Line objects between each pair of adjacent airborne positions.
     * The altitude of each airborne position is used to determine the colour of the Line object,
     * with a gradient being used if the altitude changes between two positions.
     *
     * @param trajectory The Group object representing the trajectory of the given ObservableAircraftState object
     * @param oas        The ObservableAircraftState object whose trajectory is to be built
     */
    private void buildTrajectory(Group trajectory, ObservableAircraftState oas) {
        if (trajectory.isVisible()) {
            trajectory.getChildren().clear();
            ObservableList<ObservableAircraftState.AirbornePos> oasTrajectory = oas.trajectoryProperty();

            for (int i = 1; i < oasTrajectory.size(); i++) {
                GeoPos start = oasTrajectory.get(i - 1).position();
                GeoPos end = oasTrajectory.get(i).position();

                double startX = WebMercator.x(currentZoom.get(), start.longitude());
                double startY = WebMercator.y(currentZoom.get(), start.latitude());
                double endX = WebMercator.x(currentZoom.get(), end.longitude());
                double endY = WebMercator.y(currentZoom.get(), end.latitude());

                Line line = new Line(startX, startY, endX, endY);

                if (oasTrajectory.get(i - 1).altitude() == oasTrajectory.get(i).altitude())
                    line.setStroke(altitudeToPlasmaColourIndex(oasTrajectory.get(i).altitude()));
                else {
                    Stop s1 = new Stop(0, altitudeToPlasmaColourIndex(oasTrajectory.get(i - 1).altitude()));
                    Stop s2 = new Stop(1, altitudeToPlasmaColourIndex(oasTrajectory.get(i).altitude()));
                    line.setStroke(new LinearGradient(startX, startY, endX, endY, true, NO_CYCLE, s1, s2));
                }
                trajectory.getChildren().add(line);
            }
        }
    }


    /**
     * Sets the bindings for the position of the icon and label based on the position
     * of the aircraft and the current zoom level.
     *
     * @param oas          the observable aircraft state for which to set the bindings
     * @param iconAndLabel the group containing the aircraft icon and label
     */
    private void setGroupBindings(ObservableAircraftState oas, Group iconAndLabel) {

        // Bind the position of the icon and label to the position of the aircraft
        oas.positionProperty().addListener(e -> aircraftLabelAndIconPositioning(oas, iconAndLabel));

        // Bind the position of the icon and label to the current zoom level
        parameters.zoomProperty().addListener(e -> aircraftLabelAndIconPositioning(oas, iconAndLabel));

    }

    /**
     * Positions the icon and label of an aircraft on the map based on its current position and zoom level.
     *
     * @param oas          The ObservableAircraftState object representing the aircraft to be positioned.
     * @param iconAndLabel The Group containing the icon and label of the aircraft.
     */
    private void aircraftLabelAndIconPositioning(ObservableAircraftState oas, Group iconAndLabel) {

        //binding the icon and label to the position of the aircraft
        ReadOnlyObjectProperty<GeoPos> position = oas.positionProperty();

        //bind the position of the aircraft to the position we are using
        double positionX = WebMercator.x(currentZoom.get(), position.get().longitude());
        double positionY = WebMercator.y(currentZoom.get(), position.get().latitude());
        iconAndLabel.layoutXProperty().bind(
                Bindings.createDoubleBinding(() ->
                        positionX - parameters.getMinX(), position, currentZoom, parameters.minXProperty()));
        iconAndLabel.layoutYProperty().bind(
                Bindings.createDoubleBinding(() ->
                        positionY - parameters.getMinY(), position, currentZoom, parameters.minYProperty()));
    }

    /**
     * Creates a Group object containing a Text object and a Rectangle object, representing the label for an aircraft.
     * The Text object displays the aircraft's registration, velocity and altitude information.
     * The Rectangle object serves as a background for the Text object.
     * The Group object is made visible only if the corresponding aircraft's state label is selected.
     *
     * @param oas An ObservableAircraftState object representing the aircraft for which the label is created.
     * @return A Group object representing the label for the given aircraft.
     */
    public Group aircraftLabelInitialisation(ObservableAircraftState oas) {
        Rectangle rectangle = new Rectangle();
        Text text = new Text();

        // bind the Text object's text property to the aircraft's registration, velocity and altitude
        text.textProperty().bind(
                Bindings.createStringBinding(() -> String.format("%s \n%s km/h %s m"
                                , (oas.getData() != null) ? oas.getData().registration().string() : "Unknown"
                                , (Double.isNaN(oas.getVelocity())) ? "?" : (int) Math.rint(Units.convertTo(oas.getVelocity(), Units.Speed.KILOMETER_PER_HOUR))
                                , (Double.isNaN(oas.getAltitude())) ? "?" : (int) Math.rint(oas.getAltitude()))
                        , oas.velocityProperty(), oas.altitudeProperty()));

        // bind the Rectangle object's height and width properties to the Text object's layoutBounds property
        rectangle.heightProperty().bind(
                text.layoutBoundsProperty().map(b -> b.getHeight() + 4));
        rectangle.widthProperty().bind(
                text.layoutBoundsProperty().map(b -> b.getWidth() + 4));

        // create the Group object containing the Rectangle and Text objects
        Group label = new Group(rectangle, text);

        // bind the visibility property of the Group object to whether the corresponding aircraft's state label is selected
        label.visibleProperty().bind(
                Bindings.createBooleanBinding(() -> selectedStateLabelListener(oas)));
        label.getStyleClass().add("label");
        showLabelListener(label, oas);
        return label;
    }

    /**
     * Adds listeners to the current zoom level and currently selected state in order to show or hide the label.
     *
     * @param label The label to show or hide.
     * @param oas   The ObservableAircraftState to associate the label with.
     */
    private void showLabelListener(Group label, ObservableAircraftState oas) {

        // Add listener to current zoom level
        currentZoom.addListener(e ->
                label.visibleProperty().bind(Bindings.createBooleanBinding(() ->
                        selectedStateLabelListener(oas))));

        // Add listener to currently selected state
        currentSelectedState.addListener(e ->
                label.visibleProperty().bind(Bindings.createBooleanBinding(() ->
                        selectedStateLabelListener(oas))));
    }

    /**
     * Initializes and returns the aircraft icon for the given ObservableAircraftState.
     * The icon is an SVGPath that represents the aircraft and includes bindings to update its color
     * based on the altitude of the aircraft, and to rotate it based on the aircraft's track.
     *
     * @param oas the ObservableAircraftState for which the icon is being created
     * @return a Group containing the aircraft icon
     */
    private Group aircraftIconInitialisation(ObservableAircraftState oas) {

        // create the SVGPath for the aircraft icon
        SVGPath aircraftIcon = new SVGPath();
        aircraftIcon.getStyleClass().add("aircraft");

        // set up a click listener to select the aircraft when the icon is clicked
        aircraftIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                currentSelectedState.set(oas);
            }
        });

        // get the icon for the aircraft based on its type and category
        AircraftData data = oas.getData();
        ObservableValue<AircraftIcon> icon = (data == null)
                ? oas.categoryProperty().map(f -> AircraftIcon.iconFor(new AircraftTypeDesignator(""),
                new AircraftDescription(""), 0, WakeTurbulenceCategory.UNKNOWN))
                : oas.categoryProperty().map(f -> AircraftIcon.iconFor(data.typeDesignator(), data.description(),
                oas.getCategory(), data.wakeTurbulenceCategory())
        );

        // set the initial color of the icon based on the altitude of the aircraft
        iconColorSetter(aircraftIcon, oas);

        // bind the color of the icon to the altitude of the aircraft, so it updates automatically
        oas.altitudeProperty().addListener(e -> iconColorSetter(aircraftIcon, oas));

        // set the stroke color of the icon to black
        aircraftIcon.setStroke(Color.BLACK);

        // bind the content of the SVGPath to the SVG path string for the icon, so it updates
        aircraftIcon.contentProperty().bind(Bindings.createStringBinding(icon.getValue()::svgPath, icon));

        // set up a rotation binding to rotate the icon based on the aircraft's track
        rotateIcon(icon, oas, aircraftIcon);

        return new Group(aircraftIcon);
    }

    /**
     * This method sets the color of the aircraft icon based on the altitude of the aircraft.
     * It creates an ObjectProperty of type Paint, which represents the color of the icon,
     * and binds it to the value returned by the altitudeToPlasmaColourIndex() method, which
     * calculates the color based on the altitude. Finally, it binds the fillProperty of the
     * aircraftIcon (an SVGPath object) to the ObjectProperty to update the color of the icon.
     *
     * @param aircraftIcon the SVGPath object representing the aircraft icon
     * @param oas          the ObservableAircraftState object representing the aircraft state
     */
    private void iconColorSetter(SVGPath aircraftIcon, ObservableAircraftState oas) {
        ObjectProperty<Paint> iconColorUpdated =
                new SimpleObjectProperty<>(altitudeToPlasmaColourIndex(oas.getAltitude()));
        aircraftIcon.fillProperty().bind(iconColorUpdated);
    }


    /**
     * Rotates the given aircraft icon according to the track or heading of the aircraft.
     *
     * @param icon         the icon of the aircraft
     * @param oas          the observable state of the aircraft
     * @param aircraftIcon the SVGPath object representing the aircraft icon
     */
    private void rotateIcon(ObservableValue<AircraftIcon> icon, ObservableAircraftState oas, SVGPath aircraftIcon) {
        oas.trackOrHeadingProperty().addListener(e -> {
            if (icon.getValue().canRotate())
                aircraftIcon.rotateProperty().bind(Bindings.createDoubleBinding(() ->
                        Units.convertTo(oas.trackOrHeadingProperty().get(), Units.Angle.DEGREE)));
        });
    }

    /**
     * Maps an altitude to a color from the PLASMA color ramp, using the cube root of the altitude as the index.
     *
     * @param altitude the altitude to map to a color
     * @return a color from the PLASMA color ramp
     */
    private Color altitudeToPlasmaColourIndex(double altitude) {
        return ColorRamp.PLASMA.at(Math.cbrt(altitude / ALTITUDE_CEILING));
    }

    /**
     * Returns true if the given aircraft state label should be visible based on the current zoom level and selected state.
     * If no aircraft state is currently selected, the label is visible only when the zoom level is greater than or equal to 11.
     * If an aircraft state is selected, the label is visible when the zoom level is greater than or equal to 11 or the given
     * aircraft state is equal to the selected state.
     *
     * @param oas the observable aircraft state corresponding to the label
     * @return true if the label should be visible, false otherwise
     */
    private boolean selectedStateLabelListener(ObservableAircraftState oas) {
        return (currentSelectedState.getValue() == null)
                ? currentZoom.get() >= ZOOM_HURDLE
                : currentZoom.get() >= ZOOM_HURDLE || currentSelectedState.getValue().equals(oas);
    }
}