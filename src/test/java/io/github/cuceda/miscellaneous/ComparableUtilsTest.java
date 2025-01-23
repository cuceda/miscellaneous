package io.github.cuceda.miscellaneous;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ComparableUtilsTest implements WithAssertions {

    @Nested
    class LessThan {

        @Test
        void returnsTrueWhenLeftValueIsLessThanRightValue() {
            assertThat(ComparableUtils.lt(1, 2)).as("1 < 2").isTrue();
        }

        @Test
        void returnsFalseWhenLeftValueIsEqualToRightValue() {
            assertThat(ComparableUtils.lt(2, 2)).as("2 < 2").isFalse();
        }

        @Test
        void returnsFalseWhenLeftValueIsGreaterThanRightValue() {
            assertThat(ComparableUtils.lt(3, 2)).as("3 < 2").isFalse();
        }
    }

    @Nested
    class LessThanOrEqualTo {

        @Test
        void returnsTrueWhenLeftValueIsLessThanRightValue() {
            assertThat(ComparableUtils.lte(1, 2)).as("1 ≤ 2").isTrue();
        }

        @Test
        void returnsTrueWhenValuesAreEqual() {
            assertThat(ComparableUtils.lte(2, 2)).as("2 ≤ 2").isTrue();
        }

        @Test
        void returnsFalseWhenLeftValueIsGreaterThanRightValue() {
            assertThat(ComparableUtils.lte(3, 2)).as("3 ≤ 2").isFalse();
        }
    }

    @Nested
    class EqualTo {

        @Test
        void returnsTrueWhenValuesAreEqual() {
            assertThat(ComparableUtils.eq(2, 2)).as("2 = 2").isTrue();
        }

        @Test
        void returnsFalseWhenValuesAreDifferent() {
            assertThat(ComparableUtils.eq(2, 3)).as("2 = 3").isFalse();
        }
    }

    @Nested
    class GreaterThan {

        @Test
        void returnsTrueWhenLeftValueIsGreaterThanRightValue() {
            assertThat(ComparableUtils.gt(3, 2)).as("3 > 2").isTrue();
        }

        @Test
        void returnsFalseWhenValuesAreEqual() {
            assertThat(ComparableUtils.gt(2, 2)).as("2 > 2").isFalse();
        }

        @Test
        void returnsFalseWhenLeftValueIsLessThanRightValue() {
            assertThat(ComparableUtils.gt(1, 2)).as("1 > 2").isFalse();
        }
    }

    @Nested
    class GreaterThanOrEqualTo {

        @Test
        void returnsTrueWhenLeftValueIsGreaterThanRightValue() {
            assertThat(ComparableUtils.gte(3, 2)).as("3 ≥ 2").isTrue();
        }

        @Test
        void returnsTrueWhenValuesAreEqual() {
            assertThat(ComparableUtils.gte(2, 2)).as("2 ≥ 2").isTrue();
        }

        @Test
        void returnsFalseWhenLeftValueIsLessThanRightValue() {
            assertThat(ComparableUtils.gte(1, 2)).as("1 ≥ 2").isFalse();
        }
    }

    @Nested
    class Minimum {

        @Test
        void returnsLeftValueWhenItIsSmaller() {
            assertThat(ComparableUtils.min(1, 2)).as("min(1, 2)").isEqualTo(1);
        }

        @Test
        void returnsLeftValueWhenValuesAreEqual() {
            assertThat(ComparableUtils.min(2, 2)).as("min(2, 2)").isEqualTo(2);
        }

        @Test
        void returnsRightValueWhenItIsSmaller() {
            assertThat(ComparableUtils.min(3, 2)).as("min(3, 2)").isEqualTo(2);
        }
    }

    @Nested
    class Maximum {

        @Test
        void returnsLeftValueWhenItIsLarger() {
            assertThat(ComparableUtils.max(3, 2)).as("max(3, 2)").isEqualTo(3);
        }

        @Test
        void returnsLeftValueWhenValuesAreEqual() {
            assertThat(ComparableUtils.max(2, 2)).as("max(2, 2)").isEqualTo(2);
        }

        @Test
        void returnsRightValueWhenItIsLarger() {
            assertThat(ComparableUtils.max(1, 2)).as("max(1, 2)").isEqualTo(2);
        }
    }
}
