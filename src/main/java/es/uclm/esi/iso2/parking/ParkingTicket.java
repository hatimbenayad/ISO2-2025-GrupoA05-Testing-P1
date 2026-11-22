package es.uclm.esi.iso2.parking;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public final class ParkingTicket {
    private final LocalDateTime entryTime;
    private final LocalDateTime exitTime;

    public ParkingTicket(LocalDateTime entryTime, LocalDateTime exitTime) {
        if (entryTime == null) {
            throw new NullArgumentException("entryTime must not be null");
        }
        if (exitTime == null) {
            throw new NullArgumentException("exitTime must not be null");
        }
        if (exitTime.isBefore(entryTime)) {
            throw new InvalidParkingStayException("exitTime must not be before entryTime");
        }

        this.entryTime = entryTime;
        this.exitTime = exitTime;
    }

    public long getDurationInMinutes() {
        return Duration.between(entryTime, exitTime).toMinutes();
    }

    @Override
    public String toString() {
        return "ParkingTicket{entryTime=" + entryTime + ", exitTime=" + exitTime + '}';
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ParkingTicket that)) {
            return false;
        }
        return entryTime.equals(that.entryTime) && exitTime.equals(that.exitTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entryTime, exitTime);
    }
}
