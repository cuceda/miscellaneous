"""
This module provides an API adaptation of the `java.math` package from the Java SE specification,
offering Java developers to use a familiar interface in a Python environment.
"""
from collections import namedtuple
from decimal import Context, Decimal, Inexact
from enum import IntEnum, auto
from typing import override


class RoundingMode(IntEnum):
    """
    Specifies a rounding behavior for numerical operations capable of discarding precision.
    """
    UP = 0
    """Rounding mode to round away from zero."""
    DOWN = auto()
    """Rounding mode to round towards zero."""
    CEILING = auto()
    """Rounding mode to round towards positive infinity."""
    FLOOR = auto()
    """Rounding mode to round towards negative infinity."""
    HALF_UP = auto()
    """Rounding mode to round towards "nearest neighbor" unless both neighbors are equidistant,
    in which case round up."""
    HALF_DOWN = auto()
    """Rounding mode to round towards "nearest neighbor" unless both neighbors are equidistant,
    in which case round down."""
    HALF_EVEN = auto()
    """Rounding mode to round towards the "nearest neighbor" unless both neighbors are equidistant,
    in which case, round towards the even neighbor."""
    UNNECESSARY = auto()
    """Rounding mode to assert that the requested operation has an exact result,
    hence no rounding is necessary."""


_RM_TO_CONTEXT = [
    Context(traps=[Inexact]) if rm is RoundingMode.UNNECESSARY
    else Context(rounding=f"ROUND_{rm.name}")
    for rm in RoundingMode
]


BigDecimalTuple = namedtuple("BigDecimalTuple", "unscaled_value, scale")


class BigDecimal(Decimal):
    """
    Immutable, arbitrary-precision signed decimal numbers.A BigDecimal consists of
    an arbitrary precision integer unscaled value and a 32-bit integer scale. If zero or positive,
    the scale is the number of digits to the right of the decimal point. If negative,
    the unscaled value of the number is multiplied by ten to the power of the negation of the scale.
    The value of the number represented by the BigDecimal is therefore (unscaledValue × 10^-scale).
    """
    @override
    def as_tuple(self):
        """
        Returns a named tuple representation of this BigDecimal as:
        `BigDecimalTuple(unscaled_value, scale)`
        """
        sign, digits, exponent = super().as_tuple()
        unscaled_value = sum(digit * 10 ** i for i, digit in enumerate(reversed(digits)))
        return BigDecimalTuple(-unscaled_value if sign else unscaled_value, -exponent)

    def set_scale(self, new_scale, rounding=RoundingMode.UNNECESSARY):
        """
        Returns a BigDecimal whose scale is the specified value,
        and whose value is numerically equal to this BigDecimal's.
        Throws an ArithmeticError if this is not possible.

        Args:
            new_scale: Scale of the BigDecimal value to be returned.
            rounding: The rounding mode to apply. Defaults to `UNNECESSARY`.

        Raises:
            Inexact: If roundingMode==UNNECESSARY and the specified scaling operation
                would require rounding.
        """
        context = _RM_TO_CONTEXT[rounding]
        prototype = Decimal((0, (1,), -new_scale))
        return BigDecimal(self.quantize(prototype, context=context))
