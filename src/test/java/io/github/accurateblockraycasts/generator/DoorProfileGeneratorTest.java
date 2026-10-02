package io.github.accurateblockraycasts.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.image.BufferedImage;
import java.util.List;
import org.junit.jupiter.api.Test;

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
}
