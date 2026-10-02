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
    private static final RayProfile COPPER_DOOR = new DoorRayProfile(Blocks.COPPER_DOOR.weathering().unaffected(), CopperDoorMasks.fullDoor());
    private static final RayProfile EXPOSED_COPPER_DOOR = new DoorRayProfile(Blocks.COPPER_DOOR.weathering().exposed(), CopperDoorMasks.fullDoor());
    // Vanilla's iron door has the same two-by-two upper-window layout as oak.
    private static final RayProfile IRON_DOOR = new DoorRayProfile(Blocks.IRON_DOOR, OakDoorMasks.fullDoor());
    private static final RayProfile JUNGLE_DOOR = new DoorRayProfile(Blocks.JUNGLE_DOOR, OtherWoodDoorMasks.jungle());
    private static final RayProfile POPLAR_DOOR = new DoorRayProfile(Blocks.POPLAR_DOOR, OtherWoodDoorMasks.poplar());
    private static final RayProfile WEATHERED_COPPER_DOOR = new DoorRayProfile(Blocks.COPPER_DOOR.weathering().weathered(), CopperDoorMasks.fullDoor());
    private static final RayProfile OXIDIZED_COPPER_DOOR = new DoorRayProfile(Blocks.COPPER_DOOR.weathering().oxidized(), CopperDoorMasks.fullDoor());
    private static final RayProfile WAXED_COPPER_DOOR = new DoorRayProfile(Blocks.COPPER_DOOR.waxed().unaffected(), CopperDoorMasks.fullDoor());
    private static final RayProfile WAXED_EXPOSED_COPPER_DOOR = new DoorRayProfile(Blocks.COPPER_DOOR.waxed().exposed(), CopperDoorMasks.fullDoor());
    private static final RayProfile WAXED_WEATHERED_COPPER_DOOR = new DoorRayProfile(Blocks.COPPER_DOOR.waxed().weathered(), CopperDoorMasks.fullDoor());
    private static final RayProfile WAXED_OXIDIZED_COPPER_DOOR = new DoorRayProfile(Blocks.COPPER_DOOR.waxed().oxidized(), CopperDoorMasks.fullDoor());
    private static final RayProfile ACACIA_TRAPDOOR = new TrapdoorRayProfile(Blocks.ACACIA_TRAPDOOR, TrapdoorMasks.acacia());
    private static final RayProfile BAMBOO_TRAPDOOR = new TrapdoorRayProfile(Blocks.BAMBOO_TRAPDOOR, TrapdoorMasks.bamboo());
    private static final RayProfile CHERRY_TRAPDOOR = new TrapdoorRayProfile(Blocks.CHERRY_TRAPDOOR, TrapdoorMasks.cherry());
    private static final RayProfile CRIMSON_TRAPDOOR = new TrapdoorRayProfile(Blocks.CRIMSON_TRAPDOOR, TrapdoorMasks.crimson());
    private static final RayProfile JUNGLE_TRAPDOOR = new TrapdoorRayProfile(Blocks.JUNGLE_TRAPDOOR, TrapdoorMasks.jungle());
    private static final RayProfile MANGROVE_TRAPDOOR = new TrapdoorRayProfile(Blocks.MANGROVE_TRAPDOOR, TrapdoorMasks.mangrove());
    private static final RayProfile OAK_TRAPDOOR = new TrapdoorRayProfile(Blocks.OAK_TRAPDOOR, TrapdoorMasks.oak());
    private static final RayProfile POPLAR_TRAPDOOR = new TrapdoorRayProfile(Blocks.POPLAR_TRAPDOOR, TrapdoorMasks.poplar());
    private static final RayProfile WARPED_TRAPDOOR = new TrapdoorRayProfile(Blocks.WARPED_TRAPDOOR, TrapdoorMasks.warped());

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
        if (state.is(Blocks.ACACIA_TRAPDOOR)) {
            return ACACIA_TRAPDOOR;
        }
        if (state.is(Blocks.BAMBOO_TRAPDOOR)) {
            return BAMBOO_TRAPDOOR;
        }
        if (state.is(Blocks.CHERRY_TRAPDOOR)) {
            return CHERRY_TRAPDOOR;
        }
        if (state.is(Blocks.CRIMSON_TRAPDOOR)) {
            return CRIMSON_TRAPDOOR;
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
        if (state.is(Blocks.COPPER_DOOR.weathering().unaffected())) {
            return COPPER_DOOR;
        }
        if (state.is(Blocks.COPPER_DOOR.weathering().exposed())) {
            return EXPOSED_COPPER_DOOR;
        }
        if (state.is(Blocks.IRON_DOOR)) {
            return IRON_DOOR;
        }
        if (state.is(Blocks.JUNGLE_DOOR)) {
            return JUNGLE_DOOR;
        }
        if (state.is(Blocks.JUNGLE_TRAPDOOR)) {
            return JUNGLE_TRAPDOOR;
        }
        if (state.is(Blocks.MANGROVE_TRAPDOOR)) {
            return MANGROVE_TRAPDOOR;
        }
        if (state.is(Blocks.OAK_TRAPDOOR)) {
            return OAK_TRAPDOOR;
        }
        if (state.is(Blocks.POPLAR_DOOR)) {
            return POPLAR_DOOR;
        }
        if (state.is(Blocks.POPLAR_TRAPDOOR)) {
            return POPLAR_TRAPDOOR;
        }
        if (state.is(Blocks.COPPER_DOOR.weathering().weathered())) {
            return WEATHERED_COPPER_DOOR;
        }
        if (state.is(Blocks.COPPER_DOOR.weathering().oxidized())) {
            return OXIDIZED_COPPER_DOOR;
        }
        if (state.is(Blocks.COPPER_DOOR.waxed().unaffected())) {
            return WAXED_COPPER_DOOR;
        }
        if (state.is(Blocks.COPPER_DOOR.waxed().exposed())) {
            return WAXED_EXPOSED_COPPER_DOOR;
        }
        if (state.is(Blocks.COPPER_DOOR.waxed().weathered())) {
            return WAXED_WEATHERED_COPPER_DOOR;
        }
        if (state.is(Blocks.COPPER_DOOR.waxed().oxidized())) {
            return WAXED_OXIDIZED_COPPER_DOOR;
        }
        return state.is(Blocks.WARPED_TRAPDOOR) ? WARPED_TRAPDOOR : null;
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
