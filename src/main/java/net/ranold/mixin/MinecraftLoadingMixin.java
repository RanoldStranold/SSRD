package net.ranold.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftLoadingMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void ssd$confirmLoading(CallbackInfo ci) {
        com.mojang.logging.LogUtils.getLogger().info("SSRD: MOD IS ACTIVE AND RUNNING");
    }
}
