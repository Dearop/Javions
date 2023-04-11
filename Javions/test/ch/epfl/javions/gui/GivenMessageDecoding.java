package ch.epfl.javions.gui;
import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.RawMessage;
import org.junit.jupiter.api.Test;

import java.io.*;

public class GivenMessageDecoding {

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

}
