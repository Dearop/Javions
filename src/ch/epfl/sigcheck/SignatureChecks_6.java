package ch.epfl.sigcheck;

// Attention : cette classe n'est *pas* un test JUnit, et son code n'est
// pas destiné à être exécuté. Son seul but est de vérifier, autant que
// possible, que les noms et les types des différentes entités à définir
// pour cette étape du projet sont corrects.

final class SignatureChecks_6 {
    private SignatureChecks_6() {}

    void checkAirborneVelocityMessage() throws Exception {
        this.v01 = new ch.epfl.javions.adsb.AirborneVelocityMessage(this.v02, this.v03, this.v04, this.v04);
        this.v01 = ch.epfl.javions.adsb.AirborneVelocityMessage.of(this.v05);
        this.v07 = this.v01.equals(this.v06);
        this.v08 = this.v01.hashCode();
        this.v03 = this.v01.icaoAddress();
        this.v04 = this.v01.speed();
        this.v02 = this.v01.timeStampNs();
        this.v09 = this.v01.toString();
        this.v04 = this.v01.trackOrHeading();
    }

    void checkAircraftStateAccumulator() throws Exception {
        this.v10 = new ch.epfl.javions.adsb.AircraftStateAccumulator<>(this.v11);
        this.v11 = this.v10.stateSetter();
        this.v10.update(this.v12);
    }

    void checkMessageParser() throws Exception {
        this.v12 = ch.epfl.javions.adsb.MessageParser.parse(this.v05);
    }

    ch.epfl.javions.adsb.AirborneVelocityMessage v01;
    long v02;
    ch.epfl.javions.aircraft.IcaoAddress v03;
    double v04;
    ch.epfl.javions.adsb.RawMessage v05;
    Object v06;
    boolean v07;
    int v08;
    String v09;
    ch.epfl.javions.adsb.AircraftStateAccumulator<ch.epfl.javions.adsb.AircraftStateSetter> v10;
    ch.epfl.javions.adsb.AircraftStateSetter v11;
    ch.epfl.javions.adsb.Message v12;
}
