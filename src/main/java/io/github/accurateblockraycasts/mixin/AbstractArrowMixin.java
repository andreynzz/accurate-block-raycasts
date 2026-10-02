package io.github.accurateblockraycasts.mixin;

import io.github.accurateblockraycasts.raycast.ArrowBlockRaycaster;
import io.github.accurateblockraycasts.raycast.RayProfileRegistry;
import io.github.accurateblockraycasts.geometry.Vec3;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Minecraft 26.3 traces an arrow's block path directly in
 * {@link AbstractArrow#tick()}. Redirecting this single invocation preserves
 * all other level clipping and lets the shared traversal retain later hits.
 */
@Mixin(AbstractArrow.class)
abstract class AbstractArrowMixin {
    @Redirect(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
        )
    )
    private BlockState accurateBlockRaycasts$skipOpeningForInGroundCheck(Level level, net.minecraft.core.BlockPos position) {
        BlockState state = level.getBlockState(position);
        if (!level.isClientSide() && RayProfileRegistry.INSTANCE.isPassableAt(
            state,
            position,
            new Vec3(((AbstractArrow) (Object) this).getX(), ((AbstractArrow) (Object) this).getY(), ((AbstractArrow) (Object) this).getZ())
        )) {
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Redirect(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;clipIncludingBorder(Lnet/minecraft/world/level/ClipContext;)Lnet/minecraft/world/phys/BlockHitResult;"
        )
    )
    private BlockHitResult accurateBlockRaycasts$tracePerforatedDoor(Level level, ClipContext context) {
        return ArrowBlockRaycaster.trace(level, (AbstractArrow) (Object) this, context);
    }
}
