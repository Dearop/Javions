package ch.epfl.javions.aircraft;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class AircraftDatabase {
    private String fileName;
    public AircraftDatabase(String fileName){
        if(fileName == null) throw new NullPointerException();
        this.fileName = fileName;
    }

    public AircraftData get(IcaoAddress address) throws IOException {
        String aircraft = getClass().getResource("/aircraft.zip").getFile();
        try (ZipFile zipFileUsed = new ZipFile(aircraft);
             InputStream stream = zipFileUsed.getInputStream(zipFileUsed.getEntry("14.csv"));
             Reader reader = new InputStreamReader(stream, UTF_8);
             BufferedReader buffer = new BufferedReader(reader)) {
            String stringLineFiltered = "";
            while ((stringLineFiltered = buffer.readLine()) != null)
                System.out.println(stringLineFiltered);
        } // up to here code works duh

        // in here you need to go through the stringLineFiltered and find the address given
        //if(address == a certain icaoaddress)

        return // has to be the address file called by icao;
    }

}
