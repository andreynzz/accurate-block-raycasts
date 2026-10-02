package io.github.accurateblockraycasts.raycast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.accurateblockraycasts.AccurateBlockRaycasts;
import io.github.accurateblockraycasts.geometry.DoorMask;
import io.github.accurateblockraycasts.geometry.PixelMask;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Loads opt-in server-data profiles from each pack's {@code accurateblockraycasts/ray_profiles} data directory. */
public final class RayProfileReloadListener extends SimplePreparableReloadListener<Map<Block, RayProfile>>
    implements IdentifiableResourceReloadListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccurateBlockRaycasts.MOD_ID + "/ray_profiles");
    private static final String DIRECTORY = AccurateBlockRaycasts.MOD_ID + "/ray_profiles";

    @Override
    public Identifier getFabricId() {
        return Identifier.fromNamespaceAndPath(AccurateBlockRaycasts.MOD_ID, "ray_profiles");
    }

    @Override
    protected Map<Block, RayProfile> prepare(ResourceManager manager, ProfilerFiller profiler) {
        Map<Block, RayProfile> profiles = new HashMap<>();
        manager.listResources(DIRECTORY, id -> id.getPath().endsWith(".json")).forEach((id, resource) -> load(id, resource, profiles));
        return Map.copyOf(profiles);
    }

    @Override
    protected void apply(Map<Block, RayProfile> profiles, ResourceManager manager, ProfilerFiller profiler) {
        RayProfileRegistry.INSTANCE.replaceDataProfiles(profiles);
        LOGGER.info("Loaded {} data-driven ray profiles", profiles.size());
    }

    private static void load(Identifier resourceId, Resource resource, Map<Block, RayProfile> profiles) {
        try (Reader reader = resource.openAsReader()) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            String type = requiredString(json, "type");
            Block block = requiredBlock(json);
            RayProfile profile = switch (type) {
                case "door" -> new DoorRayProfile(requireDoor(block, resourceId), DoorMask.fromHalves(mask(json, "lower"), mask(json, "upper")));
                case "trapdoor" -> new TrapdoorRayProfile(requireTrapdoor(block, resourceId), mask(json, "mask"));
                default -> throw new IllegalArgumentException("unknown profile type '" + type + "'");
            };
            if (profiles.containsKey(block)) {
                throw new IllegalArgumentException("more than one profile targets block " + BuiltInRegistries.BLOCK.getKey(block));
            }
            profiles.put(block, profile);
        } catch (IOException | RuntimeException exception) {
            LOGGER.error("Ignoring invalid ray profile {} from {}: {}", resourceId, resource.sourcePackId(), exception.getMessage());
        }
    }

    private static Block requiredBlock(JsonObject json) {
        Identifier id = Identifier.tryParse(requiredString(json, "block"));
        if (id == null) {
            throw new IllegalArgumentException("'block' is not a valid identifier");
        }
        return BuiltInRegistries.BLOCK.getOptional(id)
            .orElseThrow(() -> new IllegalArgumentException("unknown block " + id));
    }

    private static DoorBlock requireDoor(Block block, Identifier resourceId) {
        if (block instanceof DoorBlock door) {
            return door;
        }
        throw new IllegalArgumentException(resourceId + " declares a door profile for a non-door block");
    }

    private static TrapDoorBlock requireTrapdoor(Block block, Identifier resourceId) {
        if (block instanceof TrapDoorBlock trapdoor) {
            return trapdoor;
        }
        throw new IllegalArgumentException(resourceId + " declares a trapdoor profile for a non-trapdoor block");
    }

    private static PixelMask mask(JsonObject json, String field) {
        if (!json.has(field) || !json.get(field).isJsonArray()) {
            throw new IllegalArgumentException("'" + field + "' must be an array of 16 rows");
        }
        JsonArray rows = json.getAsJsonArray(field);
        return PixelMask.fromRows(List.of(rows.asList().stream().map(element -> element.getAsString()).toArray(String[]::new)));
    }

    private static String requiredString(JsonObject json, String field) {
        if (!json.has(field) || !json.get(field).isJsonPrimitive()) {
            throw new IllegalArgumentException("'" + field + "' must be a string");
        }
        return json.get(field).getAsString();
    }
}
