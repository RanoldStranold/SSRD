package net.ranold.mixin;

import com.google.common.collect.ImmutableList;
import net.caffeinemc.mods.sodium.client.gui.options.OptionGroup;
import net.caffeinemc.mods.sodium.client.gui.options.OptionPage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = OptionPage.class, remap = false)
public interface OptionPageAccessor {
    @Accessor("groups")
    ImmutableList<OptionGroup> getGroups();
}
