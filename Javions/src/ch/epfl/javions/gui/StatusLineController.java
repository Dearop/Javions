package ch.epfl.javions.gui;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public final class StatusLineController {

    private BorderPane scenegraph;
    private IntegerProperty airCraftCountProperty = new SimpleIntegerProperty();
    private LongProperty messageCountProperty = new SimpleLongProperty();

    public StatusLineController(){
        this.airCraftCountProperty.set(0);
        this.messageCountProperty.set(0);

        Text numberOfAircraft = new Text();
        ObservableValue<String> bottomLeftText = new SimpleObjectProperty<>("Aeronefs visibles " + Bindings.createStringBinding(() ->
                String.format("%s" , airCraftCountProperty.get())));
        numberOfAircraft.textProperty().bind(bottomLeftText);
        Text numberOfMessages = new Text();
        ObservableValue<String> topRight = new SimpleObjectProperty<>("Aeronefs visibles " + Bindings.createStringBinding(() ->
                String.format("%s" ,  messageCountProperty.get())));
        numberOfMessages.textProperty().bind(topRight);

        this.scenegraph = new BorderPane(null, null,numberOfAircraft , null, numberOfMessages);
        scenegraph.setPickOnBounds(false);
        scenegraph.getStylesheets().add("status.css");

    }

    public Pane pane(){
        return scenegraph;
    }

    public IntegerProperty airCraftCountProperty(){
        return airCraftCountProperty;
    }

    public LongProperty messageCountProperty(){
        return messageCountProperty;
    }
}
