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
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

public class Main extends Application {

    private long timeOnStartUp;

    private long MILLION = 1_000_000L;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        timeOnStartUp = System.nanoTime();
        List<String> parameters = this.getParameters().getRaw();
        Queue<Message> messages = new ConcurrentLinkedQueue();

        Supplier<Message> messageSupplier = parameters.isEmpty() ? demodulation() : messageFromFile(parameters);

        URL dbUrl = getClass().getResource("/aircraft.zip");
        assert dbUrl != null;
        String f = Path.of(dbUrl.toURI()).toString();
        var dataBase = new AircraftDatabase(f);
        AircraftStateManager asm = new AircraftStateManager(dataBase);


        Thread messageHandler = new Thread(() -> {
            while (true) {
                if (messageSupplier.get() == null)
                    break;
                else messages.add(messageSupplier.get());
            }
        });

        messageHandler.setDaemon(true);
        messageHandler.start();

        Path tileCache = Path.of("tile-cache");
        TileManager tm = new TileManager(tileCache, "tile.openstreetmap.org");
        MapParameters mp = new MapParameters(8, 33_530, 23_070);
        ObjectProperty<ObservableAircraftState> sap = new SimpleObjectProperty<>();


        AircraftController ac = new AircraftController(mp, asm.states(), sap);
        AircraftTableController table = new AircraftTableController(asm.states(), sap);
        StatusLineController controller = new StatusLineController();

        controller.airCraftCountProperty().bind(Bindings.size(asm.states()));

        BorderPane tableAndLinePane =
                new BorderPane(table.pane(), controller.pane(), null, null, null);

        BaseMapController bmc = new BaseMapController(tm, mp);

        StackPane aircraftAndMapPane = new StackPane(bmc.pane(), ac.pane());
        SplitPane mainPane = new SplitPane(aircraftAndMapPane, tableAndLinePane);
        mainPane.setOrientation(Orientation.VERTICAL);
        primaryStage.setTitle("Javions");
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();


        new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    while (!messages.isEmpty()) {
                        Message m = messages.remove();
                        controller.messageCountProperty().set(controller.messageCountProperty().get() + 1);
                        asm.updateWithMessage(m);
                    }
                    //purge
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }.start();
    }

    private Supplier<Message> demodulation() {
        return () -> {
            try {
                while (true) {
                    AdsbDemodulator demodulator = new AdsbDemodulator(System.in);
                    if (demodulator.nextMessage() != null) {
                        RawMessage message = demodulator.nextMessage();
                        MessageParser.parse(message);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException();
            }
        };
    }

    private Supplier<Message> messageFromFile(List<String> parameters) {
        String f = Path.of(parameters.get(0)).toString();
        List<Message> messages = new ArrayList<>();
        var mi = messages.iterator();
        return () -> {
            try (DataInputStream s = new DataInputStream(
                    new FileInputStream(f))) {
                byte[] bytes = new byte[RawMessage.LENGTH];
                long timeStampNs;
                while (true) {
                    timeStampNs = s.readLong();
                    int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                    assert bytesRead == RawMessage.LENGTH;
                    long deltaT = timeStampNs - (System.nanoTime() - timeOnStartUp);
                    if (deltaT >= 0) {
                        Thread.sleep(deltaT / MILLION);
                    }
                    messages.add(MessageParser.parse(new RawMessage(timeStampNs, new ByteString(bytes))));
                    if(mi.hasNext())
                        return mi.next();
                }
            } catch (InterruptedException | IOException exception) {
                throw new RuntimeException();
            }
        };
    }

    private static List<RawMessage> readAllMessages(String fileName)
            throws IOException {
        List<RawMessage> rawMessages = new ArrayList<>();
        try (DataInputStream s = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(fileName)))) {
            byte[] bytes = new byte[RawMessage.LENGTH];
            long timeStampNs;
            while (true) {
                timeStampNs = s.readLong();
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                rawMessages.add(new RawMessage(timeStampNs, new ByteString(bytes)));
            }
        } catch (EOFException exception) {

        }
        return rawMessages;
    }
}