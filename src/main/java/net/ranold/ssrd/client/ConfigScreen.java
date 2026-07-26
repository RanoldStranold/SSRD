package net.ranold.ssrd.client;
import net.ranold.ssrd.ClientConfigSyncPacket;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.ranold.ssrd.Config;

public class ConfigScreen extends Screen {
    private final Screen lastScreen;
    private EditBox distanceEdit;
    private DistanceLimits.EffectiveMax effectiveMax;

    public ConfigScreen(Screen lastScreen) {
        super(Component.translatable("ssrd.screen.config.title"));
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int startY = 80;

        this.effectiveMax = DistanceLimits.query(Config.minPhysicsRenderDistance);

        // Distance Input
        this.distanceEdit = new EditBox(this.font, centerX - 100, startY, 200, 20, Component.literal("Distance"));
        this.distanceEdit.setValue(String.valueOf(Config.physicsRenderDistance));
        this.distanceEdit.setFilter(s -> s.isEmpty() || s.matches("\\d+"));
        this.addRenderableWidget(this.distanceEdit);

        // Done button (Saves changes)
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> {
            this.saveAndClose();
        }).bounds(centerX - 100, this.height - 40, 200, 20).build());
    }

    private void saveAndClose() {
        try {
            int dist = Integer.parseInt(this.distanceEdit.getValue());
            // Respect the server's synced distance cap while connected (issue #47).
            // Singleplayer/LAN hosts are never capped.
            int serverCap = net.ranold.ssrd.ssrd.serverMaxTrackingChunks;
            if (serverCap > 0 && this.minecraft.getConnection() != null && !this.minecraft.hasSingleplayerServer()) {
                dist = Math.min(dist, serverCap);
            }
            dist = Math.max(dist, 1);
            Config.setPhysicsRenderDistance(dist);
            
            if (this.minecraft.player != null && this.minecraft.getConnection() != null) {
                net.neoforged.neoforge.network.PacketDistributor.sendToServer(new ClientConfigSyncPacket(dist));
            }
        } catch (NumberFormatException ignored) {}
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);
        
        guiGraphics.drawString(this.font, "Physics Render Distance (Chunks)", this.width / 2 - 100, 70, 0xA0A0A0);
        if (this.effectiveMax != null) {
            String label = switch (this.effectiveMax.source()) {
                case "Server" -> "Max: " + this.effectiveMax.chunks() + " chunks (Server limit)";
                case "Distant Horizons" -> "Max: " + this.effectiveMax.chunks() + " chunks (Distant Horizons LOD distance)";
                case "Voxy" -> "Max: " + this.effectiveMax.chunks() + " chunks (Voxy LOD distance)";
                default -> "Max: " + this.effectiveMax.chunks() + " chunks";
            };
            guiGraphics.drawString(this.font, label, this.width / 2 - 100, 106, 0xA0A0A0);
        }
    }

    @Override
    public void onClose() {
        this.saveAndClose();
    }
}
