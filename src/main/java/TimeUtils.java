import lombok.NonNull;

import java.time.*;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.EnumSet;
import java.util.List;

import static java.time.temporal.TemporalAdjusters.next;
import static java.time.temporal.TemporalAdjusters.nextOrSame;

public final class TimeUtils {

    private static final EnumSet<DayOfWeek> WEEKEND_DAYS = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
    private static final EnumSet<DayOfWeek> WORKING_DAYS = EnumSet.complementOf(WEEKEND_DAYS);

    private TimeUtils() {}

    /**
     * <p>Returns a {@code TemporalAdjuster} decorator that handles {@code Instant} objects
     * as date-time values by converting them to and from {@code ZonedDateTime} in UTC.</p>
     *
     * <p>If the input {@code Temporal} object is an {@code Instant}, it is
     * converted to a {@code ZonedDateTime} in UTC, the specified adjuster is
     * applied, and the result is converted back to an {@code Instant}.</p>
     *
     * <p>For other {@code Temporal} types, the adjuster is applied directly.</p>
     *
     * @param adjuster the temporal adjuster to be applied
     * @return a TemporalAdjuster that handles Instant objects as date-time values in UTC
     */
    public static TemporalAdjuster withInstantAsZuluDateTime(@NonNull TemporalAdjuster adjuster) {
        return temporal -> temporal instanceof Instant instant ?
            instant.atZone(ZoneOffset.UTC).with(adjuster).toInstant() :
            temporal.with(adjuster);
    }

    /**
     * <p>Returns a {@code TemporalAdjuster} which returns the nearest future time
     * which aligns exactly with the specified unit.</p>
     *
     * <p>This method supports only time-based units.</p>
     *
     * <p>Examples:</p>
     * <table>
     *     <thead>
     *         <tr><th>Unit</th><th>Input</th><th>Output</th></tr>
     *     </thead>
     *     <tbody>
     *         <tr><td>ChronoUnit.MINUTES</td><td>10:45:00</td><td>10:46:00</td></tr>
     *         <tr><td>ChronoUnit.MINUTES</td><td>17:00:30</td><td>17:01:00</td></tr>
     *         <tr><td>ChronoUnit.HALF_DAYS</td><td>04:10:59</td><td>12:00:00</td></tr>
     *     </tbody>
     * </table>
     *
     * @param unit the temporal unit to which the adjustment is applied
     * @return the earliest future exact time adjuster, not null
     * @throws UnsupportedTemporalTypeException if the specified unit is not supported
     */
    public static TemporalAdjuster nextExact(@NonNull TemporalUnit unit) {
        if (!unit.isTimeBased()) {
            throw new UnsupportedTemporalTypeException("Unsupported temporal unit: " + unit);
        }
        return withInstantAsZuluDateTime(temporal -> {
            LocalTime time = LocalTime.from(temporal);
            LocalTime t0 = time.truncatedTo(unit);
            return temporal.with(t0).plus(1, unit);
        });
    }

    /**
     * <p>Returns a {@code TemporalAdjuster} which returns the nearest present or future time
     * which aligns exactly with the specified unit.</p>
     *
     * <p>This method supports only time-based units.</p>
     *
     * <p>Examples:</p>
     * <table>
     *     <thead>
     *         <tr><th>Unit</th><th>Input</th><th>Output</th></tr>
     *     </thead>
     *     <tbody>
     *         <tr><td>ChronoUnit.MINUTES</td><td>10:45:00</td><td>10:45:00</td></tr>
     *         <tr><td>ChronoUnit.MINUTES</td><td>17:00:30</td><td>17:01:00</td></tr>
     *         <tr><td>ChronoUnit.HALF_DAYS</td><td>04:10:59</td><td>12:00:00</td></tr>
     *     </tbody>
     * </table>
     *
     * @param unit the temporal unit to which the adjustment is applied
     * @return the earliest present or future exact time adjuster, not null
     * @throws UnsupportedTemporalTypeException if the specified unit is not supported
     */
    public static TemporalAdjuster nextOrSameExact(@NonNull TemporalUnit unit) {
        if (!unit.isTimeBased()) {
            throw new UnsupportedTemporalTypeException("Unsupported temporal unit: " + unit);
        }
        return withInstantAsZuluDateTime(temporal -> {
            LocalTime time = LocalTime.from(temporal);
            LocalTime t0 = time.truncatedTo(unit);
            return time.equals(t0) ? temporal : temporal.with(t0).plus(1, unit);
        });
    }

    /**
     * Returns an unmodifiable list containing the weekend days,
     * which are defined as Saturday and Sunday.
     *
     * @return an unmodifiable {@code List} of {@link DayOfWeek} representing the weekend days.
     */
    public static List<DayOfWeek> getWeekendDays() {
        return List.copyOf(WEEKEND_DAYS);
    }

    /**
     * Returns an unmodifiable list containing the working days,
     * which are defined as Monday through Friday.
     *
     * @return an unmodifiable {@code List} of {@link DayOfWeek} representing the working days.
     */
    public static List<DayOfWeek> getWorkingDays() {
        return List.copyOf(WORKING_DAYS);
    }

    /**
     * Returns a {@code TemporalAdjuster} that advances the given temporal by the specified
     * number of working days (Monday to Friday).
     *
     * <p>If the given temporal falls on a weekend (Saturday or Sunday), the adjustment
     * starts from the next working day (Monday).</p>
     *
     * @param amount the number of working days to add; must be non-negative
     * @return a {@link TemporalAdjuster} that moves the temporal forward by the given number of working days
     * @throws IllegalArgumentException if {@code days} is negative
     */
    public static TemporalAdjuster nextWorkingDays(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Invalid amount: " + amount);
        }
        return withInstantAsZuluDateTime(new TemporalAdjuster() {
            final int wdSize = WORKING_DAYS.size();

            @Override
            public Temporal adjustInto(Temporal temporal) {
                DayOfWeek day0 = DayOfWeek.from(temporal);
                if (WEEKEND_DAYS.contains(day0)) {
                    return temporal.with(next(DayOfWeek.MONDAY)).with(this);
                }
                Period weeks = Period.ofWeeks(amount / wdSize);
                int remainder = amount % wdSize;
                DayOfWeek day = DayOfWeek.of((day0.ordinal() + remainder) % wdSize + 1);
                return temporal.plus(weeks).with(nextOrSame(day));
            }
        });
    }
}
