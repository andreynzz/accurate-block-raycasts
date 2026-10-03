package io.github.accurateblockraycasts.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        Path blockstates = pack.resolve("assets/example/blockstates");
        Files.createDirectories(models);
        Files.createDirectories(textures);
        Files.createDirectories(blockstates);
        Files.writeString(blockstates.resolve("test_door.json"), "{\"variants\":{\"facing=east,half=lower,hinge=left,open=false\":{\"model\":\"example:block/custom_closed_model\"}}}");
        Files.writeString(models.resolve("custom_closed_model.json"), "{\"parent\":\"example:block/door_parent\",\"textures\":{\"bottom\":\"#lower\"}}");
        Files.writeString(models.resolve("door_parent.json"), "{\"textures\":{\"lower\":\"example:block/bottom\",\"top\":\"example:block/top\"}}");
        ImageIO.write(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), "png", textures.resolve("bottom.png").toFile());
        ImageIO.write(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), "png", textures.resolve("top.png").toFile());

        ResourcePackDoorTextures.Textures found = ResourcePackDoorTextures.find(pack, "example:test_door");

        assertEquals(textures.resolve("bottom.png"), found.bottom());
        assertEquals(textures.resolve("top.png"), found.top());
    }

    @Test
    void rejectsBlockstateAlternativesWithDifferentModels(@TempDir Path pack) throws Exception {
        Path blockstates = pack.resolve("assets/example/blockstates");
        Files.createDirectories(blockstates);
        Files.writeString(blockstates.resolve("test_door.json"), "{\"variants\":{\"facing=east,half=lower,hinge=left,open=false\":[{\"model\":\"example:block/a\"},{\"model\":\"example:block/b\"}]}}");
        assertThrows(IllegalArgumentException.class, () -> ResourcePackDoorTextures.find(pack, "example:test_door"));
    }
}
