package es.uclm.esi.iso2.parking;

public class InvalidParkingStayException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;

    public InvalidParkingStayException(String message) {
        super(message);
    }
}
