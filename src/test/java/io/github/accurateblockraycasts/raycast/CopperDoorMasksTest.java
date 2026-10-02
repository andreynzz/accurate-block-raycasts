package io.github.accurateblockraycasts.raycast;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.accurateblockraycasts.geometry.DoorMask;
import org.junit.jupiter.api.Test;

class CopperDoorMasksTest {
    @Test
    void preservesTheVerifiedUpperWindowPattern() {
        DoorMask mask = CopperDoorMasks.fullDoor();

        assertTrue(mask.isSolid(0, 15));
        assertFalse(mask.isSolid(5, 19));
        assertFalse(mask.isSolid(6, 20));
        assertTrue(mask.isSolid(4, 21));
        assertFalse(mask.isSolid(3, 21));
        assertTrue(mask.isSolid(0, 21));
    }
}
