package net.ranold.ssrd;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class SSRDMixinPlugin implements IMixinConfigPlugin {
    private static boolean isDHLoaded = false;
    private static boolean isVoxyLoaded = false;

    @Override
    public void onLoad(String mixinPackage) {
        try {
            Class.forName("com.seibel.distanthorizons.api.DhApi", false, this.getClass().getClassLoader());
            isDHLoaded = true;
        } catch (ClassNotFoundException e) {
            isDHLoaded = false;
        }

        try {
            Class.forName("me.cortex.voxy.Voxy", false, this.getClass().getClassLoader());
            isVoxyLoaded = true;
        } catch (ClassNotFoundException e) {
            isVoxyLoaded = false;
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains("DH") || mixinClassName.contains("DistantHorizons")) {
            if (!isDHLoaded) {
                return false;
            }
        }

        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            if (mixinClassName.contains("client") ||
                mixinClassName.contains("Viewport") || 
                mixinClassName.contains("Sodium") || 
                mixinClassName.contains("LevelRenderer") ||
                mixinClassName.contains("GameRenderer") ||
                mixinClassName.contains("VanillaDispatcher") ||
                mixinClassName.contains("EntityRenderDispatcher") ||
                mixinClassName.contains("BlockEntityRenderer") ||
                mixinClassName.contains("OcclusionCuller")) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
