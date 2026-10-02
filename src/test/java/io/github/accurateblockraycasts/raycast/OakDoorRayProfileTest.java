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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class OakDoorRayProfileTest {
    private static final BlockPos LOWER_POS = new BlockPos(10, 64, 20);

    @BeforeAll
    static void bootstrapVanillaRegistries() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @ParameterizedTest
    @MethodSource("closedDoorFacings")
    void classifiesOpeningsAndSolidPixelsForEveryClosedFacing(Direction facing) {
        BlockState state = oakDoor(facing, false, DoorHingeSide.LEFT);
        DoorTransform transform = DoorTransform.forOakDoor(state, LOWER_POS);

        assertEquals(RayProfileResult.OPEN, evaluate(state, crossingRay(transform, 0.25, 0.675)));
        assertEquals(RayProfileResult.SOLID, evaluate(state, crossingRay(transform, 0.15, 0.675)));
    }

    @ParameterizedTest
    @MethodSource("openDoorStates")
    void classifiesTheSameMaskAfterOpenDoorRotation(Direction facing, DoorHingeSide hinge) {
        BlockState state = oakDoor(facing, true, hinge);
        DoorTransform transform = DoorTransform.forOakDoor(state, LOWER_POS);

        assertEquals(RayProfileResult.OPEN, evaluate(state, crossingRay(transform, 0.25, 0.675)));
        assertEquals(RayProfileResult.SOLID, evaluate(state, crossingRay(transform, 0.15, 0.675)));
    }

    @Test
    void preservesVanillaBehaviorForUnsupportedOrNonIntersectingRays() {
        Ray ray = new Ray(new Vec3(10.25, 64.5, 19.0), new Vec3(10.25, 64.5, 22.0));
        BlockState oakDoor = oakDoor(Direction.NORTH, false, DoorHingeSide.LEFT);

        assertEquals(RayProfileResult.NO_SPECIAL_RESULT, evaluate(Blocks.STONE.defaultBlockState(), ray));
        assertEquals(
            RayProfileResult.NO_SPECIAL_RESULT,
            evaluate(oakDoor, new Ray(new Vec3(10.0, 64.5, 20.0), new Vec3(11.0, 64.5, 20.0)))
        );
    }

    @Test
    void preservesVanillaBehaviorAtTheSurfaceBoundary() {
        BlockState state = oakDoor(Direction.NORTH, false, DoorHingeSide.LEFT);
        DoorTransform transform = DoorTransform.forOakDoor(state, LOWER_POS);

        assertEquals(RayProfileResult.NO_SPECIAL_RESULT, evaluate(state, crossingRay(transform, 1.0, 0.675)));
        assertEquals(RayProfileResult.NO_SPECIAL_RESULT, evaluate(state, crossingRay(transform, 0.25, 1.0)));
    }

    private static Stream<Direction> closedDoorFacings() {
        return Stream.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
    }

    private static Stream<Arguments> openDoorStates() {
        return Stream.of(
            Arguments.of(Direction.NORTH, DoorHingeSide.LEFT),
            Arguments.of(Direction.NORTH, DoorHingeSide.RIGHT),
            Arguments.of(Direction.EAST, DoorHingeSide.LEFT),
            Arguments.of(Direction.EAST, DoorHingeSide.RIGHT),
            Arguments.of(Direction.SOUTH, DoorHingeSide.LEFT),
            Arguments.of(Direction.SOUTH, DoorHingeSide.RIGHT),
            Arguments.of(Direction.WEST, DoorHingeSide.LEFT),
            Arguments.of(Direction.WEST, DoorHingeSide.RIGHT)
        );
    }

    private static RayProfileResult evaluate(BlockState state, Ray ray) {
        return OakDoorRayProfile.INSTANCE.evaluate(state, LOWER_POS, ray);
    }

    private static BlockState oakDoor(Direction facing, boolean open, DoorHingeSide hinge) {
        return Blocks.OAK_DOOR.defaultBlockState()
            .setValue(DoorBlock.FACING, facing)
            .setValue(DoorBlock.OPEN, open)
            .setValue(DoorBlock.HINGE, hinge)
            .setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER);
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
