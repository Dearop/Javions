package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;
import ch.epfl.javions.WebMercator;

import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.AircraftDescription;
import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import ch.epfl.javions.aircraft.WakeTurbulenceCategory;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;

public final class AircraftController {
    private MapParameters parameters;
    private ObjectProperty<ObservableAircraftState> currentSelectedState;
    private Pane aircraftPane;
    private IntegerProperty currentZoom;
    private boolean airCraftClicked;
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
        parameters.zoomProperty().addListener(e -> {
            currentZoom.set(parameters.getZoom());
        });
    }

    public Pane pane() {
        return aircraftPane;
    }

    private Group individualAircraftGroup(ObservableAircraftState oas) {
        //Group label = new Group(aircraftLabelInitialisation(oas));
        Group icon = new Group(aircraftIconInitialisation(oas));
        //label.visibleProperty().bind(isShowable(icon));
        //Group iconAndLabel = new Group(icon);
        Group aircraftGroup = new Group(icon);
        aircraftGroup.setId(oas.icaoAddress().string());
        aircraftLabelAndIconPositioning(oas, icon);
        setGroupBindings(oas, icon);
        return aircraftGroup;
    }

    /**
     * null for now because I just want to be able to see the labels and the icons on the aircraft before doing anything else
     *
     * @param oas
     * @return
     */
    private Group aircraftTrajectory(ObservableAircraftState oas) {
        return null;
    }

    private void setGroupBindings(ObservableAircraftState oas, Group iconAndLabel){
        oas.positionProperty().addListener(e -> aircraftLabelAndIconPositioning(oas, iconAndLabel));
        parameters.zoomProperty().addListener( e -> aircraftLabelAndIconPositioning(oas,iconAndLabel));
    }

    private void aircraftLabelAndIconPositioning(ObservableAircraftState oas, Group iconAndLabel) {
        //binding the icon and label to the position of the aircraft
        ReadOnlyObjectProperty<GeoPos> position = oas.positionProperty();
        if(iconAndLabel.getParent().getId().equals(oas.icaoAddress().string()))
            System.out.println(position);
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

    /*public Group aircraftLabelInitialisation(ObservableAircraftState oas) {
        Rectangle rectangle = new Rectangle();
        Text text = new Text();
        text.textProperty().bind(
                Bindings.format("%f/n", new SimpleStringProperty(oas.icaoAddress().string())));
        text.textProperty().bind(
                Bindings.createStringBinding(() ->
                                String.format("%f m/s u/2002 %f m�tres", oas.getVelocity(), oas.getAltitude())
                        , oas.velocityProperty(), oas.altitudeProperty()));
        rectangle.heightProperty().bind(
                text.layoutBoundsProperty().map(b -> b.getHeight()));
        rectangle.widthProperty().bind(
                text.layoutBoundsProperty().map(b -> b.getWidth() + 4));
        Group label = new Group(rectangle, text);
        label.getStyleClass();
        return label;
    }*/

    private Rectangle aircraftIconInitialisation(ObservableAircraftState oas) {
        SVGPath aircraftIcon = new SVGPath();
        aircraftIcon.getStyleClass().add("aircraft");
        AircraftData data = oas.getData();
        ObservableValue<AircraftIcon> icon = (data == null)
                ? oas.categoryProperty().map(f -> AircraftIcon.iconFor(new AircraftTypeDesignator("G3"),
                new AircraftDescription("L2J"), 0, WakeTurbulenceCategory.UNKNOWN))
                : oas.categoryProperty().map(f -> AircraftIcon.iconFor(data.typeDesignator(), data.description(),
                oas.getCategory(), data.wakeTurbulenceCategory())
        );

        // Binding the altitude to a paint property and then binding the icon color to that paint property
        oas.altitudeProperty().addListener(e -> {
            ObjectProperty<Paint> iconColor =
                    new SimpleObjectProperty<>(ColorRamp.PLASMA.at(Math.cbrt(Math.rint(oas.getAltitude() / ALTITUDE_CEILING))));
            aircraftIcon.fillProperty().bind(iconColor);
        });

        //aircraftIcon.fillProperty().bind(Bindings.createObjectBinding(() ->
        //ColorRamp.PLASMA.at(Math.cbrt(oas.getAltitude() / ALTITUDE_CEILING))
        //));
        aircraftIcon.contentProperty().bind(Bindings.createStringBinding(icon.getValue()::svgPath, icon));
        aircraftIcon.rotateProperty().bind(Bindings.createDoubleBinding(() -> {
            if (icon.getValue().canRotate()) {
                return Units.convertTo(oas.trackOrHeadingProperty().get(), Units.Angle.DEGREE);
            }
            return 0.0;
        }));
        //return new Group(aircraftIcon);
        //just trying to see what's working and what isn't
        return new Rectangle(10, 10, new Color(0,0,1,1));
    }
    /*
    private BooleanBinding isShowable(Group label){
        return Bindings.createBooleanBinding(
                () -> currentZoom.get() <= 11 || airCraftHasBeenClicked(icon));
    }*/

    /*private boolean airCraftHasBeenClicked(Group icon){
        aircraftPane.setOnMouseClicked(e -> {
            if ((e.getX() == aircraftPane.getWidth() + icon.getLayoutX()) && e.getY() == aircraftPane.getHeight() + icon.getLayoutY() && !airCraftClicked)
                airCraftClicked = true;
            else if (airCraftClicked)
                airCraftClicked = false;
        });
        return airCraftClicked;
    }*/
}