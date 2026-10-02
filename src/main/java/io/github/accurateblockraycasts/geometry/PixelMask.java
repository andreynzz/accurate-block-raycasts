package io.github.accurateblockraycasts.geometry;

import java.util.Arrays;
import java.util.Objects;

/**
 * An immutable 16 by 16 mask. A set bit represents a solid pixel; an unset bit
 * represents a passable pixel. Rows are indexed bottom-to-top and columns
 * left-to-right, both in the inclusive range {@code [0, 15]}.
 */
public final class PixelMask {
    public static final int SIZE = 16;
    private final long[] solidBits;

    private PixelMask(long[] solidBits) {
        this.solidBits = solidBits;
    }

    public static PixelMask empty() {
        return new PixelMask(new long[4]);
    }

    public static PixelMask solid() {
        long[] bits = new long[4];
        Arrays.fill(bits, -1L);
        return new PixelMask(bits);
    }

    public static PixelMask fromSolidPixels(boolean[][] solidPixels) {
        Objects.requireNonNull(solidPixels, "solidPixels");
        if (solidPixels.length != SIZE) {
            throw new IllegalArgumentException("expected exactly 16 rows");
        }

        long[] bits = new long[4];
        for (int row = 0; row < SIZE; row++) {
            if (solidPixels[row] == null || solidPixels[row].length != SIZE) {
                throw new IllegalArgumentException("each row must contain exactly 16 pixels");
            }
            for (int column = 0; column < SIZE; column++) {
                if (solidPixels[row][column]) {
                    set(bits, index(column, row));
                }
            }
        }
        return new PixelMask(bits);
    }

    /**
     * Builds a mask from sixteen bottom-to-top rows, using {@code '#'} for a
     * solid pixel and {@code '.'} for an opening.
     */
    public static PixelMask fromRows(java.util.List<String> rows) {
        Objects.requireNonNull(rows, "rows");
        if (rows.size() != SIZE) {
            throw new IllegalArgumentException("expected exactly 16 rows");
        }

        boolean[][] solidPixels = new boolean[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            String pattern = Objects.requireNonNull(rows.get(row), "mask row");
            if (pattern.length() != SIZE) {
                throw new IllegalArgumentException("each mask row must contain exactly 16 pixels");
            }
            for (int column = 0; column < SIZE; column++) {
                solidPixels[row][column] = switch (pattern.charAt(column)) {
                    case '#' -> true;
                    case '.' -> false;
                    default -> throw new IllegalArgumentException("mask rows may only contain '#' or '.'");
                };
            }
        }
        return fromSolidPixels(solidPixels);
    }

    public boolean isSolid(int column, int row) {
        int index = index(column, row);
        return (solidBits[index >>> 6] & (1L << (index & 63))) != 0;
    }

    public boolean isPassable(int column, int row) {
        return !isSolid(column, row);
    }

    private static void set(long[] bits, int index) {
        bits[index >>> 6] |= 1L << (index & 63);
    }

    private static int index(int column, int row) {
        if (column < 0 || column >= SIZE || row < 0 || row >= SIZE) {
            throw new IndexOutOfBoundsException("pixel coordinates must be in [0, 15]");
        }
        return row * SIZE + column;
    }
}
