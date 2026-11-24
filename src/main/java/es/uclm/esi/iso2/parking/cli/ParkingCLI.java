package es.uclm.esi.iso2.parking.cli;

import es.uclm.esi.iso2.parking.InvalidParkingStayException;
import es.uclm.esi.iso2.parking.NullArgumentException;
import es.uclm.esi.iso2.parking.ParkingCalculator;
import es.uclm.esi.iso2.parking.ParkingTicket;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Scanner;

public final class ParkingCLI {
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm").withResolverStyle(ResolverStyle.STRICT);

    private ParkingCLI() {
    }

    public static void main(String[] args) {
        run(new Scanner(System.in), System.out);
    }

    static void run(Scanner scanner, PrintStream out) {
        out.println("=== Parking Fee Calculator ===");

        try {
            LocalDateTime entryTime = readDateTime(scanner, out, "Entry");
            LocalDateTime exitTime = readDateTime(scanner, out, "Exit");

            ParkingTicket ticket = new ParkingTicket(entryTime, exitTime);
            BigDecimal totalCost = new ParkingCalculator().calculateTotalCost(ticket);

            printReceipt(out, ticket.getDurationInMinutes(), totalCost);
        } catch (NullArgumentException e) {
            out.println("Error: missing date and time (" + e.getMessage() + ").");
        } catch (InvalidParkingStayException e) {
            out.println("Error: invalid parking stay (" + e.getMessage() + ").");
        } catch (DateTimeParseException e) {
            out.println("Error: '" + e.getParsedString() + "' is not a valid date and time (" + DATE_TIME_PATTERN + ").");
        }
    }

    private static LocalDateTime readDateTime(Scanner scanner, PrintStream out, String label) {
        out.print(label + " date and time (" + DATE_TIME_PATTERN + "): ");

        if (!scanner.hasNextLine()) {
            return null;
        }
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(input, FORMATTER);
    }

    private static void printReceipt(PrintStream out, long durationInMinutes, BigDecimal totalCost) {
        long fullDays = durationInMinutes / ParkingCalculator.MINUTES_PER_DAY;
        long remainingMinutes = durationInMinutes % ParkingCalculator.MINUTES_PER_DAY;
        long chargedMinutes = Math.max(0, remainingMinutes - ParkingCalculator.FREE_MINUTES);
        long freeMinutes = remainingMinutes - chargedMinutes;

        BigDecimal fullDaysCost = ParkingCalculator.DAILY_MAXIMUM.multiply(BigDecimal.valueOf(fullDays));
        BigDecimal remainingCost = totalCost.subtract(fullDaysCost);

        out.println();
        out.printf("Duration: %d min (%d d %02d h %02d min)%n",
                durationInMinutes, fullDays, remainingMinutes / 60, remainingMinutes % 60);
        out.printf("Full 24h blocks: %d x %s EUR = %s EUR%n",
                fullDays, ParkingCalculator.DAILY_MAXIMUM, fullDaysCost.setScale(2));
        out.printf("Remaining time: %d min (%d min free, %d min at %s EUR/min)%n",
                remainingMinutes, freeMinutes, chargedMinutes, ParkingCalculator.RATE_PER_MINUTE);
        out.printf("Remaining charge: %s EUR%s%n",
                remainingCost, remainingCost.compareTo(ParkingCalculator.DAILY_MAXIMUM) == 0 ? " (daily maximum)" : "");
        out.printf("Total: %s EUR%n", totalCost);
    }
}
