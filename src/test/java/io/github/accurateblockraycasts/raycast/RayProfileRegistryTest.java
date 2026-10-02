package io.github.accurateblockraycasts.raycast;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    void resolvesProfilesOnlyForVerifiedDoorsWithOpenings() {
        assertNotNull(RayProfileRegistry.INSTANCE.resolve(Blocks.ACACIA_DOOR.defaultBlockState()));
        assertNotNull(RayProfileRegistry.INSTANCE.resolve(Blocks.BAMBOO_DOOR.defaultBlockState()));
        assertNotNull(RayProfileRegistry.INSTANCE.resolve(Blocks.CHERRY_DOOR.defaultBlockState()));
        assertNotNull(RayProfileRegistry.INSTANCE.resolve(Blocks.COPPER_DOOR.weathering().unaffected().defaultBlockState()));
        assertNotNull(RayProfileRegistry.INSTANCE.resolve(Blocks.COPPER_DOOR.weathering().exposed().defaultBlockState()));
        assertNotNull(RayProfileRegistry.INSTANCE.resolve(Blocks.IRON_DOOR.defaultBlockState()));
        assertNotNull(RayProfileRegistry.INSTANCE.resolve(Blocks.JUNGLE_DOOR.defaultBlockState()));
        assertNotNull(RayProfileRegistry.INSTANCE.resolve(Blocks.POPLAR_DOOR.defaultBlockState()));
        assertNotNull(RayProfileRegistry.INSTANCE.resolve(Blocks.COPPER_DOOR.weathering().weathered().defaultBlockState()));
        assertNotNull(RayProfileRegistry.INSTANCE.resolve(Blocks.COPPER_DOOR.weathering().oxidized().defaultBlockState()));
        assertNotNull(RayProfileRegistry.INSTANCE.resolve(Blocks.COPPER_DOOR.waxed().unaffected().defaultBlockState()));
        assertNotNull(RayProfileRegistry.INSTANCE.resolve(Blocks.COPPER_DOOR.waxed().exposed().defaultBlockState()));
        assertNotNull(RayProfileRegistry.INSTANCE.resolve(Blocks.COPPER_DOOR.waxed().weathered().defaultBlockState()));
        assertNotNull(RayProfileRegistry.INSTANCE.resolve(Blocks.COPPER_DOOR.waxed().oxidized().defaultBlockState()));
        assertNull(RayProfileRegistry.INSTANCE.resolve(Blocks.BIRCH_DOOR.defaultBlockState()));
        assertNull(RayProfileRegistry.INSTANCE.resolve(Blocks.CRIMSON_DOOR.defaultBlockState()));
        assertNull(RayProfileRegistry.INSTANCE.resolve(Blocks.SPRUCE_DOOR.defaultBlockState()));
    }

    @Test
    void resolvesProfilesOnlyForVerifiedTrapdoorsWithOpenings() {
        assertNotNull(RayProfileRegistry.INSTANCE.resolve(Blocks.ACACIA_TRAPDOOR.defaultBlockState()));
        assertNotNull(RayProfileRegistry.INSTANCE.resolve(Blocks.BAMBOO_TRAPDOOR.defaultBlockState()));
        assertNotNull(RayProfileRegistry.INSTANCE.resolve(Blocks.CHERRY_TRAPDOOR.defaultBlockState()));
        assertNotNull(RayProfileRegistry.INSTANCE.resolve(Blocks.CRIMSON_TRAPDOOR.defaultBlockState()));
        assertNotNull(RayProfileRegistry.INSTANCE.resolve(Blocks.JUNGLE_TRAPDOOR.defaultBlockState()));
        assertNotNull(RayProfileRegistry.INSTANCE.resolve(Blocks.MANGROVE_TRAPDOOR.defaultBlockState()));
        assertNotNull(RayProfileRegistry.INSTANCE.resolve(Blocks.OAK_TRAPDOOR.defaultBlockState()));
        assertNotNull(RayProfileRegistry.INSTANCE.resolve(Blocks.POPLAR_TRAPDOOR.defaultBlockState()));
        assertNotNull(RayProfileRegistry.INSTANCE.resolve(Blocks.WARPED_TRAPDOOR.defaultBlockState()));

        assertNull(RayProfileRegistry.INSTANCE.resolve(Blocks.BIRCH_TRAPDOOR.defaultBlockState()));
        assertNull(RayProfileRegistry.INSTANCE.resolve(Blocks.DARK_OAK_TRAPDOOR.defaultBlockState()));
        assertNull(RayProfileRegistry.INSTANCE.resolve(Blocks.IRON_TRAPDOOR.defaultBlockState()));
        assertNull(RayProfileRegistry.INSTANCE.resolve(Blocks.PALE_OAK_TRAPDOOR.defaultBlockState()));
        assertNull(RayProfileRegistry.INSTANCE.resolve(Blocks.SPRUCE_TRAPDOOR.defaultBlockState()));
    }

    @Test
    void delegatesOpeningChecksOnlyToResolvedProfiles() {
        BlockPos position = BlockPos.ZERO;
        Vec3 opening = new Vec3(0.25, 1.4, 0.5);

        assertTrue(RayProfileRegistry.INSTANCE.isPassableAt(Blocks.OAK_DOOR.defaultBlockState(), position, opening));
        assertFalse(RayProfileRegistry.INSTANCE.isPassableAt(Blocks.STONE.defaultBlockState(), position, opening));
    }
}
