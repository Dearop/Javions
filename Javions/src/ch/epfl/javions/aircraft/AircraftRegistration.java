package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

public record AircraftRegistration(String string) {
    private static Pattern pattern;

    /**
     *
     * @param string
     */
    public AircraftRegistration{
        pattern = Pattern.compile("^[A-Z0-9 .?/_+-]+$");
        if (string.isEmpty() || !pattern.matcher(string).matches()) throw new IllegalArgumentException();
    }
}
