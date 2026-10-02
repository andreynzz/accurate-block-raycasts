package io.github.accurateblockraycasts.geometry;

import java.util.Objects;

/**
 * An immutable 16-by-32 door surface assembled from lower and upper masks.
 *
 * <p>Rows are indexed bottom-to-top: rows {@code [0, 15]} belong to the
 * lower block and rows {@code [16, 31]} belong to the upper block. The two
 * immutable half masks are retained directly, avoiding a duplicate 512-bit
 * representation for every door profile.
 */
public final class DoorMask {
    public static final int WIDTH = PixelMask.SIZE;
    public static final int HEIGHT = PixelMask.SIZE * 2;

    private final PixelMask lowerHalf;
    private final PixelMask upperHalf;

    private DoorMask(PixelMask lowerHalf, PixelMask upperHalf) {
        this.lowerHalf = lowerHalf;
        this.upperHalf = upperHalf;
    }

    public static DoorMask fromHalves(PixelMask lowerHalf, PixelMask upperHalf) {
        return new DoorMask(
            Objects.requireNonNull(lowerHalf, "lowerHalf"),
            Objects.requireNonNull(upperHalf, "upperHalf")
        );
    }

    public boolean isSolid(int column, int row) {
        checkCoordinates(column, row);
        return row < PixelMask.SIZE
            ? lowerHalf.isSolid(column, row)
            : upperHalf.isSolid(column, row - PixelMask.SIZE);
    }

    public boolean isPassable(int column, int row) {
        return !isSolid(column, row);
    }

    private static void checkCoordinates(int column, int row) {
        if (column < 0 || column >= WIDTH || row < 0 || row >= HEIGHT) {
            throw new IndexOutOfBoundsException("door-mask coordinates must be in [0, 15] by [0, 31]");
        }
    }
}
