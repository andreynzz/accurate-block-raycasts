package io.github.accurateblockraycasts.geometry;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DoorMaskTest {
    @Test
    void composesLowerAndUpperMasksIntoOneContinuousSurface() {
        DoorMask mask = DoorMask.fromHalves(maskWithSolidPixel(0, 0), maskWithSolidPixel(15, 15));

        assertTrue(mask.isSolid(0, 0));
        assertFalse(mask.isSolid(15, 0));
        assertFalse(mask.isSolid(0, 16));
        assertTrue(mask.isSolid(15, 31));
    }

    @Test
    void rejectsCoordinatesOutsideTheFullDoorSurface() {
        DoorMask mask = DoorMask.fromHalves(PixelMask.empty(), PixelMask.empty());

        assertThrows(IndexOutOfBoundsException.class, () -> mask.isSolid(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> mask.isSolid(16, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> mask.isSolid(0, -1));
        assertThrows(IndexOutOfBoundsException.class, () -> mask.isSolid(0, 32));
    }

    private static PixelMask maskWithSolidPixel(int column, int row) {
        boolean[][] pixels = new boolean[PixelMask.SIZE][PixelMask.SIZE];
        pixels[row][column] = true;
        return PixelMask.fromSolidPixels(pixels);
    }
}
