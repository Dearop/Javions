package ch.epfl.javions.aircraft;

public enum WakeTurbulenceCategory {
    LIGHT, MEDIUM, HEAVY, UNKNOWN;

    /**
     *
      * @param s
     * @return
     */
    public static WakeTurbulenceCategory WakeTurbulenceCategoryof(String s){
        if (s.equals("L")) return LIGHT;
        if (s.equals("M")) return MEDIUM;
        if (s.equals("H")) return HEAVY;
        return UNKNOWN;
    }
}
