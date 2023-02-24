package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

public record AircraftDescription(String string) {
    private static Pattern pattern;
    public AircraftDescription{
        pattern = Pattern.compile("^[ABDGHLPRSTV-][0123468][EJPT-]+$");
        if(pattern.matcher(string).matches()) throw new IllegalArgumentException();
    }


}
