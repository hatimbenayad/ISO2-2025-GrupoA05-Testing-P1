package es.uclm.esi.iso2.parking;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public final class ParkingCalculator {
    public static final long FREE_MINUTES = 30;
    public static final long MINUTES_PER_DAY = 1440;
    public static final BigDecimal RATE_PER_MINUTE = new BigDecimal("0.05");
    public static final BigDecimal DAILY_MAXIMUM = new BigDecimal("20.00");

    public BigDecimal calculateTotalCost(ParkingTicket ticket) {
        if (ticket == null) {
            throw new NullArgumentException("ticket must not be null");
        }

        long durationInMinutes = ticket.getDurationInMinutes();
        long fullDays = durationInMinutes / MINUTES_PER_DAY;
        long remainingMinutes = durationInMinutes % MINUTES_PER_DAY;

        BigDecimal fullDaysCost = DAILY_MAXIMUM.multiply(BigDecimal.valueOf(fullDays));
        BigDecimal totalCost = fullDaysCost.add(calculatePartialDayCost(remainingMinutes));

        return totalCost.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTotalCost(LocalDateTime entry, LocalDateTime exit) {
        return calculateTotalCost(new ParkingTicket(entry, exit));
    }

    private BigDecimal calculatePartialDayCost(long minutes) {
        if (minutes <= FREE_MINUTES) {
            return BigDecimal.ZERO;
        }

        BigDecimal cost = RATE_PER_MINUTE.multiply(BigDecimal.valueOf(minutes - FREE_MINUTES));
        return cost.min(DAILY_MAXIMUM);
    }
}
