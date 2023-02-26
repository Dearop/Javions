package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

public record AircraftTypeDesignator(String string) {
    private static Pattern pattern;

    /**
     *
     * @param string
     */
    public AircraftTypeDesignator{
        pattern = Pattern.compile("^[A-Z0-9]{2,4}+$");
        if(!pattern.matcher(string).matches()) throw new IllegalArgumentException();
    }
}
