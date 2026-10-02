package io.github.accurateblockraycasts.mixin;

import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Exposes immutable clip settings so a retried trace can preserve vanilla semantics. */
@Mixin(ClipContext.class)
public interface ClipContextAccessor {
    @Accessor("block")
    ClipContext.Block accurateBlockRaycasts$block();

    @Accessor("fluid")
    ClipContext.Fluid accurateBlockRaycasts$fluid();

    @Accessor("collisionContext")
    CollisionContext accurateBlockRaycasts$collisionContext();
}
