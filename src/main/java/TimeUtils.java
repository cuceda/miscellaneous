import lombok.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;

import static java.time.temporal.TemporalAdjusters.nextOrSame;

public final class TimeUtils {

    private TimeUtils() {}

    /**
     * <p>Returns a {@code TemporalAdjuster} decorator that handles {@code Instant} objects
     * as date-time values by converting them to and from {@code ZonedDateTime} at
     * the specified time zone.</p>
     *
     * <p>If the input {@code Temporal} object is an {@code Instant}, it is
     * converted to a {@code ZonedDateTime}, the specified adjuster is applied
     * and the result is converted back to an {@code Instant}.</p>
     *
     * <p>For other {@code Temporal} types, the adjuster is applied directly.</p>
     *
     * @param zoneId   the id of the time zone with which to combine instant values
     * @param adjuster the temporal adjuster to be applied
     * @return a TemporalAdjuster that handles Instant objects as date-time values
     */
    public static TemporalAdjuster withInstantAtZone(@NonNull ZoneId zoneId, @NonNull TemporalAdjuster adjuster) {
        return temporal -> temporal instanceof Instant instant ?
                instant.atZone(zoneId).with(adjuster).toInstant() :
                temporal.with(adjuster);
    }

    /**
     * <p>Returns a {@code TemporalAdjuster} decorator that handles {@code Instant} objects
     * as date-time values by converting them to and from {@code ZonedDateTime} at Zulu time zone.</p>
     *
     * <p>If the input {@code Temporal} object is an {@code Instant}, it is
     * converted to a {@code ZonedDateTime}, the specified adjuster is applied
     * and the result is converted back to an {@code Instant}.</p>
     *
     * <p>For other {@code Temporal} types, the adjuster is applied directly.</p>
     *
     * @param adjuster the temporal adjuster to be applied
     * @return a TemporalAdjuster that handles Instant objects as date-time values
     */
    public static TemporalAdjuster withInstantAtZulu(@NonNull TemporalAdjuster adjuster) {
        return withInstantAtZone(ZoneOffset.UTC, adjuster);
    }

    /**
     * <p>Returns a {@code TemporalAdjuster} which rounds the time portion of a {@link Temporal}
     * to the specified unit using the given rounding mode.</p>
     *
     * <p>This method supports only time-based units (e.g., MINUTES, SECONDS, MILLISECONDS, etc.).
     * It does not support date-based units.</p>
     *
     * <p>Examples:</p>
     * <table>
     *     <thead>
     *         <tr><th>Unit</th><th>Mode</th><th>Input</th><th>Output</th></tr>
     *     </thead>
     *     <tbody>
     *         <tr><td>MINUTES</td><td>*</td><td>10:45:00</td><td>10:45:00</td></tr>
     *         <tr><td>MINUTES</td><td>HALF_UP</td><td>17:00:30</td><td>17:01:00</td></tr>
     *         <tr><td>HALF_DAYS</td><td>DOWN</td><td>14:10:59</td><td>12:00:00</td></tr>
     *     </tbody>
     * </table>
     *
     * @param unit the temporal unit to which the adjustment is applied
     * @param mode the rounding mode to apply (excluding {@link RoundingMode#UNNECESSARY})
     * @return a TemporalAdjuster that rounds time to the specified unit
     * @throws UnsupportedTemporalTypeException if the specified unit is not supported or if it is a date-based unit
     */
    public static TemporalAdjuster roundTime(@NonNull TemporalUnit unit, @NonNull RoundingMode mode) {
        if (!(unit.isTimeBased() && unit.getDuration().isPositive())) {
            throw new UnsupportedTemporalTypeException("Unsupported temporal unit: " + unit);
        } else if (mode == RoundingMode.UNNECESSARY) {
            throw new UnsupportedTemporalTypeException("Unsupported rounding mode: " + mode);
        }
        BigDecimal precision = BigDecimal.valueOf(unit.getDuration().toNanos());
        return temporal -> {
            BigDecimal offset0 = BigDecimal.valueOf(LocalTime.from(temporal).toNanoOfDay());
            BigDecimal offset1 = offset0.divide(precision, 0, mode).multiply(precision);
            Duration delta = Duration.ofNanos(offset1.subtract(offset0).longValue());
            return temporal.plus(delta);
        };
    }

    /**
     * <p>Returns a {@code TemporalAdjuster} that advances the given temporal by the
     * specified number of working days (Monday to Friday).</p>
     *
     * @param days the number of working days to add. If zero, no adjustment will be made.
     * @return a {@link TemporalAdjuster} that advances the temporal by the given number of working days
     */
    public static TemporalAdjuster workingDays(int days) {
        if (days == 0) return temporal -> temporal;
        int wdSize = DayOfWeek.FRIDAY.getValue() - DayOfWeek.MONDAY.getValue() + 1;
        return temporal -> {
            DayOfWeek day0 = DayOfWeek.from(temporal);
            Temporal floor = temporal.plus(Period.ofWeeks(Math.floorDiv(days, wdSize)));
            DayOfWeek dow = DayOfWeek.of((day0.ordinal() + Math.floorMod(days, wdSize)) % wdSize + 1);
            return floor.with(nextOrSame(dow));
        };
    }
}
