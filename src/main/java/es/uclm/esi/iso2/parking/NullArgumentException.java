package es.uclm.esi.iso2.parking;

public class NullArgumentException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;

    public NullArgumentException(String message) {
        super(message);
    }
}
