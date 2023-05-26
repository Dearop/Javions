package ch.epfl.javions.gui;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 * A controller class for a status line UI element.
 * The status line displays the number of visible aircraft and the number of received messages.
 *
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public final class StatusLineController {

    private BorderPane scenegraph;
    private IntegerProperty airCraftCountProperty = new SimpleIntegerProperty();
    private LongProperty messageCountProperty = new SimpleLongProperty();
    private static final String CSS_STATUS_FILE = "status.css";
    private static final String FORMATTING = "%s";

    /**
     * Constructs a new StatusLineController object.
     * Sets up the UI elements for displaying the number of visible aircraft and the number of received messages.
     * Binds the text of the UI elements to the corresponding properties.
     * Applies the CSS stylesheet "status.css" to the UI elements.
     */
    public StatusLineController(){

        Text numberOfAircraft = new Text();
        numberOfAircraft.textProperty().bind(Bindings.createStringBinding(() ->
                String.format(FORMATTING, "Aéronefs visibles : "
                        + airCraftCountProperty.get()), airCraftCountProperty));

        Text numberOfMessages = new Text();
        numberOfMessages.textProperty().bind(Bindings.createStringBinding(() ->
                String.format(FORMATTING, "Messages reçus : " + messageCountProperty.get()), messageCountProperty));

        this.scenegraph = new BorderPane(null, null,numberOfMessages , null,numberOfAircraft );
        scenegraph.setPickOnBounds(false);
        scenegraph.getStylesheets().add(CSS_STATUS_FILE);

    }

    /**
     * @return the UI element for the status line
     */
    public Pane pane(){
        return scenegraph;
    }

    /**
     * @return the integer property for the number of visible aircraft
     */
    public IntegerProperty airCraftCountProperty(){
        return airCraftCountProperty;
    }

    /**
     * @return the long property for the number of received messages
     */
    public LongProperty messageCountProperty(){
        return messageCountProperty;
    }
}
