package io.github.accurateblockraycasts.raycast;

import io.github.accurateblockraycasts.geometry.DoorMask;
import io.github.accurateblockraycasts.geometry.PixelMask;

/**
 * Manually verified opening mask shared by every oxidation and wax state of
 * the vanilla copper door. Rows are specified bottom-to-top.
 */
public final class CopperDoorMasks {
    private static final String SOLID = "################";
    private static final String SIX_OPENINGS = "#####......#####";
    private static final String FOUR_OPENINGS = "######....######";
    private static final String SPLIT_OPENING = "###.###..###.###";
    private static final String NARROW_CENTER = "###..######..###";
    private static final String WIDE_CENTER = "###...####...###";

    private static final DoorMask FULL_DOOR = DoorMask.fromHalves(
        PixelMask.solid(),
        mask(
            SOLID, SOLID, SOLID,
            SIX_OPENINGS,
            FOUR_OPENINGS,
            SPLIT_OPENING,
            NARROW_CENTER,
            WIDE_CENTER,
            WIDE_CENTER,
            NARROW_CENTER,
            SPLIT_OPENING,
            FOUR_OPENINGS,
            SIX_OPENINGS,
            SOLID, SOLID, SOLID
        )
    );

    private CopperDoorMasks() {
    }

    public static DoorMask fullDoor() {
        return FULL_DOOR;
    }

    private static PixelMask mask(String... rows) {
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
