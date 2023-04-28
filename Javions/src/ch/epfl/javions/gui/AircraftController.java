package ch.epfl.javions.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;


public final class AircraftController {
    private MapParameters parameters;
    private ObservableSet<ObservableAircraftState> knownStates;
    private ObjectProperty<ObservableAircraftState> currentSelectedState;
    private Pane aircraftPane;

    public AircraftController(MapParameters parameters, ObservableSet<ObservableAircraftState> knownStates,
                              ObjectProperty<ObservableAircraftState> currentSelectedState){
        this.parameters = parameters;
        this.knownStates = knownStates;
        this.currentSelectedState = currentSelectedState;
        installListeners();
    }

    public Pane pane(){
        aircraftPane = new Pane();
        for(ObservableAircraftState oas : knownStates){
            Group mainGroup = aircraftGroup(oas);
            mainGroup.viewOrderProperty().bind(oas.altitudeProperty().negate());
            aircraftPane.getChildren().add(mainGroup);
        }
        aircraftPane.setPickOnBounds(false);
        return aircraftPane;
    }

    private Group aircraftGroup(ObservableAircraftState oas){
        Group aircraftGroup = new Group(aircraftTrajectory(oas), aircraftLabelAndIcon(oas));
        aircraftGroup.setId(oas.icaoAddress().string());
        aircraftGroup.getParent().getStylesheets();

        return aircraftGroup;
    }

    private Group aircraftTrajectory(ObservableAircraftState oas){
        return null;
    }

    private Group aircraftLabelAndIcon(ObservableAircraftState oas){
        //initialisation of the different parts of the Group for the Label and Icon
        AircraftIcon icon = AircraftIcon.iconFor(oas.getData().typeDesignator(), oas.getData().description(),
                oas.getCategory(), oas.getData().wakeTurbulenceCategory());
        SVGPath aircraftIcon = new SVGPath();
        // Caracterisation of the SVGPath to get the Label
        aircraftIcon.getStyleClass();
        //bindings
        aircraftIcon.contentProperty().bind(new SimpleStringProperty(icon.svgPath()));
        aircraftIcon.rotateProperty().bind(oas.trackOrHeadingProperty());


        //Creation of the label
        Rectangle rectangle = new Rectangle();
        Text text = new Text();
        //make these lines a function
        text.textProperty().bind(Bindings.format("%f/n", new SimpleStringProperty(oas.icaoAddress().string())));
        text.textProperty().bind(
                Bindings.createStringBinding(() ->
                        String.format("%f m/s", oas.getVelocity()) , oas.velocityProperty()));
        text.textProperty().bind(
                Bindings.createStringBinding(() ->
                        String.format("%f mètres", oas.getAltitude()), oas.altitudeProperty()));

        rectangle.widthProperty().bind(text.layoutBoundsProperty().map(b -> b.getWidth() + 4));
        Group label = new Group(rectangle, text);
        label.getStyleClass();
        return new Group(label, aircraftIcon);
    }

    private void installListeners(){
        knownStates.addListener((SetChangeListener<ObservableAircraftState>) c -> {
           aircraftPane.getChildren().add(aircraftGroup(c.getElementRemoved()));
           aircraftPane.getChildren().remove(aircraftGroup(c.getElementRemoved()));
        });

    }
}