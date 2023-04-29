package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.WebMercator;
import javafx.beans.binding.Bindings;
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
    private static final int ALTITUDE_CEILING = 12000;
    public AircraftController(MapParameters parameters, ObservableSet<ObservableAircraftState> knownStates,
                              ObjectProperty<ObservableAircraftState> currentSelectedState){
        this.parameters = parameters;
        this.knownStates = knownStates;
        this.currentSelectedState = currentSelectedState;
        installManagerListeners();
    }

    public Pane pane(){
        aircraftPane = new Pane();
        for(ObservableAircraftState oas : knownStates){
            Group mainGroup = individualAircraftGroup(oas);
            mainGroup.viewOrderProperty().bind(oas.altitudeProperty().negate());
            aircraftPane.getChildren().add(mainGroup);
        }
        aircraftPane.setPickOnBounds(false);
        return aircraftPane;
    }

    private Group individualAircraftGroup(ObservableAircraftState oas){
        Group iconAndLabel = new Group(new Group(aircraftLabel(oas), new Group(aircraftIcon(oas))));
        //binding the icon and label to the position of the aircraft
        SimpleObjectProperty<GeoPos> position = new SimpleObjectProperty<>();
        //bind the position of the aircraft to the position we are using
        position.bind(oas.positionProperty());
       double positionX = WebMercator.x(parameters.getZoom(), position.get().latitude());
        double positionY = WebMercator.y(parameters.getZoom(), position.get().longitude());
        // lmk what you think of this if statement, basically I'm only drawing the labels on the aircraft that we can see on the screen
        // TODO: 4/29/2023: might have to add a listener that observes the pane here if we don't see anything
        if(parameters.getMinX() < positionX && positionX < parameters.getMinX() + aircraftPane.getWidth()
                && parameters.getMinY() < positionY && positionY < parameters.getMinY() + aircraftPane.getHeight()){
            DoubleProperty positionOnPaneX = new SimpleDoubleProperty(parameters.getMinX() - positionX);
            DoubleProperty positionOnPaneY = new SimpleDoubleProperty(parameters.getMinY() - positionY);
            iconAndLabel.layoutXProperty().bind(positionOnPaneX);
            iconAndLabel.layoutYProperty().bind(positionOnPaneY);
        }
        Group aircraftGroup =
                new Group(aircraftTrajectory(oas), iconAndLabel);
        aircraftGroup.setId(oas.icaoAddress().string());
        aircraftGroup.getParent().getStylesheets();

        return aircraftGroup;
    }

    /**
     * null for now because I just want to be able to see the labels and the icons on the aircraft before doing anything else
     * @param oas
     * @return
     */
    private Group aircraftTrajectory(ObservableAircraftState oas){
        return null;
    }

    public Group aircraftLabel(ObservableAircraftState oas){
        Rectangle rectangle = new Rectangle();
        Text text = new Text();
        text.textProperty().bind(
                Bindings.format("%f/n", new SimpleStringProperty(oas.icaoAddress().string())));
        text.textProperty().bind(
                Bindings.createStringBinding(() ->
                        String.format("%f m/s", oas.getVelocity()) , oas.velocityProperty()));
        text.textProperty().bind(
                Bindings.createStringBinding(() ->
                        String.format("%f mètres", oas.getAltitude()), oas.altitudeProperty()));
        rectangle.widthProperty().bind(
                text.layoutBoundsProperty().map(b -> b.getWidth() + 4));
        Group label = new Group(rectangle, text);
        label.getStyleClass();
        return label;
    }

    private Group aircraftIcon(ObservableAircraftState oas){
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

    private void installManagerListeners(){
        knownStates.addListener((SetChangeListener<ObservableAircraftState>) c -> {
            Group addedAircraft = individualAircraftGroup(c.getElementAdded());
            addedAircraft.viewOrderProperty().bind(c.getElementAdded().altitudeProperty().negate());
           aircraftPane.getChildren().add(addedAircraft);
           aircraftPane.getChildren().remove(individualAircraftGroup(c.getElementRemoved()));
        });
    }
}