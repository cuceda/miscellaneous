package io.github.cuceda.miscellaneous;

import lombok.NonNull;

/**
 * Utility methods for comparing {@link Comparable} values.
 */
public final class ComparableUtils {
    
    private ComparableUtils() {}
    
    /**
     * Returns whether {@code a} is less than {@code b}.
     *
     * @param a the left-hand value
     * @param b the right-hand value
     * @param <T> the comparable type
     * @return {@code true} if {@code a < b}; {@code false} otherwise
     */
    public static <T extends Comparable<T>> boolean lt(@NonNull T a, @NonNull T b) {
        return a.compareTo(b) < 0;
    }
    
    /**
     * Returns whether {@code a} is less than or equal to {@code b}.
     *
     * @param a the left-hand value
     * @param b the right-hand value
     * @param <T> the comparable type
     * @return {@code true} if {@code a <= b}; {@code false} otherwise
     */
    public static <T extends Comparable<T>> boolean lte(@NonNull T a, @NonNull T b) {
        return a.compareTo(b) <= 0;
    }
    
    /**
     * Returns whether {@code a} is equal to {@code b}.
     *
     * @param a the left-hand value
     * @param b the right-hand value
     * @param <T> the comparable type
     * @return {@code true} if {@code a == b}; {@code false} otherwise
     */
    public static <T extends Comparable<T>> boolean eq(@NonNull T a, @NonNull T b) {
        return a.compareTo(b) == 0;
    }
    
    /**
     * Returns whether {@code a} is greater than {@code b}.
     *
     * @param a the left-hand value
     * @param b the right-hand value
     * @param <T> the comparable type
     * @return {@code true} if {@code a > b}; {@code false} otherwise
     */
    public static <T extends Comparable<T>> boolean gt(@NonNull T a, @NonNull T b) {
        return a.compareTo(b) > 0;
    }
    
    /**
     * Returns whether {@code a} is greater than or equal to {@code b}.
     *
     * @param a the left-hand value
     * @param b the right-hand value
     * @param <T> the comparable type
     * @return {@code true} if {@code a >= b}; {@code false} otherwise
     */
    public static <T extends Comparable<T>> boolean gte(@NonNull T a, @NonNull T b) {
        return a.compareTo(b) >= 0;
    }
    
    /**
     * Returns the smaller of {@code a} and {@code b}.
     *
     * @param a the first value
     * @param b the second value
     * @param <T> the comparable type
     * @return {@code a} if it is less than or equal to {@code b}; otherwise {@code b}
     */
    public static <T extends Comparable<T>> T min(@NonNull T a, @NonNull T b) {
        return lte(a, b) ? a : b;
    }
    
    /**
     * Returns the larger of {@code a} and {@code b}.
     *
     * @param a the first value
     * @param b the second value
     * @param <T> the comparable type
     * @return {@code a} if it is greater than or equal to {@code b}; otherwise {@code b}
     */
    public static <T extends Comparable<T>> T max(@NonNull T a, @NonNull T b) {
        return gte(a, b) ? a : b;
    }
}
