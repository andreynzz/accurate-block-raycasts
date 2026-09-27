package io.github.accurateblockraycasts;

import net.fabricmc.api.ModInitializer;

/** Common mod entrypoint; gameplay integrations are intentionally deferred. */
public final class AccurateBlockRaycasts implements ModInitializer {
    public static final String MOD_ID = "accurateblockraycasts";

    @Override
    public void onInitialize() {
        // Geometry is deliberately independent from Minecraft gameplay hooks.
    }
}
