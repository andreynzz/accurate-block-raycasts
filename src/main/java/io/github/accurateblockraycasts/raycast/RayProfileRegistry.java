package io.github.accurateblockraycasts.raycast;

import io.github.accurateblockraycasts.geometry.Vec3;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/** Resolves shared ray profiles only for explicitly supported block states. */
public final class RayProfileRegistry {
    public static final RayProfileRegistry INSTANCE = new RayProfileRegistry();

    private RayProfileRegistry() {
    }

    /**
     * Returns the supported block's profile, or {@code null} when vanilla must
     * retain full control of the raycast.
     */
    public @Nullable RayProfile resolve(BlockState state) {
        Objects.requireNonNull(state, "state");
        return state.is(Blocks.OAK_DOOR) ? OakDoorRayProfile.INSTANCE : null;
    }

    /** Returns whether a supported profile has an opening at this world point. */
    public boolean isPassableAt(BlockState state, BlockPos position, Vec3 worldPoint) {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(position, "position");
        Objects.requireNonNull(worldPoint, "worldPoint");
        RayProfile profile = resolve(state);
        return profile != null && profile.isPassableAt(state, position, worldPoint);
    }
}
