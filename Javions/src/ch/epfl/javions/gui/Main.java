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
public class Main extends Application {
    private long time;
    private static final long MILLION = 1_000_000L;
    private static final long BILLION = 1_000_000_000L;
    private static final int WINDOW_HEIGHT = 600;
    private static final int WINDOW_WIDTH = 800;
    private static final int START_ZOOM = 8;
    private static final int START_TOP_TILE_X = 33_530;
    private static final int START_TOP_TILE_Y = 23_070;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        List<String> parameters = this.getParameters().getRaw();
        Queue<Message> messages = new ConcurrentLinkedQueue();

        URL dbUrl = getClass().getResource("/aircraft.zip");
        assert dbUrl != null;
        String f = Path.of(dbUrl.toURI()).toString();
        var dataBase = new AircraftDatabase(f);
        AircraftStateManager asm = new AircraftStateManager(dataBase);

        Path tileCache = Path.of("tile-cache");
        TileManager tm = new TileManager(tileCache, "tile.openstreetmap.org");
        MapParameters mp = new MapParameters(START_ZOOM, START_TOP_TILE_X, START_TOP_TILE_Y);
        ObjectProperty<ObservableAircraftState> sap = new SimpleObjectProperty<>();

        AircraftController ac = new AircraftController(mp, asm.states(), sap);
        AircraftTableController table = new AircraftTableController(asm.states(), sap);
        StatusLineController controller = new StatusLineController();

        controller.airCraftCountProperty().bind(Bindings.size(asm.states()));

        BaseMapController bmc = new BaseMapController(tm, mp);

        Consumer<ObservableAircraftState> stateConsumer = new Consumer<ObservableAircraftState>() {
            @Override
            public void accept(ObservableAircraftState oas) {
                bmc.centerOn(oas.getPosition());
                sap.set(oas);
            }
        };

        table.setOnDoubleClick(stateConsumer);

        BorderPane tableAndLinePane =
                new BorderPane(table.pane(), controller.pane(), null, null, null);

        StackPane aircraftAndMapPane = new StackPane(bmc.pane(), ac.pane());
        SplitPane mainPane = new SplitPane(aircraftAndMapPane, tableAndLinePane);
        mainPane.setOrientation(Orientation.VERTICAL);
        primaryStage.setTitle("Javions");
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.setMinWidth(WINDOW_WIDTH);
        primaryStage.setMinHeight(WINDOW_HEIGHT);
        primaryStage.show();

        Supplier<Message> messageSupplier =
                (parameters.isEmpty()) ? demodulation() : messageFromFile(parameters);

        Thread messageHandler = new Thread(() -> {
            while (true) {
                if (messageSupplier.get() == null)
                    break;
                else messages.add(messageSupplier.get());
            }
        });

        messageHandler.setDaemon(true);
        messageHandler.start();

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
                        Message m = messages.remove();
                        controller.messageCountProperty().set(controller.messageCountProperty().get() + 1);
                        asm.updateWithMessage(m);
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
     * to an IO port of the computer running this application.
     *
     * @return A Supplier containing a Message which is then added to the Queue.
     */
    private Supplier<Message> demodulation() {
        return () -> {
            try {
                while (true) {
                    AdsbDemodulator demodulator = new AdsbDemodulator(System.in);
                    if (demodulator.nextMessage() != null) {
                        RawMessage message = demodulator.nextMessage();
                        return MessageParser.parse(message);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException();
            }
        };
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
    private Supplier<Message> messageFromFile(List<String> parameters) {
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
                    return MessageParser.parse(new RawMessage(timeStampNs, new ByteString(bytes)));
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