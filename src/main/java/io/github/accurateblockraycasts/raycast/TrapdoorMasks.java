package io.github.accurateblockraycasts.raycast;

import io.github.accurateblockraycasts.geometry.PixelMask;

/**
 * Manually verified server-side opening masks for the supported vanilla
 * trapdoors. Rows are written bottom-to-top, matching {@link PixelMask}.
 *
 * <p>These are static gameplay data transcribed from the vanilla 26.3 visual
 * design. No client assets, models, or textures are accessed at runtime.
 */
public final class TrapdoorMasks {
    private static final String SOLID = "################";

    private static final PixelMask ACACIA = mask(
        SOLID, SOLID, SOLID,
        "##..###..###..##", "##..###..###..##", "##..###..###..##", "##..###..###..##",
        "##..###..###..##", "##..###..###..##", "##..###..###..##", "##..###..###..##",
        "##..###..###..##", "##..###..###..##",
        SOLID, SOLID, SOLID
    );
    private static final PixelMask BAMBOO = mask(
        SOLID, SOLID, SOLID,
        "###.#.#..#.#.###", SOLID, "###.#.#..#.#.###", "#######..#######",
        "###.#......#.###", "###.#......#.###", "#######..#######", "###.#.#..#.#.###",
        SOLID, "###.#.#..#.#.###", SOLID, SOLID, SOLID
    );
    private static final PixelMask CHERRY = mask(
        SOLID, SOLID, SOLID, SOLID,
        "####..#..#..####", "####..#..#..####", SOLID,
        "####..#..#..####", "####..#..#..####", SOLID,
        "####..#..#..####", "####..#..#..####",
        SOLID, SOLID, SOLID, SOLID
    );
    private static final PixelMask CRIMSON = mask(
        SOLID, SOLID, SOLID,
        "###..........###", SOLID, SOLID, SOLID,
        "###..........###", "###..........###", SOLID, SOLID, SOLID,
        "###..........###", SOLID, SOLID, SOLID
    );
    private static final PixelMask JUNGLE = mask(
        SOLID, SOLID, SOLID,
        "#####.#..#.#####", "####..#..#..####", "####..#..#..####",
        SOLID, SOLID,
        "###...#..#...###", "###...#..#...###", "####..#..#..####", "#####.#..#.#####",
        SOLID, SOLID, SOLID, SOLID
    );
    private static final PixelMask MANGROVE = mask(
        SOLID, SOLID, SOLID, SOLID, SOLID,
        "######....######", "#####......#####", "#####......#####", "#####......#####", "#####......#####", "######....######",
        SOLID, SOLID, SOLID, SOLID, SOLID
    );
    private static final PixelMask OAK = mask(
        SOLID, SOLID, SOLID,
        "###...####...###", "###...####...###", "###...####...###",
        SOLID, SOLID, SOLID, SOLID,
        "###...####...###", "###...####...###", "###...####...###",
        SOLID, SOLID, SOLID
    );
    // Iron uses the same verified opening layout as oak, but has its own profile.
    private static final PixelMask IRON = OAK;
    // All weathering and wax states use this same verified copper opening layout.
    private static final PixelMask COPPER = mask(
        SOLID, SOLID, SOLID,
        "#####......#####", "######....######", "###.###..###.###", "###..######..###",
        "###...#..#...###", "###...#..#...###", "###..######..###", "###.###..###.###",
        "######....######", "#####......#####",
        SOLID, SOLID, SOLID
    );
    private static final PixelMask POPLAR = mask(
        SOLID, SOLID, SOLID, SOLID, SOLID,
        "#######..#######", "######....######", "#####......#####", "#####......#####", "######....######", "#######..#######",
        SOLID, SOLID, SOLID, SOLID, SOLID
    );
    private static final PixelMask WARPED = mask(
        SOLID, SOLID, SOLID,
        "###.##..###..###", "###.###..#...###", "###..##..##.####", "####..#..##.####",
        "####..#...#.####", "###..##...#.####", "###.###...#.####", "###.##...##..###",
        "###.##...##..###", "###.###..#.#.###",
        SOLID, SOLID, SOLID
    );

    private TrapdoorMasks() {
    }

    public static PixelMask acacia() { return ACACIA; }
    public static PixelMask bamboo() { return BAMBOO; }
    public static PixelMask cherry() { return CHERRY; }
    public static PixelMask crimson() { return CRIMSON; }
    public static PixelMask jungle() { return JUNGLE; }
    public static PixelMask mangrove() { return MANGROVE; }
    public static PixelMask oak() { return OAK; }
    public static PixelMask iron() { return IRON; }
    public static PixelMask copper() { return COPPER; }
    public static PixelMask poplar() { return POPLAR; }
    public static PixelMask warped() { return WARPED; }

    private static PixelMask mask(String... rows) {
        if (rows.length != PixelMask.SIZE) {
            throw new IllegalArgumentException("expected exactly 16 rows");
        }
        boolean[][] solidPixels = new boolean[PixelMask.SIZE][PixelMask.SIZE];
        for (int row = 0; row < PixelMask.SIZE; row++) {
            if (rows[row].length() != PixelMask.SIZE) {
                throw new IllegalArgumentException("each mask row must contain exactly 16 pixels");
            }
            for (int column = 0; column < PixelMask.SIZE; column++) {
                solidPixels[row][column] = switch (rows[row].charAt(column)) {
                    case '#' -> true;
                    case '.' -> false;
                    default -> throw new IllegalArgumentException("mask rows may only contain '#' or '.'");
                };
            }
        }
        return PixelMask.fromSolidPixels(solidPixels);
    }
}
