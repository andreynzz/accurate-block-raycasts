package io.github.accurateblockraycasts.raycast;

import io.github.accurateblockraycasts.geometry.PixelMask;

/**
 * Server-side blocking patterns for the vanilla 26.3 oak door.
 *
 * <p>These masks intentionally encode only the manually verified opening
 * geometry: the lower half is solid and the upper half has four 4-by-3-pixel
 * openings. They are not derived from, nor do they load, client assets at
 * runtime. Rows passed to {@link PixelMask} run bottom-to-top.
 */
public final class OakDoorMasks {
    private static final String SOLID = "################";
    private static final String OPENINGS = "###....##....###";

    private static final PixelMask LOWER_HALF = PixelMask.solid();
    private static final PixelMask UPPER_HALF = fromRows(
        SOLID,
        SOLID,
        SOLID,
        SOLID,
        SOLID,
        OPENINGS,
        OPENINGS,
        OPENINGS,
        SOLID,
        SOLID,
        OPENINGS,
        OPENINGS,
        OPENINGS,
        SOLID,
        SOLID,
        SOLID
    );

    private OakDoorMasks() {
    }

    public static PixelMask lowerHalf() {
        return LOWER_HALF;
    }

    public static PixelMask upperHalf() {
        return UPPER_HALF;
    }

    private static PixelMask fromRows(String... rows) {
        boolean[][] solidPixels = new boolean[PixelMask.SIZE][PixelMask.SIZE];
        for (int row = 0; row < PixelMask.SIZE; row++) {
            String pattern = rows[row];
            if (pattern.length() != PixelMask.SIZE) {
                throw new IllegalArgumentException("each mask row must contain exactly 16 pixels");
            }
            for (int column = 0; column < PixelMask.SIZE; column++) {
                solidPixels[row][column] = switch (pattern.charAt(column)) {
                    case '#' -> true;
                    case '.' -> false;
                    default -> throw new IllegalArgumentException("mask rows may only contain '#' or '.'");
                };
            }
        }
        return PixelMask.fromSolidPixels(solidPixels);
    }
}
