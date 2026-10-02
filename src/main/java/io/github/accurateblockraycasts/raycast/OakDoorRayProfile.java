package io.github.accurateblockraycasts.raycast;

import io.github.accurateblockraycasts.geometry.Ray;
import io.github.accurateblockraycasts.geometry.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** Shared server-side ray geometry for {@code minecraft:oak_door}. */
public final class OakDoorRayProfile implements RayProfile {
    public static final OakDoorRayProfile INSTANCE = new OakDoorRayProfile();
    private static final DoorRayProfile PROFILE = new DoorRayProfile(Blocks.OAK_DOOR, OakDoorMasks.fullDoor());

    private OakDoorRayProfile() {
    }

    @Override
    public RayProfileResult evaluate(BlockState state, BlockPos position, Ray ray) {
        return PROFILE.evaluate(state, position, ray);
    }

    /** Returns whether a point projected onto this door's canonical surface is an opening. */
    @Override
    public boolean isPassableAt(BlockState state, BlockPos position, Vec3 worldPoint) {
        return PROFILE.isPassableAt(state, position, worldPoint);
    }
}
