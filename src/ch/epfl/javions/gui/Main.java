package ch.epfl.javions.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * The main class of the Javions application. Extends the Application class
 * and serves as the entry point for launching the application.
 *
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public final class Main extends Application {
    private double time;
    private static final long MILLION = 1_000_000L;
    private static final long BILLION = 1_000_000_000L;
    private static final int WINDOW_START_HEIGHT = 600;
    private static final int WINDOW_START_WIDTH = 800;
    private static final int START_ZOOM = 8;
    private static final String AIRCRAFT_DATABASE_FILE = "/aircraft.zip";
    private static final String TILE_CACHE_FILENAME = "tile-cache";
    private static final String SERVER_ADDRESS = "tile.openstreetmap.org";
    private static final String APPLICATION_NAME = "Javions";
    private static final int START_TOP_TILE_X = 33_530;
    private static final int START_TOP_TILE_Y = 23_070;
    private final ConcurrentLinkedQueue<RawMessage> messages = new ConcurrentLinkedQueue<>();

    /**
     * The main entry point for the JavaFX application.
     *
     * @param args The command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    /**
     * Initializes and starts the JavaFX application. Including the different Panes and combining them into one!
     *
     * @param primaryStage The primary stage for the JavaFX application.
     * @throws Exception If an exception occurs during application startup.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        List<String> parameters = this.getParameters().getRaw();

        URL dbUrl = getClass().getResource(AIRCRAFT_DATABASE_FILE);
        assert dbUrl != null;
        String f = Path.of(dbUrl.toURI()).toString();
        var dataBase = new AircraftDatabase(f);
        AircraftStateManager asm = new AircraftStateManager(dataBase);

        Path tileCache = Path.of(TILE_CACHE_FILENAME);
        TileManager tm = new TileManager(tileCache, SERVER_ADDRESS);
        MapParameters mp = new MapParameters(START_ZOOM, START_TOP_TILE_X, START_TOP_TILE_Y);
        ObjectProperty<ObservableAircraftState> sap = new SimpleObjectProperty<>();

        AircraftController ac = new AircraftController(mp, asm.states(), sap);
        AircraftTableController table = new AircraftTableController(asm.states(), sap);
        StatusLineController controller = new StatusLineController();
        controller.airCraftCountProperty().bind(Bindings.size(asm.states()));

        BaseMapController bmc = new BaseMapController(tm, mp);

        Consumer<ObservableAircraftState> stateConsumer = oas -> {
            bmc.centerOn(oas.getPosition());
            sap.set(oas);
        };

        table.setOnDoubleClick(stateConsumer);

        BorderPane tableAndLinePane =
                new BorderPane(table.pane(), controller.pane(), null, null, null);

        StackPane aircraftAndMapPane = new StackPane(bmc.pane(), ac.pane());
        SplitPane mainPane = new SplitPane(aircraftAndMapPane, tableAndLinePane);
        mainPane.setOrientation(Orientation.VERTICAL);
        primaryStage.setTitle(APPLICATION_NAME);
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.setMinWidth(WINDOW_START_WIDTH);
        primaryStage.setMinHeight(WINDOW_START_HEIGHT);
        primaryStage.show();
        Thread messageHandler;
        if (parameters.isEmpty()) {
            messageHandler = new Thread(() -> demodulation());
        } else {
            messageHandler = new Thread(() -> {
                Supplier<RawMessage> messageSupplier = messageFromFile(parameters);
                while (true) {
                    if (messageSupplier.get() != null) {
                        messages.add(messageSupplier.get());
                    } else {
                        break;
                    }
                }
            });
        }
        messageHandler.setDaemon(true);
        messageHandler.start();

        // We create a new instance of an AnimationTimer and define its handle() method.
        new AnimationTimer() {
            /**
             * Redefinition of the handle method from the AnimationTimer Interface.
             */
            @Override
            public void handle(long now) {
                try {
                    // Checks if time variable is 0 and assigns 'now' value to it if true
                    if (time == 0)
                        time = now;
                    // Calculates the elapsed time in seconds since time variable was last updated
                    double elapsedTime = (now - time) / BILLION;
                    // Enters a loop while there are messages in the messages queue
                    while (!messages.isEmpty()) {
                        // Removes a message from the queue and passes it to the m variable
                        RawMessage rawM = messages.remove();
                        Message m;
                        if ((m = MessageParser.parse(rawM)) != null) {
                            controller.messageCountProperty().set(controller.messageCountProperty().get() + 1);
                            asm.updateWithMessage(m);
                        }
                        // Checks if elapsed time is greater than 1 second
                        if (elapsedTime > 1) {
                            // Purges the AircraftStateManager of any unnecessary data
                            asm.purge();
                            // Updates time variable to the current time
                            time = now;
                        }
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }.start(); // Starts the animation timer, causing the handle() method to be called repeatedly.
    }

    /**
     * Demodulates the messages coming from the AirSpy Radio supposedly connected
     * to an IO port of the computer running this application and adds it to the queue of messages.
     *
     */
    private void demodulation() {
        try {
            AdsbDemodulator demodulator = new AdsbDemodulator(System.in);
            while (true) {
                RawMessage message = demodulator.nextMessage();
                if (message != null) {
                    this.messages.add(message);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    /**
     * Reads messages coming from a local File, the file directory is read from the
     * application parameters. We sync up the timing of the messages with the time since startup
     * of the application so that the aircraft are simulated as they would be in real time with an
     * AirSpy radio instead of the messages being read sequentially.
     *
     * @param parameters List of Strings that contain the startup parameter applications
     * @return A Supplier containing a Message which is then added to the Queue.
     */
    private Supplier<RawMessage> messageFromFile(List<String> parameters) {
        String f = Path.of(parameters.get(0)).toString();
        long supplierStartTime = System.nanoTime();
        try {
            DataInputStream s = new DataInputStream(new FileInputStream(f));
            return () -> {
                try {
                    byte[] bytes = new byte[RawMessage.LENGTH];
                    long timeStampNs = s.readLong();
                    int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                    assert bytesRead == RawMessage.LENGTH;
                    long now = System.nanoTime();
                    long deltaT = timeStampNs - (now - supplierStartTime);
                    if (deltaT >= 0) {
                        Thread.sleep(deltaT / MILLION);
                    }
                    return new RawMessage(timeStampNs, new ByteString(bytes));
                } catch (InterruptedException e) {
                    throw new RuntimeException();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            };
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}