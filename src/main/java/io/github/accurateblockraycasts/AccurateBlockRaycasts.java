package io.github.accurateblockraycasts;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;
import io.github.accurateblockraycasts.raycast.RayProfileReloadListener;

/** Common, server-safe mod entrypoint. */
public final class AccurateBlockRaycasts implements ModInitializer {
    public static final String MOD_ID = "accurateblockraycasts";

    @Override
    public void onInitialize() {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new RayProfileReloadListener());
    }
}
