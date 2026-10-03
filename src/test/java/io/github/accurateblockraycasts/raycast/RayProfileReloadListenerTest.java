package io.github.accurateblockraycasts.raycast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RayProfileReloadListenerTest {
    private static final Identifier TEST_ID = Identifier.fromNamespaceAndPath("test", "ray_profiles/birch_door.json");

    @BeforeAll
    static void bootstrapVanillaRegistries() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void parsesADoorProfileForAnOtherwiseUnsupportedBlock() {
        RayProfile profile = RayProfileReloadListener.parseProfile(doorProfile("minecraft:birch_door", "."), TEST_ID);

        assertEquals(
            RayProfileResult.OPEN,
            profile.evaluate(
                Blocks.BIRCH_DOOR.defaultBlockState(),
                BlockPos.ZERO,
                new io.github.accurateblockraycasts.geometry.Ray(
                    new io.github.accurateblockraycasts.geometry.Vec3(0.5, 0.5, -1.0),
                    new io.github.accurateblockraycasts.geometry.Vec3(0.5, 0.5, 1.0)
                )
            )
        );
    }

    @Test
    void rejectsProfilesWithAnInvalidBlockTypeOrMask() {
        assertThrows(IllegalArgumentException.class, () -> RayProfileReloadListener.parseProfile(doorProfile("minecraft:stone", "."), TEST_ID));

        JsonObject malformedMask = doorProfile("minecraft:birch_door", ".");
        malformedMask.getAsJsonArray("upper").remove(15);
        assertThrows(IllegalArgumentException.class, () -> RayProfileReloadListener.parseProfile(malformedMask, TEST_ID));
    }

    private static JsonObject doorProfile(String block, String pixel) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "door");
        json.addProperty("block", block);
        json.add("lower", rows(pixel));
        json.add("upper", rows(pixel));
        return json;
    }

    private static JsonArray rows(String pixel) {
        JsonArray rows = new JsonArray();
        String row = pixel.repeat(16);
        for (int index = 0; index < 16; index++) {
            rows.add(row);
        }
        return rows;
    }
}
