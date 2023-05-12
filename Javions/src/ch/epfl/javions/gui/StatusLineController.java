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

        Text numberOfAircraft = new Text();
        numberOfAircraft.textProperty().bind(Bindings.createStringBinding(() ->
               String.format("Aéronefs visibles : " + airCraftCountProperty.get())));

        Text numberOfMessages = new Text();
        numberOfMessages.textProperty().bind(Bindings.createStringBinding(() ->
                ("Messages reçus : " + messageCountProperty.get())));

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
