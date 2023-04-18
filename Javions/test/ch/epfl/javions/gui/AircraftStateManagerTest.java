package ch.epfl.javions.gui;
import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AircraftStateManagerTest {

    // TODO: 4/10/2023 OK this all works 
    @Test
    public void PrintingOutValues() {
        try (DataInputStream s = new DataInputStream(
                                    new BufferedInputStream(
                                    new FileInputStream("C:\\Users\\Paul\\Dropbox\\PC\\Documents\\EPFL\\BA 2\\POOP\\Javions\\Javions\\Javions\\resources\\messages_20230318_0915.bin")))){
            byte[] bytes = new byte[RawMessage.LENGTH];
            int i = 0;
            while (i < 4) {
                long timeStampNs = s.readLong();
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                ByteString message = new ByteString(bytes);
                System.out.printf("%13d: %s\n", timeStampNs, message);
                i+=1;
            }
        } catch (EOFException e) { /* nothing to do */ }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void AircraftStateManagerTestWindow(){
        AircraftDatabase database = new AircraftDatabase("C:\\Users\\Paul\\Dropbox\\PC\\Documents\\EPFL\\BA 2\\POOP\\Javions\\Javions\\Javions\\resources\\aircraft.zip");
        AircraftStateManager aircraftStateManager = new AircraftStateManager(database);
        List<ObservableAircraftState> goatManager = new ArrayList<>();
        long startTime = 0;

        try (DataInputStream s = new DataInputStream(
                                        new BufferedInputStream(
                                            new FileInputStream("C:\\Users\\Paul\\Dropbox\\PC\\Documents\\EPFL\\BA 2\\POOP\\Javions\\Javions\\Javions\\resources\\messages_20230318_0915.bin")))){
            byte[] bytes = new byte[RawMessage.LENGTH];
            int i = 0;
            while (i < 1e5) {
                long timeStampNs = s.readLong();
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                ByteString message = new ByteString(bytes);
                System.out.printf("%13d: %s\n", timeStampNs, message);
                Message realMessage = MessageParser.parse(new RawMessage(timeStampNs, message));
                aircraftStateManager.updateWithMessage(realMessage);
                AdressComparator<ObservableAircraftState> comparator = new AdressComparator<>();
                if(startTime == 0){
                    startTime = System.nanoTime();
                }
                if(aircraftStateManager.getAccumulatorMap().containsKey(realMessage.icaoAddress())){
                    goatManager.set(goatManager.indexOf(aircraftStateManager.getAccumulatorMap().get(realMessage.icaoAddress()).stateSetter()), aircraftStateManager.getAccumulatorMap().get(realMessage.icaoAddress()).stateSetter());
                } else
                    goatManager.add(aircraftStateManager.getAccumulatorMap().get(realMessage.icaoAddress()).stateSetter());
                if(System.nanoTime() - startTime > 1e9){
                    goatManager.sort(comparator); //do something
                    System.out.println(goatManager);
                    String CSI = "\u001B[";
                    String CLEAR_SCREEN = CSI + "2J";
                    System.out.print(CLEAR_SCREEN);
                }
                System.out.println(System.nanoTime() + "NanoTime");

                i++;
            }
        } catch (IOException ignored) {
            throw new IllegalArgumentException("I can't code");
        }
    }
    public static class AdressComparator<T> implements Comparator<ObservableAircraftState> {
        @Override
        public int compare(ObservableAircraftState o1, ObservableAircraftState o2) {
            return (o1.icaoAddress().string()).compareTo(o2.icaoAddress().string());
        }
    }

}

