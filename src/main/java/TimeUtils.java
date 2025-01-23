import lombok.NonNull;

import java.time.*;
import java.time.temporal.*;

public final class TimeUtils {

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
}
