package io.github.accurateblockraycasts.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TrapdoorTransformTest {
    private static final BlockPos POSITION = new BlockPos(10, 64, 20);

    @BeforeAll
    static void bootstrapVanillaRegistries() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @ParameterizedTest
    @MethodSource("closedStates")
    void mapsClosedTrapdoorsToTheHorizontalUnitSquare(Half half) {
        TrapdoorTransform transform = TrapdoorTransform.forTrapdoor(state(Direction.NORTH, false, half), POSITION);

        assertEquals(new TrapdoorLocalCoordinates(0.25, 0.75), transform.toLocal(new Vec3(10.25, 64.5, 20.75)));
        assertEquals(half == Half.TOP ? 64.90625 : 64.09375, transform.plane().point().y());
    }

    @ParameterizedTest
    @MethodSource("openStates")
    void mapsOpenTrapdoorsForEveryFacing(Direction facing) {
        TrapdoorTransform transform = TrapdoorTransform.forTrapdoor(state(facing, true, Half.BOTTOM), POSITION);
        Vec3 normal = transform.planeNormal();
        Vec3 point = transform.plane().point();
        Vec3 width = new Vec3(facing.getClockWise().getStepX(), 0.0, facing.getClockWise().getStepZ());
        Vec3 sample = new Vec3(point.x() + width.x() * -0.25, 64.75, point.z() + width.z() * -0.25);

        assertEquals(new TrapdoorLocalCoordinates(0.25, 0.75), transform.toLocal(sample));
        assertEquals(1.0, normal.x() * normal.x() + normal.z() * normal.z());
    }

    private static Stream<Arguments> closedStates() {
        return Stream.of(Arguments.of(Half.BOTTOM), Arguments.of(Half.TOP));
    }

    private static Stream<Arguments> openStates() {
        return Stream.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST).map(Arguments::of);
    }

    private static BlockState state(Direction facing, boolean open, Half half) {
        return Blocks.OAK_TRAPDOOR.defaultBlockState()
            .setValue(TrapDoorBlock.FACING, facing)
            .setValue(TrapDoorBlock.OPEN, open)
            .setValue(TrapDoorBlock.HALF, half);
    }
}
