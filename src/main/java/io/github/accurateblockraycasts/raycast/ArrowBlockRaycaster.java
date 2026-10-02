package io.github.accurateblockraycasts.raycast;

import io.github.accurateblockraycasts.geometry.Ray;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Adapts the shared perforated traversal to vanilla arrow block tracing. */
public final class ArrowBlockRaycaster {
    private ArrowBlockRaycaster() {
    }

    /**
     * Replaces the block trace made by {@code AbstractArrow.tick} on the
     * logical server. Client prediction retains its untouched vanilla trace.
     */
    public static BlockHitResult trace(Level level, Entity arrow, ClipContext originalContext) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(arrow, "arrow");
        Objects.requireNonNull(originalContext, "originalContext");
        if (level.isClientSide()) {
            return level.clipIncludingBorder(originalContext);
        }

        Vec3 from = originalContext.getFrom();
        Vec3 to = originalContext.getTo();
        Ray originalRay = new Ray(toGeometry(from), toGeometry(to));
        BlockRaycastHit result = PerforatedRaycaster.INSTANCE.trace(originalRay, ray -> traceVanilla(level, arrow, ray));
        return result == null ? miss(to, from) : Objects.requireNonNull(result.vanillaHit(), "vanillaHit");
    }

    private static BlockRaycastHit traceVanilla(Level level, Entity arrow, Ray ray) {
        BlockHitResult hit = level.clipIncludingBorder(
            new ClipContext(toVanilla(ray.start()), toVanilla(ray.end()), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, arrow)
        );
        if (hit.getType() == HitResult.Type.MISS) {
            return null;
        }
        BlockPos position = hit.getBlockPos();
        return new BlockRaycastHit(position, level.getBlockState(position), hit);
    }

    private static BlockHitResult miss(Vec3 to, Vec3 from) {
        return BlockHitResult.miss(to, nearestDirection(to.x - from.x, to.y - from.y, to.z - from.z), BlockPos.containing(to));
    }

    private static Direction nearestDirection(double x, double y, double z) {
        double horizontal = Math.max(Math.abs(x), Math.abs(z));
        if (Math.abs(y) > horizontal) {
            return y >= 0.0 ? Direction.UP : Direction.DOWN;
        }
        if (Math.abs(x) > Math.abs(z)) {
            return x >= 0.0 ? Direction.EAST : Direction.WEST;
        }
        return z >= 0.0 ? Direction.SOUTH : Direction.NORTH;
    }

    private static io.github.accurateblockraycasts.geometry.Vec3 toGeometry(Vec3 vector) {
        return new io.github.accurateblockraycasts.geometry.Vec3(vector.x, vector.y, vector.z);
    }

    private static Vec3 toVanilla(io.github.accurateblockraycasts.geometry.Vec3 vector) {
        return new Vec3(vector.x(), vector.y(), vector.z());
    }
}
