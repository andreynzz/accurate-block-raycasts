package io.github.accurateblockraycasts.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DoorProfileGeneratorTest {
    @Test
    void convertsTransparencyToBottomToTopMaskRows() {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 15, 0xFF000000);
        image.setRGB(15, 0, 0x7F000000);

        List<String> rows = DoorProfileGenerator.toRows(image);

        assertEquals("#...............", rows.get(0));
        assertEquals("...............#", rows.get(15));
        assertEquals("................", rows.get(8));
    }

    @Test
    void rejectsTexturesThatAreNotSixteenBySixteen() {
        assertThrows(IllegalArgumentException.class, () -> DoorProfileGenerator.toRows(new BufferedImage(15, 16, BufferedImage.TYPE_INT_ARGB)));
    }

    @Test
    void generatesAProfileFromAZippedResourcePack(@TempDir Path temporary) throws Exception {
        Path pack = temporary.resolve("pack.zip");
        Path output = temporary.resolve("profile.json");
        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(pack))) {
            entry(zip, "assets/example/models/block/test_door_bottom_left.json", "{\"textures\":{\"bottom\":\"example:block/bottom\",\"top\":\"example:block/top\"}}".getBytes());
            entry(zip, "assets/example/textures/block/bottom.png", png());
            entry(zip, "assets/example/textures/block/top.png", png());
        }
        DoorProfileGenerator.main(new String[] {"--block", "example:test_door", "--pack", pack.toString(), "--output", output.toString()});
        assertEquals(true, Files.readString(output).contains("\"block\": \"example:test_door\""));
    }

    private static void entry(ZipOutputStream zip, String name, byte[] bytes) throws Exception {
        zip.putNextEntry(new ZipEntry(name)); zip.write(bytes); zip.closeEntry();
    }

    private static byte[] png() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), "png", bytes);
        return bytes.toByteArray();
    }
}
