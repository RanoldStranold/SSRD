package net.ranold;

public class SSRDState {
    public static final ThreadLocal<Boolean> IS_SUBLEVEL_RENDER = ThreadLocal.withInitial(() -> false);
    public static boolean SUBLEVELS_VISIBLE_THIS_FRAME = false;
    public static boolean DONE_DH_PASS = false;
    public static final ThreadLocal<Boolean> IS_DH_PASS = ThreadLocal.withInitial(() -> false);
}
