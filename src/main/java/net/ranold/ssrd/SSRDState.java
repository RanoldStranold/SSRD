package net.ranold.ssrd;

public class SSRDState {
    public static final ThreadLocal<Boolean> IS_SUBLEVEL_RENDER = ThreadLocal.withInitial(() -> false);
    public static boolean SUBLEVELS_VISIBLE_THIS_FRAME = false;
    public static boolean DONE_DH_PASS = false;
    public static final ThreadLocal<Boolean> IS_DH_PASS = ThreadLocal.withInitial(() -> false);

    // Final level-pass projection matrix (SSRD-extended far plane + this frame's view-bob),
    // captured client-side at GameRenderer.resetProjectionMatrix. Null until first level frame.
    public static volatile org.joml.Matrix4f LEVEL_PROJ_MATRIX = null;

    // Pure (pre-view-bob) SSRD-extended projection from GameRenderer.getProjectionMatrix.
    // Needed because the matrices passed around during rendering have the bob transform
    // multiplied in, which makes their m22/m32 unusable as perspective coefficients.
    public static volatile org.joml.Matrix4f PURE_PROJ_MATRIX = null;
}
