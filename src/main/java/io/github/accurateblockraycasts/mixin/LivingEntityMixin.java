package io.github.accurateblockraycasts.mixin;

import io.github.accurateblockraycasts.raycast.LineOfSightRaycaster;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Redirects the common Minecraft 26.3 living-entity visibility trace only. */
@Mixin(LivingEntity.class)
abstract class LivingEntityMixin {
    @Redirect(
        method = "hasLineOfSight(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/ClipContext$Block;Lnet/minecraft/world/level/ClipContext$Fluid;D)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;clip(Lnet/minecraft/world/level/ClipContext;)Lnet/minecraft/world/phys/BlockHitResult;"
        )
    )
    private BlockHitResult accurateBlockRaycasts$tracePerforatedDoor(Level level, ClipContext context) {
        return LineOfSightRaycaster.trace(level, context);
    }
}
