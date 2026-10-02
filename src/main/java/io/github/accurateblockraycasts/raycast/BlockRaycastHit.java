package io.github.accurateblockraycasts.raycast;

import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

/** The block hit returned by one vanilla block-trace attempt. */
public record BlockRaycastHit(BlockPos position, BlockState state, @Nullable BlockHitResult vanillaHit) {
    public BlockRaycastHit {
        Objects.requireNonNull(position, "position");
        Objects.requireNonNull(state, "state");
        position = position.immutable();
    }

    public BlockRaycastHit(BlockPos position, BlockState state) {
        this(position, state, null);
    }
}
