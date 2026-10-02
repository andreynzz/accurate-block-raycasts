package io.github.accurateblockraycasts.gametest;

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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;

/** Gameplay coverage for a birch-door profile supplied by the GameTest datapack. */
public final class DataDrivenProfileGameTest implements CustomTestMethodInvoker {
    private static final BlockPos DOOR_LOWER = new BlockPos(3, 0, 3);

    @GameTest(structure = "fabric-gametest-api-v1:empty", maxTicks = 20, padding = 10)
    public void arrowPassesThroughDataDrivenDoorProfile(GameTestHelper helper) {
        placeBirchDoor(helper);
        BlockPos doorPosition = helper.absolutePos(DOOR_LOWER);
        helper.setBlock(new BlockPos(3, 1, 6), Blocks.STONE);

        Arrow arrow = helper.spawn(EntityTypes.ARROW, new Vec3(3.25, 1.4, 1.0));
        arrow.setNoGravity(true);
        arrow.setDeltaMovement(0.0, 0.0, 1.0);
        helper.runAfterDelay(8, () -> {
            helper.assertTrue(arrow.getDeltaMovement().lengthSqr() == 0.0, "arrow should collide with the later stone block");
            helper.assertTrue(arrow.position().z > doorPosition.getZ() + 2.5, "arrow should not embed in the data-driven door");
            helper.succeed();
        });
    }

    @GameTest(structure = "fabric-gametest-api-v1:empty", padding = 10)
    public void skeletonCanSeeThroughDataDrivenDoorProfile(GameTestHelper helper) {
        placeBirchDoor(helper);
        BlockPos doorPosition = helper.absolutePos(DOOR_LOWER);
        Skeleton skeleton = helper.spawn(EntityTypes.SKELETON, new Vec3(3.25, 0.0, 1.0));
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.setPos(doorPosition.getX() + 0.25, doorPosition.getY(), doorPosition.getZ() + 3.0);

        helper.assertTrue(
            skeleton.hasLineOfSight(player),
            "skeleton should see a player through the data-driven door opening"
        );
        helper.succeed();
    }

    @Override
    public void invokeTestMethod(GameTestHelper helper, Method method) throws ReflectiveOperationException {
        method.invoke(this, helper);
    }

    private static void placeBirchDoor(GameTestHelper helper) {
        BlockState base = Blocks.BIRCH_DOOR.defaultBlockState()
            .setValue(DoorBlock.FACING, Direction.NORTH)
            .setValue(DoorBlock.OPEN, false)
            .setValue(DoorBlock.HINGE, DoorHingeSide.LEFT);
        helper.setBlock(DOOR_LOWER, base.setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER));
        helper.setBlock(DOOR_LOWER.above(), base.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));
    }
}
