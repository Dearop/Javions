package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.WebMercator;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;


public final class AircraftController {
    private MapParameters parameters;
    private ObservableSet<ObservableAircraftState> knownStates;
    private ObjectProperty<ObservableAircraftState> currentSelectedState;
    private Pane aircraftPane;
    private IntegerProperty currentZoom;
    private boolean airCraftClicked;
    private static final int ALTITUDE_CEILING = 12000;

    public AircraftController(MapParameters parameters, ObservableSet<ObservableAircraftState> knownStates,
                              ObjectProperty<ObservableAircraftState> currentSelectedState) {
        this.parameters = parameters;
        this.knownStates = knownStates;
        this.currentSelectedState = currentSelectedState;
        installListeners();
    }

    public Pane pane() {
        aircraftPane = new Pane();
        for (ObservableAircraftState oas : knownStates) {
            Group mainGroup = individualAircraftGroup(oas);
            mainGroup.viewOrderProperty().bind(oas.altitudeProperty().negate());
            aircraftPane.getChildren().add(mainGroup);
        }
        aircraftPane.setPickOnBounds(false);
        return aircraftPane;
    }

    private Group individualAircraftGroup(ObservableAircraftState oas) {
        Group label = new Group(aircraftLabelInitialisation(oas));
        Group icon = new Group(aircraftIconInitialisation(oas));
        label.visibleProperty().bind(isShowable(icon));
        Group iconAndLabel = new Group(label, icon);
        aircraftLabelAndIconPositioning(oas, iconAndLabel);
        Group aircraftGroup =
                new Group(aircraftTrajectory(oas), iconAndLabel);
        aircraftGroup.setId(oas.icaoAddress().string());
        aircraftGroup.getParent().getStylesheets();

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


    private void aircraftLabelAndIconPositioning(ObservableAircraftState oas, Group iconAndLabel) {
        setBindings();
        //binding the icon and label to the position of the aircraft
        SimpleObjectProperty<GeoPos> position = new SimpleObjectProperty<>();
        //bind the position of the aircraft to the position we are using
        position.bind(oas.positionProperty());
        double positionX = WebMercator.x(currentZoom.get(), position.get().latitude());
        double positionY = WebMercator.y(currentZoom.get(), position.get().longitude());
        // lmk what you think of this if statement, basically I'm only drawing the labels on the aircraft that we can see on the screen
        // TODO: 4/29/2023: might have to add a listener that observes the pane here if we don't see anything
        if (parameters.getMinX() < positionX && positionX < parameters.getMinX() + aircraftPane.getWidth()
                && parameters.getMinY() < positionY && positionY < parameters.getMinY() + aircraftPane.getHeight()) {
            iconAndLabel.layoutXProperty().bind(
                    Bindings.createDoubleBinding(() -> parameters.getMinX() - positionX));
            iconAndLabel.layoutYProperty().bind(
                    Bindings.createDoubleBinding(() -> parameters.getMinY() - positionY));
        }
    }

    public Group aircraftLabelInitialisation(ObservableAircraftState oas) {
        Rectangle rectangle = new Rectangle();
        Text text = new Text();
        text.textProperty().bind(
                Bindings.format("%f/n", new SimpleStringProperty(oas.icaoAddress().string())));
        text.textProperty().bind(
                Bindings.createStringBinding(() ->
                                String.format("%f m/s u/2002 %f mètres", oas.getVelocity(), oas.getAltitude())
                        , oas.velocityProperty(), oas.altitudeProperty()));
        rectangle.heightProperty().bind(
                text.layoutBoundsProperty().map(b -> b.getHeight()));
        rectangle.widthProperty().bind(
                text.layoutBoundsProperty().map(b -> b.getWidth() + 4));
        Group label = new Group(rectangle, text);
        label.getStyleClass();
        return label;
    }

    private Group aircraftIconInitialisation(ObservableAircraftState oas) {
        AircraftIcon icon = AircraftIcon.iconFor(oas.getData().typeDesignator(), oas.getData().description(),
                oas.getCategory(), oas.getData().wakeTurbulenceCategory());
        SVGPath aircraftIcon = new SVGPath();
        // Caracterisation of the SVGPath to get the Label
        aircraftIcon.getStyleClass();
        // Binding the altitude to a paint property and then binding the icon color to that paint property
        oas.altitudeProperty().addListener(e -> {
            ObjectProperty<Paint> iconColor =
                    new SimpleObjectProperty<>(ColorRamp.PLASMA.at(Math.cbrt(Math.rint(oas.getAltitude() / ALTITUDE_CEILING))));
            aircraftIcon.fillProperty().bind(iconColor);
        });
        aircraftIcon.contentProperty().bind(new SimpleStringProperty(icon.svgPath()));
        aircraftIcon.rotateProperty().bind(oas.trackOrHeadingProperty());
        return new Group(aircraftIcon);
    }

    private void installListeners() {
        knownStates.addListener((SetChangeListener<ObservableAircraftState>) c -> {
            Group addedAircraft = individualAircraftGroup(c.getElementAdded());
            addedAircraft.viewOrderProperty().bind(c.getElementAdded().altitudeProperty().negate());
            aircraftPane.getChildren().add(addedAircraft);
            aircraftPane.getChildren().remove(individualAircraftGroup(c.getElementRemoved()));
        });
    }

    private void setBindings(){
        currentZoom.bind(parameters.zoomProperty());
    }

    private BooleanBinding isShowable(Group icon){
        return Bindings.createBooleanBinding(
                () -> currentZoom.get() <= 11 || airCraftHasBeenClicked(icon));
    }

    private boolean airCraftHasBeenClicked(Group icon){
        aircraftPane.setOnMouseClicked(e -> {
            if ((e.getX() == aircraftPane.getWidth() + icon.getLayoutX()) && e.getY() == aircraftPane.getHeight() + icon.getLayoutY() && !airCraftClicked)
                airCraftClicked = true;
            else if (airCraftClicked)
                airCraftClicked = false;
        });
        return airCraftClicked;
    }
}