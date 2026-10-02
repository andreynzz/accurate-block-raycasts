package io.github.accurateblockraycasts.geometry;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

/**
 * Maps the physical plane of one vanilla door into a single canonical surface.
 *
 * <p>The canonical front is the face opposite {@link DoorBlock#FACING} when
 * the door is closed. Looking at that front, {@code u = 0} is the left edge
 * and {@code u = 1} is the right edge. {@code v = 0} is the bottom of the
 * lower block, {@code v = 0.5} is the join between blocks, and {@code v = 1}
 * is the top of the upper block.
 *
 * <p>Vanilla represents each door half as a 3/16-block slab. This transform
 * deliberately uses the slab's mid-plane, rather than selecting a ray-facing
 * surface, so one deterministic plane is shared by later projectile and
 * vision integrations.
 */
public final class DoorTransform {
    private static final double SLAB_THICKNESS = 3.0 / 16.0;
    private static final double MID_PLANE_OFFSET = (1.0 - SLAB_THICKNESS) / 2.0;

    private final BlockPos position;
    private final DoubleBlockHalf half;
    private final Direction effectiveDirection;
    private final int lowerBlockY;

    private DoorTransform(BlockPos position, DoubleBlockHalf half, Direction effectiveDirection) {
        this.position = position.immutable();
        this.half = half;
        this.effectiveDirection = effectiveDirection;
        this.lowerBlockY = position.getY() - (half == DoubleBlockHalf.UPPER ? 1 : 0);
    }

    /** Creates a transform for one half of a vanilla {@link DoorBlock}. */
    public static DoorTransform forDoor(BlockState state, BlockPos position) {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(position, "position");
        if (!(state.getBlock() instanceof DoorBlock)) {
            throw new IllegalArgumentException("state must belong to a DoorBlock");
        }

        Direction facing = state.getValue(DoorBlock.FACING);
        boolean open = state.getValue(DoorBlock.OPEN);
        DoorHingeSide hinge = state.getValue(DoorBlock.HINGE);
        return forDoorState(facing, open, hinge, state.getValue(DoorBlock.HALF), position);
    }

    /**
     * Creates the same transform from already-decoded vanilla door properties.
     * Package access keeps registry-free geometry tests independent from game bootstrap.
     */
    static DoorTransform forDoorState(
        Direction facing, boolean open, DoorHingeSide hinge, DoubleBlockHalf half, BlockPos position
    ) {
        Objects.requireNonNull(facing, "facing");
        Objects.requireNonNull(hinge, "hinge");
        Objects.requireNonNull(half, "half");
        Direction effectiveDirection = !open
            ? facing
            : hinge == DoorHingeSide.RIGHT ? facing.getCounterClockWise() : facing.getClockWise();
        return new DoorTransform(position, half, effectiveDirection);
    }

    /** The normal used by vanilla's selected horizontal door shape. */
    public Vec3 planeNormal() {
        return horizontalVector(effectiveDirection);
    }

    /** Returns the representative mid-plane of this door half's physical slab. */
    public Plane plane() {
        Vec3 normal = planeNormal();
        return new Plane(new Vec3(
            position.getX() + 0.5 - normal.x() * MID_PLANE_OFFSET,
            lowerBlockY,
            position.getZ() + 0.5 - normal.z() * MID_PLANE_OFFSET
        ), normal);
    }

    /** Maps a world-space point into the canonical two-block door coordinates. */
    public DoorLocalCoordinates toLocal(Vec3 worldPoint) {
        Objects.requireNonNull(worldPoint, "worldPoint");
        Vec3 planePoint = plane().point();
        Vec3 width = horizontalVector(effectiveDirection.getClockWise());
        double u = (worldPoint.x() - planePoint.x()) * width.x() + (worldPoint.z() - planePoint.z()) * width.z() + 0.5;
        double v = (worldPoint.y() - lowerBlockY) / 2.0;
        return new DoorLocalCoordinates(u, v);
    }

    /** Intersects a finite ray segment with the representative door plane and maps it locally. */
    public Optional<DoorIntersection> intersect(Ray ray) {
        return plane().intersect(ray).map(intersection -> new DoorIntersection(
            intersection.parameter(), intersection.point(), toLocal(intersection.point())
        ));
    }

    private static Vec3 horizontalVector(Direction direction) {
        return new Vec3(direction.getStepX(), 0.0, direction.getStepZ());
    }
}
