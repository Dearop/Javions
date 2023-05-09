package ch.epfl.javions.gui;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public final class StatusLineController {

    private BorderPane scenegraph;
    private IntegerProperty airCraftCountProperty;
    private LongProperty messageCountProperty;

    public StatusLineController(){
        Text numberOfAircraft = new Text();
        ObservableValue<String> bottomLeftText = new SimpleObjectProperty<>("Aeronefs visibles " + Bindings.createStringBinding(() ->
                String.format("%s" , airCraftCountProperty.get())));
        numberOfAircraft.textProperty().bind(bottomLeftText);
        Text numberOfMessages = new Text();
        ObservableValue<String> topRight = new SimpleObjectProperty<>("Aeronefs visibles " + Bindings.createStringBinding(() ->
                String.format("%s" ,  messageCountProperty.get())));
        numberOfMessages.textProperty().bind(topRight);

        this.scenegraph = new BorderPane(null, null,numberOfAircraft , null, numberOfMessages);
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
