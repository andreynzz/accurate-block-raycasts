package io.github.accurateblockraycasts.raycast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.accurateblockraycasts.geometry.Ray;
import io.github.accurateblockraycasts.geometry.Vec3;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class PerforatedRaycasterTest {
    private static final BlockPos DOOR_POS = new BlockPos(10, 65, 20);

    @BeforeAll
    static void bootstrapVanillaRegistries() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void continuesPastAnOpenPixelAndReturnsTheLaterVanillaHit() {
        BlockRaycastHit opening = new BlockRaycastHit(DOOR_POS, oakDoorUpperHalf());
        BlockRaycastHit laterHit = new BlockRaycastHit(new BlockPos(10, 65, 22), Blocks.STONE.defaultBlockState());
        AtomicInteger calls = new AtomicInteger();

        BlockRaycastHit result = PerforatedRaycaster.INSTANCE.trace(openingRay(), ray -> {
            if (calls.getAndIncrement() == 0) {
                return opening;
            }
            assertTrue(ray.start().z() > 21.0);
            return laterHit;
        });

        assertSame(laterHit, result);
        assertEquals(2, calls.get());
    }

    @Test
    void returnsTheInitialHitWhenThePixelIsSolid() {
        BlockRaycastHit solidDoor = new BlockRaycastHit(new BlockPos(10, 64, 20), oakDoorLowerHalf());
        AtomicInteger calls = new AtomicInteger();

        BlockRaycastHit result = PerforatedRaycaster.INSTANCE.trace(solidRay(), ray -> {
            calls.incrementAndGet();
            return solidDoor;
        });

        assertSame(solidDoor, result);
        assertEquals(1, calls.get());
    }

    @Test
    void returnsTheInitialHitForUnsupportedBlocksAndMisses() {
        BlockRaycastHit stone = new BlockRaycastHit(new BlockPos(10, 65, 20), Blocks.STONE.defaultBlockState());

        assertSame(stone, PerforatedRaycaster.INSTANCE.trace(openingRay(), ray -> stone));
        assertNull(PerforatedRaycaster.INSTANCE.trace(openingRay(), ray -> null));
    }

    private static BlockState oakDoorLowerHalf() {
        return oakDoor(DoubleBlockHalf.LOWER);
    }

    private static BlockState oakDoorUpperHalf() {
        return oakDoor(DoubleBlockHalf.UPPER);
    }

    private static BlockState oakDoor(DoubleBlockHalf half) {
        return Blocks.OAK_DOOR.defaultBlockState()
            .setValue(DoorBlock.FACING, Direction.NORTH)
            .setValue(DoorBlock.OPEN, false)
            .setValue(DoorBlock.HINGE, DoorHingeSide.LEFT)
            .setValue(DoorBlock.HALF, half);
    }

    private static Ray openingRay() {
        return new Ray(new Vec3(10.25, 65.35, 19.0), new Vec3(10.25, 65.35, 23.0));
    }

    private static Ray solidRay() {
        return new Ray(new Vec3(10.25, 64.5, 19.0), new Vec3(10.25, 64.5, 23.0));
    }
}
