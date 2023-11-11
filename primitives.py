from enum import Enum, auto, property


class IntType(Enum):
    """One of the Java primitive integer types."""

    BYTE = auto()
    SHORT = auto()
    INT = auto()
    LONG = auto()

    def __init__(self, value):
        size = 2 ** (value - 1)
        limit = 2 ** (8 * size - 1)
        self._size, self._limit = size, limit

    @property
    def size(self):
        """The fixed memory size this type takes up in bytes."""
        return self._size

    def numbers(self):
        """Returns the range of all the integer values this type can represent."""
        return range(-self._limit, self._limit)

    def min_max_values(self):
        """Returns the minimum and maximum integer values this type can represent."""
        numbers = self.numbers()
        return numbers[0], numbers[-1]
