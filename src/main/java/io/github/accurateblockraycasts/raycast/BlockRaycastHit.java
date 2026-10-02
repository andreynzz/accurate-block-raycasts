package io.github.accurateblockraycasts.raycast;

import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/** The block hit returned by one vanilla block-trace attempt. */
public record BlockRaycastHit(BlockPos position, BlockState state) {
    public BlockRaycastHit {
        Objects.requireNonNull(position, "position");
        Objects.requireNonNull(state, "state");
        position = position.immutable();
    }
}
