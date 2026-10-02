package io.github.accurateblockraycasts.raycast;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.accurateblockraycasts.geometry.DoorMask;
import io.github.accurateblockraycasts.geometry.PixelMask;
import org.junit.jupiter.api.Test;

class OakDoorMasksTest {
    @Test
    void lowerHalfIsCompletelySolid() {
        PixelMask lowerHalf = OakDoorMasks.lowerHalf();

        for (int row = 0; row < PixelMask.SIZE; row++) {
            for (int column = 0; column < PixelMask.SIZE; column++) {
                assertTrue(lowerHalf.isSolid(column, row));
            }
        }
    }

    @Test
    void upperHalfContainsOnlyTheFourVerifiedWindows() {
        PixelMask upperHalf = OakDoorMasks.upperHalf();

        for (int row = 0; row < PixelMask.SIZE; row++) {
            for (int column = 0; column < PixelMask.SIZE; column++) {
                assertTrue(upperHalf.isPassable(column, row) == isWindow(column, row));
            }
        }
    }

    @Test
    void upperHalfKeepsTheWindowFramesSolid() {
        PixelMask upperHalf = OakDoorMasks.upperHalf();

        assertFalse(upperHalf.isPassable(2, 5));
        assertFalse(upperHalf.isPassable(7, 5));
        assertFalse(upperHalf.isPassable(8, 5));
        assertFalse(upperHalf.isPassable(13, 5));
        assertFalse(upperHalf.isPassable(3, 4));
        assertFalse(upperHalf.isPassable(3, 8));
    }

    @Test
    void fullDoorUsesTheSameContinuousBottomToTopCoordinates() {
        DoorMask fullDoor = OakDoorMasks.fullDoor();

        assertTrue(fullDoor.isSolid(3, 15));
        assertTrue(fullDoor.isPassable(3, 21));
        assertTrue(fullDoor.isPassable(12, 28));
        assertTrue(fullDoor.isSolid(3, 24));
    }

    private static boolean isWindow(int column, int row) {
        boolean withinColumns = (column >= 3 && column <= 6) || (column >= 9 && column <= 12);
        boolean withinRows = (row >= 5 && row <= 7) || (row >= 10 && row <= 12);
        return withinColumns && withinRows;
    }
}
