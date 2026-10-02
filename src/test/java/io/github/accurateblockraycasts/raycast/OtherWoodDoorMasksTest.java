package io.github.accurateblockraycasts.raycast;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.accurateblockraycasts.geometry.DoorMask;
import org.junit.jupiter.api.Test;

class OtherWoodDoorMasksTest {
    @Test
    void preservesVerifiedOpeningsAndSolidPixelsForEachSupportedDoor() {
        assertOpeningAndSolid(OtherWoodDoorMasks.acacia(), 3, 3, 0, 3);
        assertOpeningAndSolid(OtherWoodDoorMasks.bamboo(), 6, 17, 0, 17);
        assertOpeningAndSolid(OtherWoodDoorMasks.cherry(), 5, 0, 0, 0);
        assertOpeningAndSolid(OtherWoodDoorMasks.jungle(), 7, 19, 0, 19);
        assertOpeningAndSolid(OtherWoodDoorMasks.poplar(), 7, 20, 0, 20);
    }

    private static void assertOpeningAndSolid(DoorMask mask, int openingColumn, int openingRow, int solidColumn, int solidRow) {
        assertFalse(mask.isSolid(openingColumn, openingRow));
        assertTrue(mask.isSolid(solidColumn, solidRow));
    }
}
