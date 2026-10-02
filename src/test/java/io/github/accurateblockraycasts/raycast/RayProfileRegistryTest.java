package io.github.accurateblockraycasts.raycast;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import net.minecraft.SharedConstants;
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
}
