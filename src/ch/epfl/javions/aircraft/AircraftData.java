package ch.epfl.javions.aircraft;

import java.util.Objects;

/**
 * The AircraftData class represents the fixed data for an aircraft, including its registration, type designator,
 * model, description, and wake turbulence category.
 * The class has a compact constructor that takes all the attributes as arguments and throws a
 * NullPointerException if any of them are null. This is done using the requireNonNull() method from the Objects class.
 *
 * @param registration           The registration number of the aircraft.
 * @param typeDesignator         The type designator of the aircraft.
 * @param model                  The model of the aircraft.
 * @param description            The description of the aircraft.
 * @param wakeTurbulenceCategory The wake turbulence category of the aircraft.
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public record AircraftData(AircraftRegistration registration, AircraftTypeDesignator typeDesignator, String model,
    AircraftDescription description, WakeTurbulenceCategory wakeTurbulenceCategory) {
    /**
     * The constructor of AircraftData throws NullPointerException if any of its attributes are null
     *
     * @param registration
     * @param typeDesignator
     * @param model
     * @param description
     * @param wakeTurbulenceCategory
     */
    public AircraftData {
        Objects.requireNonNull(registration);
        Objects.requireNonNull(typeDesignator);
        Objects.requireNonNull(model);
        Objects.requireNonNull(description);
        Objects.requireNonNull(wakeTurbulenceCategory);
    }
}
