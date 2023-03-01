package ch.epfl.javions.aircraft;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class AircraftDatabase {
    private String fileName;

    public AircraftDatabase(String fileName) {
        if (fileName == null) throw new NullPointerException();
        this.fileName = fileName;
    }

    public AircraftData get(IcaoAddress address) throws IOException {
        String aircraft = getClass().getResource("/aircraft.zip").getFile();
        String stringLineFiltered = getStringLine(aircraft);
        int index;

        // up to here code works duh
        String addressString = String.valueOf(address);
        if (stringLineFiltered.startsWith(addressString)) {
            //finding the index of where the addressString is located
            index = stringLineFiltered.indexOf(addressString);

            // splits the string into an array for every line
            String[] lines = stringLineFiltered.split(System.getProperty("line.separator"));
        }


        // in here you need to go through the stringLineFiltered and find the address given
        /**
         *Example of a few lines in stringLineFiltered:
         *
         * E48414,PR-GGU,B738,BOEING 737-800,L2J,M
         * E48614,PR-GUF,B738,BOEING 737-800,L2J,M
         * E48714,PP-JMJ,C750,CESSNA 750 Citation 10,L2J,M
         * E48A14,PT-STK,C525,CESSNA 525 CitationJet,L2J,L
         *
         * the first value is the ICAO-address
         */


        //if(address == a certain icaoaddress)

        return // has to be the address file called by icao;
    }

    private static String getStringLine(String aircraft){

        try (ZipFile zipFileUsed = new ZipFile(aircraft);
             InputStream stream = zipFileUsed.getInputStream(zipFileUsed.getEntry("14.csv"));
             Reader reader = new InputStreamReader(stream, UTF_8);
             BufferedReader buffer = new BufferedReader(reader)) {
            String stringLineFiltered = "";
            while ((stringLineFiltered = buffer.readLine()) != null)
                System.out.println(stringLineFiltered);
            return stringLineFiltered;
        } catch (IOException e) {
            throw new RuntimeException(e +" zipFileError");
        }
    }

}
