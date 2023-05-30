package ch.epfl.javions.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;




public final class StatusLineControllerTest extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Test
    void readMessages() throws IOException {
        for(RawMessage message :readAllMessages("/home/henri/Henri/EPFL/CS108/Javions/Javions/resources/messages_20230318_0915.bin")) {
            System.out.println(message.toString());
        }

    }
    static List<RawMessage> readAllMessages(String fileName)
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

    @Override
    public void start(Stage primaryStage) throws Exception {
        // � � compl�ter (voir TestBaseMapController)
        Path tileCache = Path.of("tile-cache");
        TileManager tm = new TileManager(tileCache, "tile.openstreetmap.org");
        MapParameters mp =
                new MapParameters(17, 17_389_327, 11_867_430);
        BaseMapController bmc = new BaseMapController(tm,mp);


        // Cr�ation de la base de donn�es
        URL dbUrl = getClass().getResource("/aircraft.zip");
        assert dbUrl != null;
        String f = Path.of(dbUrl.toURI()).toString();
        var db = new AircraftDatabase(f);

        AircraftStateManager asm = new AircraftStateManager(db);
        ObjectProperty<ObservableAircraftState> sap =
                new SimpleObjectProperty<>();
        AircraftController ac =
                new AircraftController(mp, asm.states(), sap);

        StatusLineController controller = new StatusLineController();
        controller.airCraftCountProperty().bind(Bindings.createIntegerBinding(() -> asm.getAccumulatorMap().size()));
        System.out.println("yo");
        var root = new StackPane(controller.pane());
        //AircraftTableController atc = new AircraftTableController(asm.getKnownAircraftStates(), sap);

        //var root = new StackPane(atc.pane());
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        var mi = readAllMessages("/home/henri/Henri/EPFL/CS108/Javions/Javions/resources/messages_20230318_0915.bin").iterator();

        // Animation des a�ronefs
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