package io.github.accurateblockraycasts.raycast;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.accurateblockraycasts.geometry.PixelMask;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TrapdoorMasksTest {
    @ParameterizedTest
    @MethodSource("verifiedMasks")
    void preservesAVerifiedOpeningAndTheSolidBorder(PixelMask mask, int openingColumn, int openingRow) {
        assertTrue(mask.isSolid(0, 0));
        assertFalse(mask.isSolid(openingColumn, openingRow));
    }

    private static Stream<Arguments> verifiedMasks() {
        return Stream.of(
            Arguments.of(TrapdoorMasks.acacia(), 2, 3),
            Arguments.of(TrapdoorMasks.bamboo(), 3, 3),
            Arguments.of(TrapdoorMasks.cherry(), 4, 4),
            Arguments.of(TrapdoorMasks.crimson(), 3, 3),
            Arguments.of(TrapdoorMasks.jungle(), 5, 3),
            Arguments.of(TrapdoorMasks.mangrove(), 6, 5),
            Arguments.of(TrapdoorMasks.oak(), 3, 3),
            Arguments.of(TrapdoorMasks.poplar(), 7, 5),
            Arguments.of(TrapdoorMasks.warped(), 3, 3)
        );
    }
}
