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
    private static final RayProfile ACACIA_DOOR = new DoorRayProfile(Blocks.ACACIA_DOOR, OtherWoodDoorMasks.acacia());
    private static final RayProfile BAMBOO_DOOR = new DoorRayProfile(Blocks.BAMBOO_DOOR, OtherWoodDoorMasks.bamboo());
    private static final RayProfile CHERRY_DOOR = new DoorRayProfile(Blocks.CHERRY_DOOR, OtherWoodDoorMasks.cherry());
    private static final RayProfile JUNGLE_DOOR = new DoorRayProfile(Blocks.JUNGLE_DOOR, OtherWoodDoorMasks.jungle());
    private static final RayProfile POPLAR_DOOR = new DoorRayProfile(Blocks.POPLAR_DOOR, OtherWoodDoorMasks.poplar());

    private RayProfileRegistry() {
    }

    /**
     * Returns the supported block's profile, or {@code null} when vanilla must
     * retain full control of the raycast.
     */
    public @Nullable RayProfile resolve(BlockState state) {
        Objects.requireNonNull(state, "state");
        if (state.is(Blocks.OAK_DOOR)) {
            return OakDoorRayProfile.INSTANCE;
        }
        if (state.is(Blocks.ACACIA_DOOR)) {
            return ACACIA_DOOR;
        }
        if (state.is(Blocks.BAMBOO_DOOR)) {
            return BAMBOO_DOOR;
        }
        if (state.is(Blocks.CHERRY_DOOR)) {
            return CHERRY_DOOR;
        }
        if (state.is(Blocks.JUNGLE_DOOR)) {
            return JUNGLE_DOOR;
        }
        return state.is(Blocks.POPLAR_DOOR) ? POPLAR_DOOR : null;
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
