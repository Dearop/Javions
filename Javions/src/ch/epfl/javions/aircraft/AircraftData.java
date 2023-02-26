package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.function.Executable;

import java.util.Objects;
import java.util.stream.Stream;

public record AircraftData(AircraftRegistration registration, AircraftTypeDesignator typeDesignator, String model,
                           AircraftDescription description, WakeTurbulenceCategory wakeTurbulenceCategory) implements Executable {
    /** The constructor of AircraftData throws NullPointerException if any of it's attriubutes are null
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

    @Override
    public void execute() throws Throwable {

    }
}
