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

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL dbUrl = getClass().getResource("/aircraft.zip");
        String f = Path.of(dbUrl.toURI()).toString();
        AircraftDatabase dataBase = new AircraftDatabase(f);
        AdsbDemodulator demodulator = new AdsbDemodulator(System.in);

        Path tileCache = Path.of("tile-cache");
        TileManager tm = new TileManager(tileCache, "tile.openstreetmap.org");
        MapParameters mp = new MapParameters(8, 33_530, 23_070);
        AircraftStateManager asm = new AircraftStateManager(dataBase);

        BaseMapController bmc = new BaseMapController(tm, mp);
        ObjectProperty<ObservableAircraftState> sap = new SimpleObjectProperty<>();
        AircraftController ac = new AircraftController(mp, asm.states(), sap);
        AircraftTableController table = new AircraftTableController(asm.states(), sap);
        StatusLineController controller = new StatusLineController();
        controller.airCraftCountProperty().bind(Bindings.createIntegerBinding(() -> asm.getAccumulatorMap().size()));


        BorderPane tableAndLinePane = new BorderPane(table.pane(), controller.pane(), null, null, null);
        StackPane aircraftAndMapPane = new StackPane(bmc.pane(), ac.pane());
        SplitPane mainPane = new SplitPane(aircraftAndMapPane, table.pane());
        primaryStage.setTitle("Javions");
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();

        var mi = readAllMessages("C:\\Users\\Paul\\Dropbox\\PC\\Documents\\EPFL\\BA 2\\POOP\\Javions\\Javions\\Javions\\resources\\messages_20230318_0915.bin").iterator();
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    Message m = MessageParser.parse(mi.next());
                    if (m != null)
                        asm.updateWithMessage(m);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }.start();
    }


    private static List<RawMessage> readAllMessages(String fileName)
            throws IOException {
        List<RawMessage> rawMessages = new ArrayList<>();
        try (DataInputStream s = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(fileName)))) {
            byte[] bytes = new byte[RawMessage.LENGTH];
            long timeStampNs;
            while( true ) {
                timeStampNs = s.readLong();
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;

                rawMessages.add(new RawMessage(timeStampNs, new ByteString(bytes)));
            }
        }catch (EOFException exception){

        }
        return rawMessages;
    }
}