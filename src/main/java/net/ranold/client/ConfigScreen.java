package net.ranold.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.ranold.Config;

public class ConfigScreen extends Screen {
    private final Screen lastScreen;
    private EditBox distanceEdit;

    public ConfigScreen(Screen lastScreen) {
        super(Component.translatable("ssrd.screen.config.title"));
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int startY = 80;

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
            Config.setPhysicsRenderDistance(dist);
            
            if (this.minecraft.player != null && this.minecraft.getConnection() != null) {
                net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.ranold.ClientConfigSyncPacket(dist));
            }
        } catch (NumberFormatException ignored) {}
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);
        
        guiGraphics.drawString(this.font, "Physics Render Distance (Chunks)", this.width / 2 - 100, 70, 0xA0A0A0);
    }

    @Override
    public void onClose() {
        this.saveAndClose();
    }
}
