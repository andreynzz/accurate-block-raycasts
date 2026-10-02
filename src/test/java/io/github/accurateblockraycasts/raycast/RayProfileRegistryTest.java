package io.github.accurateblockraycasts.raycast;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.accurateblockraycasts.geometry.Vec3;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RayProfileRegistryTest {
    @BeforeAll
    static void bootstrapVanillaRegistries() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void resolvesTheOakDoorSingletonForEveryOakDoorState() {
        assertSame(OakDoorRayProfile.INSTANCE, RayProfileRegistry.INSTANCE.resolve(Blocks.OAK_DOOR.defaultBlockState()));
    }

    @Test
    void doesNotResolveProfilesForUnsupportedBlocks() {
        assertNull(RayProfileRegistry.INSTANCE.resolve(Blocks.STONE.defaultBlockState()));
    }

    @Test
    void delegatesOpeningChecksOnlyToResolvedProfiles() {
        BlockPos position = BlockPos.ZERO;
        Vec3 opening = new Vec3(0.25, 1.4, 0.5);

        assertTrue(RayProfileRegistry.INSTANCE.isPassableAt(Blocks.OAK_DOOR.defaultBlockState(), position, opening));
        assertFalse(RayProfileRegistry.INSTANCE.isPassableAt(Blocks.STONE.defaultBlockState(), position, opening));
    }
}
