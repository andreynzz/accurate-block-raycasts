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
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;

/** Server gameplay coverage for representative vanilla-door opening profiles. */
public final class OakDoorGameplayGameTest implements CustomTestMethodInvoker {
    private static final BlockPos DOOR_LOWER = new BlockPos(3, 0, 3);

    @GameTest(structure = "fabric-gametest-api-v1:empty", maxTicks = 20, padding = 10)
    public void arrowPassesThroughOakDoorOpening(GameTestHelper helper) {
        arrowPassesThroughOpeningAndHitsLaterBlock(helper, Blocks.OAK_DOOR, 0.25);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty", maxTicks = 20, padding = 10)
    public void arrowPassesThroughAcaciaDoorOpening(GameTestHelper helper) {
        arrowPassesThroughOpeningAndHitsLaterBlock(helper, Blocks.ACACIA_DOOR, 0.25);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty", maxTicks = 20, padding = 10)
    public void arrowPassesThroughIronDoorOpening(GameTestHelper helper) {
        arrowPassesThroughOpeningAndHitsLaterBlock(helper, Blocks.IRON_DOOR, 0.25);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty", maxTicks = 20, padding = 10)
    public void arrowPassesThroughCopperDoorOpening(GameTestHelper helper) {
        arrowPassesThroughOpeningAndHitsLaterBlock(helper, Blocks.COPPER_DOOR.weathering().unaffected(), 0.25);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty", maxTicks = 20, padding = 10)
    public void arrowStopsAtSolidAcaciaDoorPixel(GameTestHelper helper) {
        arrowStopsAtSolidDoorPixel(helper, Blocks.ACACIA_DOOR);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty", maxTicks = 20, padding = 10)
    public void arrowStopsAtSolidIronDoorPixel(GameTestHelper helper) {
        arrowStopsAtSolidDoorPixel(helper, Blocks.IRON_DOOR);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty", maxTicks = 20, padding = 10)
    public void arrowStopsAtSolidCopperDoorPixel(GameTestHelper helper) {
        arrowStopsAtSolidDoorPixel(helper, Blocks.COPPER_DOOR.weathering().unaffected());
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty", padding = 10)
    public void skeletonCanSeeMockPlayerThroughOakDoorOpening(GameTestHelper helper) {
        skeletonCanSeeMockPlayerThroughOpening(helper, Blocks.OAK_DOOR, 0.25);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty", padding = 10)
    public void skeletonCanSeeMockPlayerThroughAcaciaDoorOpening(GameTestHelper helper) {
        skeletonCanSeeMockPlayerThroughOpening(helper, Blocks.ACACIA_DOOR, 0.25);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty", padding = 10)
    public void skeletonCanSeeMockPlayerThroughIronDoorOpening(GameTestHelper helper) {
        skeletonCanSeeMockPlayerThroughOpening(helper, Blocks.IRON_DOOR, 0.25);
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty", padding = 10)
    public void skeletonCanSeeMockPlayerThroughCopperDoorOpening(GameTestHelper helper) {
        skeletonCanSeeMockPlayerThroughOpening(helper, Blocks.COPPER_DOOR.weathering().unaffected(), 0.5);
    }

    @Override
    public void invokeTestMethod(GameTestHelper helper, Method method) throws ReflectiveOperationException {
        method.invoke(this, helper);
    }

    private static void arrowPassesThroughOpeningAndHitsLaterBlock(GameTestHelper helper, Block door, double x) {
        placeDoor(helper, door);
        BlockPos doorPosition = helper.absolutePos(DOOR_LOWER);
        helper.setBlock(new BlockPos(3, 1, 6), Blocks.STONE);

        Arrow arrow = helper.spawn(EntityTypes.ARROW, new Vec3(3.0 + x, 1.4, 1.0));
        arrow.setNoGravity(true);
        arrow.setDeltaMovement(0.0, 0.0, 1.0);
        helper.runAfterDelay(8, () -> {
            helper.assertTrue(arrow.getDeltaMovement().lengthSqr() == 0.0, "arrow should collide with the later stone block");
            helper.assertTrue(arrow.position().z > doorPosition.getZ() + 2.5, "arrow should not embed in the door");
            helper.succeed();
        });
    }

    private static void arrowStopsAtSolidDoorPixel(GameTestHelper helper, Block door) {
        placeDoor(helper, door);
        BlockPos doorPosition = helper.absolutePos(DOOR_LOWER);

        Arrow arrow = helper.spawn(EntityTypes.ARROW, new Vec3(3.15, 1.4, 1.0));
        arrow.setNoGravity(true);
        arrow.setDeltaMovement(0.0, 0.0, 1.0);
        helper.runAfterDelay(5, () -> {
            helper.assertTrue(arrow.getDeltaMovement().lengthSqr() == 0.0, "arrow should collide with the solid door pixel");
            helper.assertTrue(arrow.position().z < doorPosition.getZ() + 1.1, "arrow should not pass through the solid door pixel");
            helper.succeed();
        });
    }

    private static void skeletonCanSeeMockPlayerThroughOpening(GameTestHelper helper, Block door, double x) {
        placeDoor(helper, door);
        BlockPos doorPosition = helper.absolutePos(DOOR_LOWER);
        BlockState doorState = helper.getLevel().getBlockState(doorPosition);
        Ray profileRay = new Ray(
            new io.github.accurateblockraycasts.geometry.Vec3(doorPosition.getX() + x, doorPosition.getY() + 1.7, doorPosition.getZ() - 2.0),
            new io.github.accurateblockraycasts.geometry.Vec3(doorPosition.getX() + x, doorPosition.getY() + 1.7, doorPosition.getZ() + 2.0)
        );
        RayProfile profile = RayProfileRegistry.INSTANCE.resolve(doorState);
        helper.assertTrue(
            profile != null && profile.evaluate(doorState, doorPosition, profileRay) == RayProfileResult.OPEN,
            "test setup must send the line through a door opening"
        );
        Skeleton skeleton = helper.spawn(EntityTypes.SKELETON, new Vec3(3.0 + x, 0.0, 1.0));
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.setPos(doorPosition.getX() + x, doorPosition.getY(), doorPosition.getZ() + 3.0);

        helper.assertTrue(
            skeleton.hasLineOfSight(player),
            "skeleton should see a player through the door opening (skeleton eye=" + skeleton.getEyePosition()
                + ", player eye=" + player.getEyePosition() + ")"
        );
        helper.succeed();
    }

    private static void placeDoor(GameTestHelper helper, Block door) {
        BlockState base = door.defaultBlockState()
            .setValue(DoorBlock.FACING, Direction.NORTH)
            .setValue(DoorBlock.OPEN, false)
            .setValue(DoorBlock.HINGE, DoorHingeSide.LEFT);
        helper.setBlock(DOOR_LOWER, base.setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER));
        helper.setBlock(DOOR_LOWER.above(), base.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));
    }
}
