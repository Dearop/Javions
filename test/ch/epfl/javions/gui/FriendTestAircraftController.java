package ch.epfl.javions.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;




public final class FriendTestAircraftController extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Test
    void readMessages() throws IOException {
        for(RawMessage message :readAllMessages("C:\\Users\\Paul\\Dropbox\\PC\\Documents\\EPFL\\BA-2\\POOP\\Javions\\Javions\\Javions\\test-resources\\messages_20230318_0915.bin")) {
            System.out.println(message.toString());
        }

    }
    static List<RawMessage> readAllMessages(String fileName)
            throws IOException {
        System.out.println(fileName);
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

    @Override
    public void start(Stage primaryStage) throws Exception {
        // … à compléter (voir TestBaseMapController)
        Path tileCache = Path.of("tile-cache");
        TileManager tm = new TileManager(tileCache, "tile.openstreetmap.org");
        MapParameters mp =
                new MapParameters(17, 17_389_327, 11_867_430);
        BaseMapController bmc = new BaseMapController(tm,mp);

        // Création de la base de données
        URL dbUrl = getClass().getResource("/aircraft.zip");
        assert dbUrl != null;
        String f = Path.of(dbUrl.toURI()).toString();
        var db = new AircraftDatabase(f);

        AircraftStateManager asm = new AircraftStateManager(db);
        ObjectProperty<ObservableAircraftState> sap =
                new SimpleObjectProperty<>();
        AircraftController ac =
                new AircraftController(mp, asm.states(), sap);
        var root = new StackPane(bmc.pane(), ac.pane());
        //AircraftTableController atc = new AircraftTableController(asm.getKnownAircraftStates(), sap);

        //var root = new StackPane(atc.pane());
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        //URL messageURL = getClass().getResource("C:\\Users\\Paul\\Dropbox\\PC\\Documents\\EPFL\\BA-2\\POOP\\Javions\\Javions\\Javions\\test-resources\\messages_20230318_0915.bin");
        //String message = Path.of(messageURL.toURI()).toString();
        var mi = readAllMessages("C:\\Users\\Paul\\Dropbox\\PC\\Documents\\EPFL\\BA-2\\POOP\\Javions\\Javions\\Javions\\test-resources\\messages_20230318_0915.bin").iterator();

        // Animation des aéronefs
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    for (int i = 0; i < 10; i += 1) {
                        Message m = MessageParser.parse(mi.next());
                        if (m != null) asm.updateWithMessage(m);
                        asm.purge();
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }.start();
    }
}
