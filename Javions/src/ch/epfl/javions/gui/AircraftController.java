package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;
import ch.epfl.javions.WebMercator;

import ch.epfl.javions.aircraft.*;
import javafx.beans.Observable;
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


public final class AircraftController {
    private MapParameters parameters;
    private ObjectProperty<ObservableAircraftState> currentSelectedState;
    private Pane aircraftPane;
    private IntegerProperty currentZoom;
    private static final int ALTITUDE_CEILING = 12000;

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

        currentZoom = (IntegerProperty) parameters.zoomProperty();
    }

    public Pane pane() {
        return aircraftPane;
    }

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
     * null for now because I just want to be able to see the labels and the icons on the aircraft before doing anything else
     *
     * @param oas
     * @return
     */
    private Group aircraftTrajectory(ObservableAircraftState oas) {
        Group trajectory = new Group();
        trajectory.getStyleClass().add("trajectory");
        trajectory.setVisible(false);

        oas.trajectoryProperty().addListener((ListChangeListener<ObservableAircraftState.AirbornePos>) c -> {
            buildTrajectory(trajectory, oas);
        });

        parameters.zoomProperty().addListener(e -> {
            buildTrajectory(trajectory, oas);
        });

        currentSelectedState.addListener(e -> trajectory.setVisible(currentSelectedState.getValue().equals(oas)));
        return trajectory;
    }

    private void buildTrajectory(Group trajectory, ObservableAircraftState oas) {
        if(trajectory.isVisible()){
            trajectory.getChildren().clear();
            ObservableList<ObservableAircraftState.AirbornePos> oasTrajectory = oas.trajectoryProperty();
            for (int i = 1; i < oasTrajectory.size(); i++) {
                GeoPos start = oasTrajectory.get(i-1).position();
                GeoPos end = oasTrajectory.get(i).position();
                Line line = new Line(WebMercator.x(currentZoom.get(), start.longitude()),
                        WebMercator.y(currentZoom.get(), start.latitude()),
                        WebMercator.x(currentZoom.get(), end.longitude()),
                        WebMercator.y(currentZoom.get(), end.latitude()));
                line.layoutXProperty().bind(parameters.minXProperty().negate());
                line.layoutYProperty().bind(parameters.minYProperty().negate());
                line.setStroke(altitudeToPlasmaColourIndex(oasTrajectory.get(i).altitude()));
                trajectory.getChildren().add(line);
            }
        }
    }

    private void setGroupBindings(ObservableAircraftState oas, Group iconAndLabel) {
        oas.positionProperty().addListener(e -> aircraftLabelAndIconPositioning(oas, iconAndLabel));
        parameters.zoomProperty().addListener(e -> aircraftLabelAndIconPositioning(oas, iconAndLabel));
    }

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

    public Group aircraftLabelInitialisation(ObservableAircraftState oas) {
        Rectangle rectangle = new Rectangle();
        Text text = new Text();
        if (oas.getData() != null) {
            text.textProperty().bind(
                    Bindings.createStringBinding(() -> String.format("%s \n%s km/h %s m"
                                    , oas.getData().registration().string()
                                    , (int) Math.rint(Units.convertTo(oas.getVelocity(), Units.Speed.KILOMETER_PER_HOUR))
                                    , (int) Math.rint(oas.getAltitude()))
                            , oas.velocityProperty(), oas.altitudeProperty()));
        }
        rectangle.heightProperty().bind(
                text.layoutBoundsProperty().map(b -> b.getHeight() + 4));
        rectangle.widthProperty().bind(
                text.layoutBoundsProperty().map(b -> b.getWidth() + 4));

        Group label = new Group(rectangle, text);
        label.visibleProperty().bind(
                Bindings.createBooleanBinding(() -> selectedStateListener(oas)));
        label.getStyleClass();
        label.getStyleClass().add("label");
        showLabelListener(label, oas);

        return label;
    }

    private void showLabelListener(Group label, ObservableAircraftState oas) {
        currentZoom.addListener(e ->
                label.visibleProperty().bind(Bindings.createBooleanBinding(() -> selectedStateListener(oas))));
        currentSelectedState.addListener(e ->
                label.visibleProperty().bind(Bindings.createBooleanBinding(() -> selectedStateListener(oas))));
    }

    private Group aircraftIconInitialisation(ObservableAircraftState oas) {
        SVGPath aircraftIcon = new SVGPath();
        aircraftIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                currentSelectedState.set(oas);
            }
        });

        aircraftIcon.getStyleClass().add("aircraft");
        AircraftData data = oas.getData();
        ObservableValue<AircraftIcon> icon = (data == null)
                ? oas.categoryProperty().map(f -> AircraftIcon.iconFor(new AircraftTypeDesignator(""),
                new AircraftDescription(""), 0, WakeTurbulenceCategory.UNKNOWN))
                : oas.categoryProperty().map(f -> AircraftIcon.iconFor(data.typeDesignator(), data.description(),
                oas.getCategory(), data.wakeTurbulenceCategory())
        );

        //initialising the color of the icon depending on the altitude
        iconColorSetter(aircraftIcon, oas);

        // Binding the altitude to a paint property and then binding the icon color to that paint property
        oas.altitudeProperty().addListener(e -> iconColorSetter(aircraftIcon, oas));

        aircraftIcon.setStroke(Color.BLACK);

        aircraftIcon.contentProperty().bind(Bindings.createStringBinding(icon.getValue()::svgPath, icon));
        rotateIcon(icon, oas, aircraftIcon);
        return new Group(aircraftIcon);
    }

    private void iconColorSetter(SVGPath aircraftIcon, ObservableAircraftState oas) {
        ObjectProperty<Paint> iconColorUpdated =
                new SimpleObjectProperty<>(altitudeToPlasmaColourIndex(oas.getAltitude()));
        aircraftIcon.fillProperty().bind(iconColorUpdated);
    }

    private void rotateIcon(ObservableValue<AircraftIcon> icon, ObservableAircraftState oas, SVGPath aircraftIcon) {
        oas.trackOrHeadingProperty().addListener(e -> {
            if (icon.getValue().canRotate())
                aircraftIcon.rotateProperty().bind(Bindings.createDoubleBinding(() ->
                        Units.convertTo(oas.trackOrHeadingProperty().get(), Units.Angle.DEGREE)));
        });
    }

    private Color altitudeToPlasmaColourIndex(double altitude) {
        return ColorRamp.PLASMA.at(Math.cbrt(altitude / ALTITUDE_CEILING));
    }

    private boolean selectedStateListener(ObservableAircraftState oas) {
        return (currentSelectedState.getValue() == null)
                ? currentZoom.get() >= 11
                : currentZoom.get() >= 11 || currentSelectedState.getValue().equals(oas);
    }
}