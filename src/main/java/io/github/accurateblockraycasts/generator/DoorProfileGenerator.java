package io.github.accurateblockraycasts.generator;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Offline tool that converts transparent 16-by-16 door textures into the
 * server datapack profile format. This class is never used by gameplay.
 */
public final class DoorProfileGenerator {
    private static final int SIZE = 16;

    private DoorProfileGenerator() {
    }

    public static void main(String[] arguments) throws IOException {
        Arguments args = Arguments.parse(arguments);
        List<String> lower;
        List<String> upper;
        if (args.pack() == null) {
            lower = toRows(read(args.bottom()));
            upper = toRows(read(args.top()));
        } else if (Files.isDirectory(args.pack())) {
            ResourcePackDoorTextures.Textures textures = ResourcePackDoorTextures.find(args.pack(), args.block());
            lower = toRows(read(textures.bottom()));
            upper = toRows(read(textures.top()));
        } else {
            try (FileSystem zip = FileSystems.newFileSystem(args.pack())) {
                ResourcePackDoorTextures.Textures textures = ResourcePackDoorTextures.find(zip.getPath("/"), args.block());
                lower = toRows(read(textures.bottom()));
                upper = toRows(read(textures.top()));
            }
        }
        Path parent = args.output().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.writeString(args.output(), json(args.block(), lower, upper));
    }

    static List<String> toRows(BufferedImage image) {
        if (image.getWidth() != SIZE || image.getHeight() != SIZE) {
            throw new IllegalArgumentException("expected a 16x16 PNG, got " + image.getWidth() + "x" + image.getHeight());
        }
        String[] rows = new String[SIZE];
        for (int row = 0; row < SIZE; row++) {
            StringBuilder pattern = new StringBuilder(SIZE);
            for (int column = 0; column < SIZE; column++) {
                int alpha = image.getRGB(column, SIZE - 1 - row) >>> 24;
                pattern.append(alpha == 0 ? '.' : '#');
            }
            rows[row] = pattern.toString();
        }
        return List.of(rows);
    }

    private static BufferedImage read(Path path) throws IOException {
        try (InputStream input = Files.newInputStream(path)) {
            BufferedImage image = ImageIO.read(input);
            if (image == null) {
                throw new IllegalArgumentException(path + " is not a readable image");
            }
            return image;
        }
    }

    private static String json(String block, List<String> lower, List<String> upper) {
        return "{\n  \"type\": \"door\",\n  \"block\": \"" + block + "\",\n  \"lower\": " + rows(lower)
            + ",\n  \"upper\": " + rows(upper) + "\n}\n";
    }

    private static String rows(List<String> rows) {
        return "[\n    \"" + String.join("\",\n    \"", rows) + "\"\n  ]";
    }

    private record Arguments(String block, Path bottom, Path top, Path pack, Path output) {
        private static Arguments parse(String[] arguments) {
            String block = value(arguments, "--block");
            String pack = optionalValue(arguments, "--pack");
            Path bottom = pack == null ? Path.of(value(arguments, "--bottom")) : null;
            Path top = pack == null ? Path.of(value(arguments, "--top")) : null;
            Path output = Path.of(value(arguments, "--output"));
            return new Arguments(block, bottom, top, pack == null ? null : Path.of(pack), output);
        }

        private static String value(String[] arguments, String option) {
            for (int index = 0; index < arguments.length - 1; index += 2) {
                if (arguments[index].equals(option)) {
                    return arguments[index + 1];
                }
            }
            throw usage();
        }

        private static String optionalValue(String[] arguments, String option) {
            for (int index = 0; index < arguments.length - 1; index += 2) if (arguments[index].equals(option)) return arguments[index + 1];
            return null;
        }

        private static IllegalArgumentException usage() {
            return new IllegalArgumentException("usage: --block <namespace:block> (--bottom <bottom.png> --top <top.png> | --pack <pack-dir>) --output <profile.json>");
        }
    }
}
