package io.github.accurateblockraycasts.raycast;

import io.github.accurateblockraycasts.geometry.DoorMask;
import io.github.accurateblockraycasts.geometry.PixelMask;

/**
 * Manually verified server-side opening masks for vanilla wood and nether
 * doors other than oak. Rows are specified bottom-to-top.
 *
 * <p>These are static gameplay data, not runtime texture or model reads.
 */
public final class OtherWoodDoorMasks {
    private static final String SOLID = "################";
    private static final String ACACIA_OPENINGS = "###..##..##..###";
    private static final String BAMBOO_WIDE_OPENING = "####.#....#.####";
    private static final String BAMBOO_NARROW_OPENING = "######....######";
    private static final String CHERRY_SMALL_OPENING = "#####.####.#####";
    private static final String CHERRY_MEDIUM_OPENING = "####...##...####";
    private static final String CHERRY_TWO_OPENINGS = "#######..#######";
    private static final String JUNGLE_TWO_OPENINGS = "#######..#######";
    private static final String JUNGLE_SPLIT_OPENING = "#####.#..#.#####";
    private static final String JUNGLE_THREE_OPENINGS = "####..#..#..####";
    private static final String JUNGLE_WIDE_OPENING = "######....######";
    private static final String POPLAR_TWO_OPENINGS = "#######..#######";
    private static final String POPLAR_FOUR_OPENINGS = "######....######";
    private static final String POPLAR_SIX_OPENINGS = "#####......#####";
    private static final String POPLAR_EIGHT_OPENINGS = "####........####";

    private static final DoorMask ACACIA = DoorMask.fromHalves(
        mask(
            SOLID, SOLID, SOLID,
            ACACIA_OPENINGS, ACACIA_OPENINGS, ACACIA_OPENINGS, ACACIA_OPENINGS,
            ACACIA_OPENINGS, ACACIA_OPENINGS, ACACIA_OPENINGS, ACACIA_OPENINGS,
            ACACIA_OPENINGS, ACACIA_OPENINGS, ACACIA_OPENINGS, ACACIA_OPENINGS,
            SOLID
        ),
        mask(
            SOLID,
            ACACIA_OPENINGS, ACACIA_OPENINGS, ACACIA_OPENINGS, ACACIA_OPENINGS,
            ACACIA_OPENINGS, ACACIA_OPENINGS, ACACIA_OPENINGS, ACACIA_OPENINGS,
            ACACIA_OPENINGS, ACACIA_OPENINGS, ACACIA_OPENINGS, ACACIA_OPENINGS,
            SOLID, SOLID, SOLID
        )
    );

    private static final DoorMask BAMBOO = DoorMask.fromHalves(
        PixelMask.solid(),
        mask(
            SOLID,
            BAMBOO_WIDE_OPENING,
            SOLID,
            BAMBOO_WIDE_OPENING,
            BAMBOO_NARROW_OPENING,
            BAMBOO_WIDE_OPENING,
            BAMBOO_NARROW_OPENING,
            BAMBOO_WIDE_OPENING,
            BAMBOO_NARROW_OPENING,
            BAMBOO_WIDE_OPENING,
            SOLID,
            BAMBOO_WIDE_OPENING,
            SOLID, SOLID, SOLID, SOLID
        )
    );

    private static final DoorMask CHERRY = DoorMask.fromHalves(
        mask(
            CHERRY_SMALL_OPENING,
            SOLID, SOLID, SOLID, SOLID, SOLID, SOLID, SOLID,
            SOLID, SOLID, SOLID, SOLID, SOLID, SOLID, SOLID, SOLID
        ),
        mask(
            CHERRY_MEDIUM_OPENING,
            CHERRY_SMALL_OPENING,
            CHERRY_TWO_OPENINGS,
            CHERRY_TWO_OPENINGS,
            CHERRY_SMALL_OPENING,
            CHERRY_MEDIUM_OPENING,
            CHERRY_SMALL_OPENING,
            CHERRY_TWO_OPENINGS,
            CHERRY_TWO_OPENINGS,
            CHERRY_SMALL_OPENING,
            CHERRY_MEDIUM_OPENING,
            CHERRY_SMALL_OPENING,
            SOLID, SOLID, SOLID, SOLID
        )
    );

    private static final DoorMask JUNGLE = DoorMask.fromHalves(
        PixelMask.solid(),
        mask(
            SOLID, SOLID, SOLID,
            JUNGLE_TWO_OPENINGS,
            JUNGLE_WIDE_OPENING,
            SOLID, SOLID,
            JUNGLE_THREE_OPENINGS,
            JUNGLE_THREE_OPENINGS,
            JUNGLE_THREE_OPENINGS,
            JUNGLE_SPLIT_OPENING,
            JUNGLE_TWO_OPENINGS,
            SOLID, SOLID, SOLID, SOLID
        )
    );

    private static final DoorMask POPLAR = DoorMask.fromHalves(
        PixelMask.solid(),
        mask(
            SOLID, SOLID, SOLID, SOLID,
            POPLAR_TWO_OPENINGS,
            POPLAR_FOUR_OPENINGS,
            POPLAR_SIX_OPENINGS,
            POPLAR_EIGHT_OPENINGS,
            POPLAR_EIGHT_OPENINGS,
            POPLAR_SIX_OPENINGS,
            POPLAR_FOUR_OPENINGS,
            POPLAR_TWO_OPENINGS,
            SOLID, SOLID, SOLID, SOLID
        )
    );

    private OtherWoodDoorMasks() {
    }

    public static DoorMask acacia() {
        return ACACIA;
    }

    public static DoorMask bamboo() {
        return BAMBOO;
    }

    public static DoorMask cherry() {
        return CHERRY;
    }

    public static DoorMask jungle() {
        return JUNGLE;
    }

    public static DoorMask poplar() {
        return POPLAR;
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
