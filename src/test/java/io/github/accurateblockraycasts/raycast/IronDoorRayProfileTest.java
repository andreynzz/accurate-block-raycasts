package io.github.accurateblockraycasts.raycast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.accurateblockraycasts.geometry.DoorTransform;
import io.github.accurateblockraycasts.geometry.Ray;
import io.github.accurateblockraycasts.geometry.Vec3;
import java.util.stream.Stream;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class IronDoorRayProfileTest {
    private static final BlockPos LOWER_POS = new BlockPos(10, 64, 20);

    @BeforeAll
    static void bootstrapVanillaRegistries() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @ParameterizedTest
    @MethodSource("doorStates")
    void classifiesTheVerifiedWindowLayoutForEveryOrientation(Direction facing, boolean open, DoorHingeSide hinge) {
        BlockState state = Blocks.IRON_DOOR.defaultBlockState()
            .setValue(DoorBlock.FACING, facing)
            .setValue(DoorBlock.OPEN, open)
            .setValue(DoorBlock.HINGE, hinge)
            .setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER);
        DoorTransform transform = DoorTransform.forDoor(state, LOWER_POS);
        RayProfile profile = RayProfileRegistry.INSTANCE.resolve(state);

        assertEquals(RayProfileResult.OPEN, profile.evaluate(state, LOWER_POS, crossingRay(transform, 0.25, 0.675)));
        assertEquals(RayProfileResult.SOLID, profile.evaluate(state, LOWER_POS, crossingRay(transform, 0.15, 0.675)));
    }

    private static Stream<Arguments> doorStates() {
        return Stream.of(
            Arguments.of(Direction.NORTH, false, DoorHingeSide.LEFT),
            Arguments.of(Direction.EAST, false, DoorHingeSide.LEFT),
            Arguments.of(Direction.SOUTH, false, DoorHingeSide.LEFT),
            Arguments.of(Direction.WEST, false, DoorHingeSide.LEFT),
            Arguments.of(Direction.NORTH, true, DoorHingeSide.LEFT),
            Arguments.of(Direction.NORTH, true, DoorHingeSide.RIGHT),
            Arguments.of(Direction.EAST, true, DoorHingeSide.LEFT),
            Arguments.of(Direction.EAST, true, DoorHingeSide.RIGHT),
            Arguments.of(Direction.SOUTH, true, DoorHingeSide.LEFT),
            Arguments.of(Direction.SOUTH, true, DoorHingeSide.RIGHT),
            Arguments.of(Direction.WEST, true, DoorHingeSide.LEFT),
            Arguments.of(Direction.WEST, true, DoorHingeSide.RIGHT)
        );
    }

    private static Ray crossingRay(DoorTransform transform, double u, double v) {
        Vec3 normal = transform.planeNormal();
        Vec3 width = new Vec3(-normal.z(), 0.0, normal.x());
        Vec3 planePoint = transform.plane().point();
        Vec3 intersection = new Vec3(
            planePoint.x() + width.x() * (u - 0.5),
            LOWER_POS.getY() + v * 2.0,
            planePoint.z() + width.z() * (u - 0.5)
        );
        return new Ray(intersection.subtract(normal), intersection.add(normal));
    }
}
