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

public class Main extends Application {
    private long time;

    private long MILLION = 1_000_000L;
    private long BILLION = 1_000_000_000L;
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
        MapParameters mp = new MapParameters(8, 33_530, 23_070);
        ObjectProperty<ObservableAircraftState> sap = new SimpleObjectProperty<>();

        AircraftController ac = new AircraftController(mp, asm.states(), sap);
        AircraftTableController table = new AircraftTableController(asm.states(), sap);
        StatusLineController controller = new StatusLineController();

        controller.airCraftCountProperty().bind(Bindings.size(asm.states()));

        BaseMapController bmc = new BaseMapController(tm, mp);

        // Set on DoubleClick
        // TODO: 5/19/2023 does the set method have to be called 
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
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();

        Supplier<Message> messageSupplier = (parameters.isEmpty()) ? demodulation() : messageFromFile(parameters);

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
            @Override
            public void handle(long now) {
                try {
                    if(time == 0)
                        time = now;
                    double elapsedTime = (now - time) / BILLION;
                    while (!messages.isEmpty()) {
                        Message m = messages.remove();
                        controller.messageCountProperty().set(controller.messageCountProperty().get() + 1);
                        asm.updateWithMessage(m);
                        if(elapsedTime > 1){
                            asm.purge();
                            time = now;
                        }
                    }
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
                        return MessageParser.parse(message);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException();
            }
        };
    }

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
                }  catch (InterruptedException e) {
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