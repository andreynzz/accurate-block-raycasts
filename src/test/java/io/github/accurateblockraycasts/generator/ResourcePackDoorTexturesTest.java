package io.github.accurateblockraycasts.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ResourcePackDoorTexturesTest {
    @Test
    void resolvesTextureAliasesAndParentsInAPack(@TempDir Path pack) throws Exception {
        Path models = pack.resolve("assets/example/models/block");
        Path textures = pack.resolve("assets/example/textures/block");
        Files.createDirectories(models);
        Files.createDirectories(textures);
        Files.writeString(models.resolve("test_door_bottom_left.json"), "{\"parent\":\"example:block/door_parent\",\"textures\":{\"bottom\":\"#lower\"}}");
        Files.writeString(models.resolve("door_parent.json"), "{\"textures\":{\"lower\":\"example:block/bottom\",\"top\":\"example:block/top\"}}");
        ImageIO.write(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), "png", textures.resolve("bottom.png").toFile());
        ImageIO.write(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), "png", textures.resolve("top.png").toFile());

        ResourcePackDoorTextures.Textures found = ResourcePackDoorTextures.find(pack, "example:test_door");

        assertEquals(textures.resolve("bottom.png"), found.bottom());
        assertEquals(textures.resolve("top.png"), found.top());
    }
}
