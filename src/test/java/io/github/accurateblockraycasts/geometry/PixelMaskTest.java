package io.github.accurateblockraycasts.geometry;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PixelMaskTest {
    @Test
    void samplesSolidAndPassablePixels() {
        boolean[][] pixels = new boolean[PixelMask.SIZE][PixelMask.SIZE];
        pixels[0][0] = true;
        pixels[15][15] = true;
        PixelMask mask = PixelMask.fromSolidPixels(pixels);

        assertTrue(mask.isSolid(0, 0));
        assertTrue(mask.isSolid(15, 15));
        assertTrue(mask.isPassable(8, 8));
    }

    @Test
    void rejectsOutOfBoundsSamples() {
        PixelMask mask = PixelMask.empty();

        assertThrows(IndexOutOfBoundsException.class, () -> mask.isSolid(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> mask.isSolid(0, 16));
    }

    @Test
    void factoryValidatesDimensionsAndCopiesInput() {
        assertThrows(IllegalArgumentException.class, () -> PixelMask.fromSolidPixels(new boolean[15][16]));
        assertThrows(IllegalArgumentException.class, () -> PixelMask.fromSolidPixels(new boolean[16][15]));

        boolean[][] pixels = new boolean[16][16];
        PixelMask mask = PixelMask.fromSolidPixels(pixels);
        pixels[0][0] = true;
        assertTrue(mask.isPassable(0, 0));
    }
}
