package io.github.accurateblockraycasts.raycast;

/**
 * The decision made by a supported block's shared ray profile.
 *
 * <p>{@link #OPEN} tells the caller that the profile's geometry contains an
 * opening at the relevant ray intersection, so a later traversal may continue
 * beyond this block. {@link #SOLID} tells the caller that the profile confirms
 * vanilla blocking at that point. {@link #NO_SPECIAL_RESULT} leaves the
 * decision entirely to vanilla behavior.
 */
public enum RayProfileResult {
    OPEN,
    SOLID,
    NO_SPECIAL_RESULT
}
