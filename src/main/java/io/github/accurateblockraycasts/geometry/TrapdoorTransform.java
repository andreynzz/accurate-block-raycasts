package io.github.accurateblockraycasts.geometry;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;

/** Maps the representative plane of one vanilla trapdoor into a unit square. */
public final class TrapdoorTransform {
    private static final double MID_PLANE_OFFSET = (1.0 - 3.0 / 16.0) / 2.0;

    private final BlockPos position;
    private final boolean open;
    private final Direction facing;
    private final Half half;

    private TrapdoorTransform(BlockPos position, boolean open, Direction facing, Half half) {
        this.position = position.immutable();
        this.open = open;
        this.facing = facing;
        this.half = half;
    }

    /** Creates a transform for one vanilla {@link TrapDoorBlock} state. */
    public static TrapdoorTransform forTrapdoor(BlockState state, BlockPos position) {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(position, "position");
        if (!(state.getBlock() instanceof TrapDoorBlock)) {
            throw new IllegalArgumentException("state must belong to a TrapDoorBlock");
        }
        return new TrapdoorTransform(
            position,
            state.getValue(TrapDoorBlock.OPEN),
            state.getValue(TrapDoorBlock.FACING),
            state.getValue(TrapDoorBlock.HALF)
        );
    }

    /** The shared representative mid-plane of the trapdoor's 3/16-block slab. */
    public Plane plane() {
        Vec3 normal = planeNormal();
        if (open) {
            return new Plane(new Vec3(
                position.getX() + 0.5 + normal.x() * MID_PLANE_OFFSET,
                position.getY(),
                position.getZ() + 0.5 + normal.z() * MID_PLANE_OFFSET
            ), normal);
        }
        double y = position.getY() + (half == Half.TOP ? 1.0 - 3.0 / 32.0 : 3.0 / 32.0);
        return new Plane(new Vec3(position.getX(), y, position.getZ()), normal);
    }

    /** The plane normal selected from the trapdoor's open or closed state. */
    public Vec3 planeNormal() {
        return open ? horizontalVector(facing.getOpposite()) : new Vec3(0.0, 1.0, 0.0);
    }

    /** Maps a world-space point into the surface's canonical unit square. */
    public TrapdoorLocalCoordinates toLocal(Vec3 worldPoint) {
        Objects.requireNonNull(worldPoint, "worldPoint");
        if (!open) {
            return new TrapdoorLocalCoordinates(worldPoint.x() - position.getX(), worldPoint.z() - position.getZ());
        }
        Vec3 width = horizontalVector(facing.getClockWise());
        Vec3 planePoint = plane().point();
        double u = (worldPoint.x() - planePoint.x()) * width.x() + (worldPoint.z() - planePoint.z()) * width.z() + 0.5;
        return new TrapdoorLocalCoordinates(u, worldPoint.y() - position.getY());
    }

    /** Intersects a finite ray with the representative plane and maps the hit locally. */
    public Optional<TrapdoorLocalCoordinates> intersect(Ray ray) {
        return plane().intersect(ray).map(intersection -> toLocal(intersection.point()));
    }

    private static Vec3 horizontalVector(Direction direction) {
        return new Vec3(direction.getStepX(), 0.0, direction.getStepZ());
    }
}
