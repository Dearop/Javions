package ch.epfl.javions.aircraft;

import java.util.Objects;
import java.util.stream.Stream;

public record AircraftData(AircraftRegistration registration, AircraftTypeDesignator typeDesignator, String model,
                           AircraftDescription description, WakeTurbulenceCategory wakeTurbulenceCategory) {
    /**
     *
     * @param registration
     * @param typeDesignator
     * @param model
     * @param description
     * @param wakeTurbulenceCategory
     */
    public AircraftData{
        Objects.requireNonNull(registration);
        Objects.requireNonNull(typeDesignator);
        Objects.requireNonNull(model);
        Objects.requireNonNull(description);
        Objects.requireNonNull(wakeTurbulenceCategory);
    }
}
