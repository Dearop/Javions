package ch.epfl.javions.adsb;

import java.util.regex.Pattern;

public record CallSign(String string) {
    private static Pattern pattern;
    /**
     * If this doesn't work take away $ sign and accent circonflexe
     * @param string
     */
    public CallSign{
        pattern = Pattern.compile("^[A-Z0-9 ]{0,8}+$");
        if (string.isEmpty() || !pattern.matcher(string).matches()) throw new IllegalArgumentException();
    }
}
