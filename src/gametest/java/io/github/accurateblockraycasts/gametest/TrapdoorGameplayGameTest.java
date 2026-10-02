package io.github.accurateblockraycasts.gametest;

import io.github.accurateblockraycasts.geometry.Ray;
import io.github.accurateblockraycasts.raycast.RayProfile;
import io.github.accurateblockraycasts.raycast.RayProfileRegistry;
import io.github.accurateblockraycasts.raycast.RayProfileResult;
import java.lang.reflect.Method;
import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.Vec3;

/** Server gameplay coverage for representative wooden, iron, and copper trapdoors. */
public final class TrapdoorGameplayGameTest implements CustomTestMethodInvoker {
    private static final BlockPos TRAPDOOR = new BlockPos(3, 2, 3);
    private static final double SOLID_SAMPLE = 1.0 / 32.0;

    @GameTest(structure = "fabric-gametest-api-v1:empty", maxTicks = 20, padding = 10)
    public void arrowPassesThroughAcaciaTrapdoorOpening(GameTestHelper helper) {
        arrowPassesThroughOpeningAndHitsLaterBlock(helper, Blocks.ACACIA_TRAPDOOR, 2.5 / 16.0, 3.5 / 16.0);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty", maxTicks = 20, padding = 10)
    public void arrowPassesThroughIronTrapdoorOpening(GameTestHelper helper) {
        arrowPassesThroughOpeningAndHitsLaterBlock(helper, Blocks.IRON_TRAPDOOR, 3.5 / 16.0, 3.5 / 16.0);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty", maxTicks = 20, padding = 10)
    public void arrowPassesThroughCopperTrapdoorOpening(GameTestHelper helper) {
        arrowPassesThroughOpeningAndHitsLaterBlock(helper, Blocks.COPPER_TRAPDOOR.weathering().unaffected(), 5.5 / 16.0, 3.5 / 16.0);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty", maxTicks = 20, padding = 10)
    public void arrowStopsAtSolidAcaciaTrapdoorPixel(GameTestHelper helper) {
        arrowStopsAtSolidTrapdoorPixel(helper, Blocks.ACACIA_TRAPDOOR);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty", maxTicks = 20, padding = 10)
    public void arrowStopsAtSolidIronTrapdoorPixel(GameTestHelper helper) {
        arrowStopsAtSolidTrapdoorPixel(helper, Blocks.IRON_TRAPDOOR);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty", maxTicks = 20, padding = 10)
    public void arrowStopsAtSolidCopperTrapdoorPixel(GameTestHelper helper) {
        arrowStopsAtSolidTrapdoorPixel(helper, Blocks.COPPER_TRAPDOOR.weathering().unaffected());
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty", padding = 10)
    public void skeletonCanSeeMockPlayerThroughAcaciaTrapdoorOpening(GameTestHelper helper) {
        skeletonCanSeeMockPlayerThroughOpening(helper, Blocks.ACACIA_TRAPDOOR, 2.5 / 16.0, 3.5 / 16.0);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty", padding = 10)
    public void skeletonCanSeeMockPlayerThroughIronTrapdoorOpening(GameTestHelper helper) {
        skeletonCanSeeMockPlayerThroughOpening(helper, Blocks.IRON_TRAPDOOR, 3.5 / 16.0, 3.5 / 16.0);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty", padding = 10)
    public void skeletonCanSeeMockPlayerThroughCopperTrapdoorOpening(GameTestHelper helper) {
        skeletonCanSeeMockPlayerThroughOpening(helper, Blocks.COPPER_TRAPDOOR.weathering().unaffected(), 5.5 / 16.0, 3.5 / 16.0);
    }

    @Override
    public void invokeTestMethod(GameTestHelper helper, Method method) throws ReflectiveOperationException {
        method.invoke(this, helper);
    }

    private static void arrowPassesThroughOpeningAndHitsLaterBlock(GameTestHelper helper, Block trapdoor, double x, double z) {
        placeClosedTrapdoor(helper, trapdoor);
        BlockPos trapdoorPosition = helper.absolutePos(TRAPDOOR);
        helper.setBlock(TRAPDOOR.below(2), Blocks.STONE);

        Arrow arrow = helper.spawn(EntityTypes.ARROW, new Vec3(TRAPDOOR.getX() + x, 5.0, TRAPDOOR.getZ() + z));
        arrow.setNoGravity(true);
        arrow.setDeltaMovement(0.0, -1.0, 0.0);
        helper.runAfterDelay(8, () -> {
            helper.assertTrue(arrow.getDeltaMovement().lengthSqr() == 0.0, "arrow should collide with the later stone block");
            helper.assertTrue(arrow.position().y < trapdoorPosition.getY(), "arrow should not embed in the trapdoor");
            helper.succeed();
        });
    }

    private static void arrowStopsAtSolidTrapdoorPixel(GameTestHelper helper, Block trapdoor) {
        placeClosedTrapdoor(helper, trapdoor);
        BlockPos trapdoorPosition = helper.absolutePos(TRAPDOOR);

        Arrow arrow = helper.spawn(EntityTypes.ARROW, new Vec3(TRAPDOOR.getX() + SOLID_SAMPLE, 5.0, TRAPDOOR.getZ() + SOLID_SAMPLE));
        arrow.setNoGravity(true);
        arrow.setDeltaMovement(0.0, -1.0, 0.0);
        helper.runAfterDelay(5, () -> {
            helper.assertTrue(arrow.getDeltaMovement().lengthSqr() == 0.0, "arrow should collide with the solid trapdoor pixel");
            helper.assertTrue(arrow.position().y > trapdoorPosition.getY() + 1.0 / 16.0, "arrow should not pass through the solid trapdoor pixel");
            helper.succeed();
        });
    }

    private static void skeletonCanSeeMockPlayerThroughOpening(GameTestHelper helper, Block trapdoor, double x, double z) {
        placeClosedTrapdoor(helper, trapdoor);
        BlockPos trapdoorPosition = helper.absolutePos(TRAPDOOR);
        BlockState trapdoorState = helper.getLevel().getBlockState(trapdoorPosition);
        Ray profileRay = new Ray(
            new io.github.accurateblockraycasts.geometry.Vec3(trapdoorPosition.getX() + x, trapdoorPosition.getY() - 1.0, trapdoorPosition.getZ() + z),
            new io.github.accurateblockraycasts.geometry.Vec3(trapdoorPosition.getX() + x, trapdoorPosition.getY() + 3.0, trapdoorPosition.getZ() + z)
        );
        RayProfile profile = RayProfileRegistry.INSTANCE.resolve(trapdoorState);
        helper.assertTrue(
            profile != null && profile.evaluate(trapdoorState, trapdoorPosition, profileRay) == RayProfileResult.OPEN,
            "test setup must send the line through a trapdoor opening"
        );

        Skeleton skeleton = helper.spawn(EntityTypes.SKELETON, new Vec3(TRAPDOOR.getX() + x, 0.0, TRAPDOOR.getZ() + z));
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.setPos(trapdoorPosition.getX() + x, trapdoorPosition.getY() + 1.0, trapdoorPosition.getZ() + z);

        helper.assertTrue(
            skeleton.hasLineOfSight(player),
            "skeleton should see a player through the trapdoor opening (skeleton eye=" + skeleton.getEyePosition()
                + ", player eye=" + player.getEyePosition() + ")"
        );
        helper.succeed();
    }

    private static void placeClosedTrapdoor(GameTestHelper helper, Block trapdoor) {
        helper.setBlock(
            TRAPDOOR,
            trapdoor.defaultBlockState()
                .setValue(TrapDoorBlock.FACING, Direction.NORTH)
                .setValue(TrapDoorBlock.OPEN, false)
                .setValue(TrapDoorBlock.HALF, Half.BOTTOM)
        );
    }
}
