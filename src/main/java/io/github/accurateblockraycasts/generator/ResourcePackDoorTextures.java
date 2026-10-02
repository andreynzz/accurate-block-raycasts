package io.github.accurateblockraycasts.generator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/** Resolves the two texture slots used by the conventional vanilla door models in a directory pack. */
final class ResourcePackDoorTextures {
    private ResourcePackDoorTextures() {
    }

    static Textures find(Path pack, String blockId) throws IOException {
        Id block = Id.parse(blockId);
        Map<String, String> textures = textures(pack, new Id(block.namespace(), "block/" + block.path() + "_bottom_left"));
        return new Textures(texture(pack, textures, "bottom"), texture(pack, textures, "top"));
    }

    private static Path texture(Path pack, Map<String, String> textures, String slot) {
        String value = resolve(textures, slot);
        Id id = Id.parse(value);
        Path path = pack.resolve("assets").resolve(id.namespace()).resolve("textures").resolve(id.path() + ".png");
        if (!Files.isRegularFile(path)) throw new IllegalArgumentException("missing texture " + path);
        return path;
    }

    private static String resolve(Map<String, String> textures, String slot) {
        String value = textures.get(slot);
        if (value == null) throw new IllegalArgumentException("door model does not declare texture slot '" + slot + "'");
        while (value.startsWith("#")) {
            value = textures.get(value.substring(1));
            if (value == null) throw new IllegalArgumentException("unresolved texture reference");
        }
        return value;
    }

    private static Map<String, String> textures(Path pack, Id model) throws IOException {
        Path path = pack.resolve("assets").resolve(model.namespace()).resolve("models").resolve(model.path() + ".json");
        if (!Files.isRegularFile(path)) throw new IllegalArgumentException("missing model " + path);
        try (Reader reader = Files.newBufferedReader(path)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            Map<String, String> childTextures = new HashMap<>();
            if (json.has("textures")) for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("textures").entrySet()) childTextures.put(entry.getKey(), entry.getValue().getAsString());
            if (childTextures.containsKey("bottom") && childTextures.containsKey("top")) return childTextures;
            Map<String, String> result = json.has("parent") ? textures(pack, Id.parse(json.get("parent").getAsString())) : new HashMap<>();
            result.putAll(childTextures);
            return result;
        }
    }

    record Textures(Path bottom, Path top) { }
    private record Id(String namespace, String path) {
        static Id parse(String value) {
            int separator = value.indexOf(':');
            return separator < 0 ? new Id("minecraft", value) : new Id(value.substring(0, separator), value.substring(separator + 1));
        }
    }
}
