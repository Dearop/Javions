package ch.epfl.sigcheck;

// Attention : cette classe n'est *pas* un test JUnit, et son code n'est
// pas destiné à être exécuté. Son seul but est de vérifier, autant que
// possible, que les noms et les types des différentes entités à définir
// pour cette étape du projet sont corrects.

final class SignatureChecks_2 {
    private SignatureChecks_2() {}

    void checkCrc24() throws Exception {
        this.v01 = new ch.epfl.javions.Crc24(this.v02);
        this.v02 = ch.epfl.javions.Crc24.GENERATOR;
        this.v02 = this.v01.crc(this.v03);
    }

    void checkAircraftDescription() throws Exception {
        this.v04 = new ch.epfl.javions.aircraft.AircraftDescription(this.v05);
        this.v07 = this.v04.equals(this.v06);
        this.v02 = this.v04.hashCode();
        this.v05 = this.v04.string();
        this.v05 = this.v04.toString();
    }

    void checkAircraftRegistration() throws Exception {
        this.v08 = new ch.epfl.javions.aircraft.AircraftRegistration(this.v05);
        this.v07 = this.v08.equals(this.v06);
        this.v02 = this.v08.hashCode();
        this.v05 = this.v08.string();
        this.v05 = this.v08.toString();
    }

    void checkAircraftTypeDesignator() throws Exception {
        this.v09 = new ch.epfl.javions.aircraft.AircraftTypeDesignator(this.v05);
        this.v07 = this.v09.equals(this.v06);
        this.v02 = this.v09.hashCode();
        this.v05 = this.v09.string();
        this.v05 = this.v09.toString();
    }

    void checkIcaoAddress() throws Exception {
        this.v10 = new ch.epfl.javions.aircraft.IcaoAddress(this.v05);
        this.v07 = this.v10.equals(this.v06);
        this.v02 = this.v10.hashCode();
        this.v05 = this.v10.string();
        this.v05 = this.v10.toString();
    }

    void checkCallSign() throws Exception {
        this.v11 = new ch.epfl.javions.adsb.CallSign(this.v05);
        this.v07 = this.v11.equals(this.v06);
        this.v02 = this.v11.hashCode();
        this.v05 = this.v11.string();
        this.v05 = this.v11.toString();
    }

    void checkWakeTurbulenceCategory() throws Exception {
        this.v12 = ch.epfl.javions.aircraft.WakeTurbulenceCategory.HEAVY;
        this.v12 = ch.epfl.javions.aircraft.WakeTurbulenceCategory.LIGHT;
        this.v12 = ch.epfl.javions.aircraft.WakeTurbulenceCategory.MEDIUM;
        this.v12 = ch.epfl.javions.aircraft.WakeTurbulenceCategory.UNKNOWN;
        this.v12 = ch.epfl.javions.aircraft.WakeTurbulenceCategory.of(this.v05);
        this.v12 = ch.epfl.javions.aircraft.WakeTurbulenceCategory.valueOf(this.v05);
        this.v13 = ch.epfl.javions.aircraft.WakeTurbulenceCategory.values();
    }

    void checkAircraftData() throws Exception {
        this.v14 = new ch.epfl.javions.aircraft.AircraftData(this.v08, this.v09, this.v05, this.v04, this.v12);
        this.v04 = this.v14.description();
        this.v07 = this.v14.equals(this.v06);
        this.v02 = this.v14.hashCode();
        this.v05 = this.v14.model();
        this.v08 = this.v14.registration();
        this.v05 = this.v14.toString();
        this.v09 = this.v14.typeDesignator();
        this.v12 = this.v14.wakeTurbulenceCategory();
    }

    void checkAircraftDatabase() throws Exception {
        this.v15 = new ch.epfl.javions.aircraft.AircraftDatabase(this.v05);
        this.v14 = this.v15.get(this.v10);
    }

    ch.epfl.javions.Crc24 v01;
    int v02;
    byte[] v03;
    ch.epfl.javions.aircraft.AircraftDescription v04;
    String v05;
    Object v06;
    boolean v07;
    ch.epfl.javions.aircraft.AircraftRegistration v08;
    ch.epfl.javions.aircraft.AircraftTypeDesignator v09;
    ch.epfl.javions.aircraft.IcaoAddress v10;
    ch.epfl.javions.adsb.CallSign v11;
    ch.epfl.javions.aircraft.WakeTurbulenceCategory v12;
    ch.epfl.javions.aircraft.WakeTurbulenceCategory[] v13;
    ch.epfl.javions.aircraft.AircraftData v14;
    ch.epfl.javions.aircraft.AircraftDatabase v15;
}
