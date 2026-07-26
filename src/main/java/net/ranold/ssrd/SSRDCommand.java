package net.ranold.ssrd;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.ticket.SubLevelLoadingTicket;
import dev.ryanhcode.sable.api.sublevel.ticket.SubLevelLoadingTicketType;
import dev.ryanhcode.sable.api.sublevel.ticket.SubLevelTicketInfo;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.UUID;

public class SSRDCommand {

    public static final SubLevelLoadingTicketType<UUID> PLAYER_FORCED = SubLevelLoadingTicketType.create(ResourceLocation.fromNamespaceAndPath("ssrd", "player_forced"), UUIDUtil.CODEC);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        try {
            Class<?> subLevelArgClass = Class.forName("dev.ryanhcode.sable.api.command.SubLevelArgumentType");
            ArgumentType<?> subLevelArg = (ArgumentType<?>) subLevelArgClass.getMethod("subLevels").invoke(null);

            dispatcher.register(Commands.literal("ssrd")
                    .then(Commands.literal("forceload")
                            .then(Commands.literal("add")
                                    .then(Commands.argument("sub_levels", subLevelArg)
                                            .executes(SSRDCommand::addForceload)))
                            .then(Commands.literal("remove")
                                    .then(Commands.argument("sub_levels", subLevelArg)
                                            .executes(SSRDCommand::removeForceload)))
                            .then(Commands.literal("list")
                                    .executes(SSRDCommand::listForceload))
                    )
                    .then(Commands.literal("help")
                            .executes(SSRDCommand::showHelp))
            );
        } catch (Exception e) {
            com.mojang.logging.LogUtils.getLogger().error("SSRD: Failed to register commands", e);
        }
    }

    private static int addForceload(CommandContext<CommandSourceStack> context) {
        try {
            Class<?> subLevelArgClass = Class.forName("dev.ryanhcode.sable.api.command.SubLevelArgumentType");
            Collection<?> subLevels = (Collection<?>) subLevelArgClass.getMethod("getSubLevels", CommandContext.class, String.class).invoke(null, context, "sub_levels");
            
            CommandSourceStack source = context.getSource();
            if (!source.isPlayer()) {
                source.sendFailure(Component.literal("Only players can run this command."));
                return 0;
            }
            
            ServerPlayer player = source.getPlayerOrException();
            ServerSubLevelContainer container = SubLevelContainer.getContainer(source.getLevel());
            if (container == null) return 0;
            
            int limit = source.getLevel().getGameRules().getInt(SSRDGameRules.RULE_SSRD_FORCELOAD_LIMIT);
            
            int currentForceloaded = 0;
            for (SubLevelTicketInfo info : container.getAllTickets().values()) {
                for (SubLevelLoadingTicket<?> ticket : info.tickets()) {
                    if (ticket.getType() == PLAYER_FORCED && player.getUUID().equals(ticket.getKey())) {
                        currentForceloaded++;
                        break;
                    }
                }
            }
            
            int added = 0;
            for (Object sl : subLevels) {
                if (sl instanceof ServerSubLevel) {
                    boolean hasTicket = false;
                    UUID uuid = (UUID) sl.getClass().getMethod("getUniqueId").invoke(sl);
                    SubLevelTicketInfo info = container.getAllTickets().get(uuid);
                    if (info != null) {
                        for (SubLevelLoadingTicket<?> ticket : info.tickets()) {
                            if (ticket.getType() == PLAYER_FORCED && player.getUUID().equals(ticket.getKey())) {
                                hasTicket = true;
                                break;
                            }
                        }
                    }
                    
                    if (!hasTicket) {
                        if (currentForceloaded + added >= limit) {
                            source.sendFailure(Component.literal("Unable to forceload, maximum amount of forceloaded sub levels is " + limit + " per player."));
                            break;
                        }
                        if (container.addForceLoadTicket((ServerSubLevel) sl, PLAYER_FORCED, player.getUUID())) {
                            added++;
                        }
                    }
                }
            }
            
            if (added > 0) {
                int finalAdded = added;
                int totalUsed = currentForceloaded + added;
                source.sendSuccess(() -> Component.literal("Added force-loading tickets for " + finalAdded + " sub-level(s). (" + totalUsed + "/" + limit + ")"), true);
            } else {
                source.sendFailure(Component.literal("No new tickets added."));
            }
            return added;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Error: " + e.getMessage()));
            return 0;
        }
    }

    private static int removeForceload(CommandContext<CommandSourceStack> context) {
        try {
            Class<?> subLevelArgClass = Class.forName("dev.ryanhcode.sable.api.command.SubLevelArgumentType");
            Collection<?> subLevels = (Collection<?>) subLevelArgClass.getMethod("getSubLevels", CommandContext.class, String.class).invoke(null, context, "sub_levels");
            
            CommandSourceStack source = context.getSource();
            if (!source.isPlayer()) {
                source.sendFailure(Component.literal("Only players can run this command."));
                return 0;
            }
            ServerPlayer player = source.getPlayerOrException();
            ServerSubLevelContainer container = SubLevelContainer.getContainer(source.getLevel());
            if (container == null) return 0;
            
            int removed = 0;
            for (Object sl : subLevels) {
                if (sl instanceof ServerSubLevel) {
                    if (container.removeForceLoadTicket((ServerSubLevel) sl, PLAYER_FORCED, player.getUUID())) {
                        removed++;
                    }
                }
            }
            
            if (removed > 0) {
                int finalRemoved = removed;
                source.sendSuccess(() -> Component.literal("Removed force-loading tickets for " + finalRemoved + " sub-level(s)."), true);
            } else {
                source.sendFailure(Component.literal("No tickets removed."));
            }
            return removed;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Error: " + e.getMessage()));
            return 0;
        }
    }

    private static int showHelp(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        int forceloadLimit = SSRDGameRules.DEFAULT_FORCELOAD_LIMIT;
        int maxRenderDistance = SSRDGameRules.DEFAULT_MAX_RENDER_DISTANCE;
        boolean dedicated = false;
        if (source.getServer() != null) {
            forceloadLimit = source.getServer().getGameRules().getInt(SSRDGameRules.RULE_SSRD_FORCELOAD_LIMIT);
            maxRenderDistance = source.getServer().getGameRules().getInt(SSRDGameRules.RULE_SSRD_MAX_RENDER_DISTANCE);
            dedicated = source.getServer().isDedicatedServer();
        }

        source.sendSuccess(() -> Component.literal("§6=== SSRD Commands ==="), false);
        source.sendSuccess(() -> Component.literal("§e/ssrd forceload add <sub_levels>§r - Keeps the selected sub-levels loaded and visible even when no players are nearby. Counts toward your per-player limit."), false);
        source.sendSuccess(() -> Component.literal("§e/ssrd forceload remove <sub_levels>§r - Removes your forceload tickets from the selected sub-levels."), false);
        source.sendSuccess(() -> Component.literal("§e/ssrd forceload list§r - Lists the sub-levels you have forceloaded and your limit (used/max)."), false);
        source.sendSuccess(() -> Component.literal("§e/ssrd config§r - (Client) Opens the SSRD screen to set your physics render distance manually. Also available as a slider in Sodium's video settings."), false);

        final String forceloadLine = "§e/gamerule ssrdForceloadLimit§r - Maximum sub-levels each player can forceload. Current: §b" + forceloadLimit + "§r, Default: §7" + SSRDGameRules.DEFAULT_FORCELOAD_LIMIT;
        source.sendSuccess(() -> Component.literal("§6=== SSRD Gamerules ==="), false);
        source.sendSuccess(() -> Component.literal(forceloadLine), false);
        if (dedicated) {
            // The cap never applies to the singleplayer host, so only mention it on dedicated servers
            final String maxDistLine = "§e/gamerule ssrdMaxRenderDistance§r - Server-wide cap (in chunks) on the physics render distance clients can request. Current: §b" + maxRenderDistance + "§r, Default: §7" + SSRDGameRules.DEFAULT_MAX_RENDER_DISTANCE;
            source.sendSuccess(() -> Component.literal(maxDistLine), false);
        }
        return 1;
    }

    /** Removes ssrdMaxRenderDistance from the /gamerule command tree (used on integrated servers, where the cap never applies to the host). */
    public static void hideMaxRenderDistanceGamerule(CommandDispatcher<CommandSourceStack> dispatcher) {
        try {
            com.mojang.brigadier.tree.CommandNode<CommandSourceStack> gamerule = dispatcher.getRoot().getChild("gamerule");
            if (gamerule == null) return;

            java.lang.reflect.Field childrenField = com.mojang.brigadier.tree.CommandNode.class.getDeclaredField("children");
            java.lang.reflect.Field literalsField = com.mojang.brigadier.tree.CommandNode.class.getDeclaredField("literals");
            childrenField.setAccessible(true);
            literalsField.setAccessible(true);
            ((java.util.Map<?, ?>) childrenField.get(gamerule)).remove("ssrdMaxRenderDistance");
            ((java.util.Map<?, ?>) literalsField.get(gamerule)).remove("ssrdMaxRenderDistance");
        } catch (Exception e) {
            com.mojang.logging.LogUtils.getLogger().warn("SSRD: Could not hide ssrdMaxRenderDistance gamerule on integrated server", e);
        }
    }

    private static int listForceload(CommandContext<CommandSourceStack> context) {
        try {
            CommandSourceStack source = context.getSource();
            if (!source.isPlayer()) {
                source.sendFailure(Component.literal("Only players can run this command."));
                return 0;
            }
            ServerPlayer player = source.getPlayerOrException();
            ServerSubLevelContainer container = SubLevelContainer.getContainer(source.getLevel());
            if (container == null) return 0;
            
            int limit = source.getLevel().getGameRules().getInt(SSRDGameRules.RULE_SSRD_FORCELOAD_LIMIT);
            java.util.List<UUID> uuids = new java.util.ArrayList<>();
            for (var entry : container.getAllTickets().entrySet()) {
                for (SubLevelLoadingTicket<?> ticket : entry.getValue().tickets()) {
                    if (ticket.getType() == PLAYER_FORCED && player.getUUID().equals(ticket.getKey())) {
                        uuids.add(entry.getKey());
                        break;
                    }
                }
            }
            
            int count = uuids.size();
            source.sendSuccess(() -> Component.literal("Your forceloaded sub-levels: (" + count + "/" + limit + ")"), false);
            for (UUID uuid : uuids) {
                source.sendSuccess(() -> Component.literal("- " + uuid), false);
            }
            if (count == 0) {
                source.sendSuccess(() -> Component.literal("None."), false);
            }
            return count;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Error: " + e.getMessage()));
            return 0;
        }
    }
}
