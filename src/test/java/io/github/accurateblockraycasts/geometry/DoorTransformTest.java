package io.github.accurateblockraycasts.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Test;

class DoorTransformTest {
    private static final double EPSILON = 1.0E-10;
    private static final BlockPos LOWER_POS = new BlockPos(10, 64, 20);

    @ParameterizedTest
    @MethodSource("closedDoorPoints")
    void mapsKnownClosedDoorPoints(Direction facing, Vec3 worldPoint) {
        DoorTransform transform = transform(facing, false, DoorHingeSide.LEFT, DoubleBlockHalf.LOWER, LOWER_POS);

        assertCoordinates(transform.toLocal(worldPoint), 0.25, 0.25);
    }

    private static Stream<Arguments> closedDoorPoints() {
        return Stream.of(
            Arguments.of(Direction.NORTH, new Vec3(10.25, 64.5, 20.90625)),
            Arguments.of(Direction.EAST, new Vec3(10.09375, 64.5, 20.25)),
            Arguments.of(Direction.SOUTH, new Vec3(10.75, 64.5, 20.09375)),
            Arguments.of(Direction.WEST, new Vec3(10.90625, 64.5, 20.75))
        );
    }

    @ParameterizedTest
    @MethodSource("openDoorStates")
    void mapsEveryOpenHingeAndFacingWithVanillaNinetyDegreeRotation(Direction facing, DoorHingeSide hinge, Direction expectedNormal) {
        DoorTransform transform = transform(facing, true, hinge, DoubleBlockHalf.LOWER, LOWER_POS);
        Vec3 worldPoint = pointOnPlane(expectedNormal, 0.25, 0.25);

        assertEquals(vector(expectedNormal), transform.planeNormal());
        assertCoordinates(transform.toLocal(worldPoint), 0.25, 0.25);
    }

    private static Stream<Arguments> openDoorStates() {
        return Stream.of(
            Arguments.of(Direction.NORTH, DoorHingeSide.LEFT, Direction.EAST),
            Arguments.of(Direction.NORTH, DoorHingeSide.RIGHT, Direction.WEST),
            Arguments.of(Direction.EAST, DoorHingeSide.LEFT, Direction.SOUTH),
            Arguments.of(Direction.EAST, DoorHingeSide.RIGHT, Direction.NORTH),
            Arguments.of(Direction.SOUTH, DoorHingeSide.LEFT, Direction.WEST),
            Arguments.of(Direction.SOUTH, DoorHingeSide.RIGHT, Direction.EAST),
            Arguments.of(Direction.WEST, DoorHingeSide.LEFT, Direction.NORTH),
            Arguments.of(Direction.WEST, DoorHingeSide.RIGHT, Direction.SOUTH)
        );
    }

    @Test
    void mapsBothHalvesIntoOneContinuousVerticalRange() {
        DoorTransform lower = transform(Direction.NORTH, false, DoorHingeSide.LEFT, DoubleBlockHalf.LOWER, LOWER_POS);
        DoorTransform upper = transform(Direction.NORTH, false, DoorHingeSide.LEFT, DoubleBlockHalf.UPPER, LOWER_POS.above());

        assertCoordinates(lower.toLocal(new Vec3(10.5, 64.5, 20.90625)), 0.5, 0.25);
        assertCoordinates(upper.toLocal(new Vec3(10.5, 65.5, 20.90625)), 0.5, 0.75);
    }

    @Test
    void mapsCanonicalBoundariesAndRayIntersection() {
        DoorTransform transform = transform(Direction.NORTH, false, DoorHingeSide.LEFT, DoubleBlockHalf.LOWER, LOWER_POS);

        assertCoordinates(transform.toLocal(new Vec3(10.0, 64.0, 20.90625)), 0.0, 0.0);
        assertCoordinates(transform.toLocal(new Vec3(10.5, 65.0, 20.90625)), 0.5, 0.5);
        assertCoordinates(transform.toLocal(new Vec3(11.0, 66.0, 20.90625)), 1.0, 1.0);

        var intersection = transform.intersect(new Ray(new Vec3(10.25, 64.5, 19.0), new Vec3(10.25, 64.5, 22.0)));
        assertTrue(intersection.isPresent());
        assertEquals(0.6354166666666666, intersection.orElseThrow().parameter(), EPSILON);
        assertCoordinates(intersection.orElseThrow().localCoordinates(), 0.25, 0.25);
    }

    @ParameterizedTest
    @MethodSource("allPhysicalOrientations")
    void mapsEquivalentPointsSymmetricallyForEveryPhysicalOrientation(Direction normal) {
        DoorTransform transform = transform(normal, false, DoorHingeSide.LEFT, DoubleBlockHalf.LOWER, LOWER_POS);

        assertCoordinates(transform.toLocal(pointOnPlane(normal, 0.75, 0.6)), 0.75, 0.6);
    }

    private static Stream<Direction> allPhysicalOrientations() {
        return Stream.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
    }

    private static Vec3 pointOnPlane(Direction normal, double u, double v) {
        Vec3 normalVector = vector(normal);
        Vec3 widthVector = vector(normal.getClockWise());
        return new Vec3(
            LOWER_POS.getX() + 0.5 - normalVector.x() * 0.40625 + widthVector.x() * (u - 0.5),
            LOWER_POS.getY() + v * 2.0,
            LOWER_POS.getZ() + 0.5 - normalVector.z() * 0.40625 + widthVector.z() * (u - 0.5)
        );
    }

    private static Vec3 vector(Direction direction) {
        return new Vec3(direction.getStepX(), 0.0, direction.getStepZ());
    }

    private static DoorTransform transform(Direction facing, boolean open, DoorHingeSide hinge, DoubleBlockHalf half, BlockPos position) {
        return DoorTransform.forDoorState(facing, open, hinge, half, position);
    }

    private static void assertCoordinates(DoorLocalCoordinates coordinates, double expectedU, double expectedV) {
        assertEquals(expectedU, coordinates.u(), EPSILON);
        assertEquals(expectedV, coordinates.v(), EPSILON);
    }
}
