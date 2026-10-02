package io.github.accurateblockraycasts.raycast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.accurateblockraycasts.geometry.Ray;
import io.github.accurateblockraycasts.geometry.Vec3;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TrapdoorRayProfileTest {
    @BeforeAll
    static void bootstrapVanillaRegistries() { SharedConstants.tryDetectVersion(); Bootstrap.bootStrap(); }

    @Test
    void samplesOpeningsAndSolidPixelsWithoutRegisteringTheBlock() {
        boolean[][] pixels = new boolean[16][16];
        pixels[8][8] = true;
        TrapdoorRayProfile profile = new TrapdoorRayProfile(Blocks.OAK_TRAPDOOR, io.github.accurateblockraycasts.geometry.PixelMask.fromSolidPixels(pixels));
        BlockPos position = BlockPos.ZERO;
        var state = Blocks.OAK_TRAPDOOR.defaultBlockState();

        assertEquals(RayProfileResult.OPEN, profile.evaluate(state, position, new Ray(new Vec3(0.25, -1.0, 0.25), new Vec3(0.25, 1.0, 0.25))));
        assertEquals(RayProfileResult.SOLID, profile.evaluate(state, position, new Ray(new Vec3(0.5, -1.0, 0.5), new Vec3(0.5, 1.0, 0.5))));
    }
}
