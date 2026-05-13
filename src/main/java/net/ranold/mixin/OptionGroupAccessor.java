package net.ranold.mixin;

import com.google.common.collect.ImmutableList;
import net.caffeinemc.mods.sodium.client.gui.options.Option;
import net.caffeinemc.mods.sodium.client.gui.options.OptionGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = OptionGroup.class, remap = false)
public interface OptionGroupAccessor {
    @Accessor("options")
    ImmutableList<Option<?>> getOptions();

    @Invoker("<init>")
    static OptionGroup create(ImmutableList<Option<?>> options) {
        throw new UnsupportedOperationException();
    }
}
