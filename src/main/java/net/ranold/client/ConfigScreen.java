package net.ranold.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.ranold.Config;

public class ConfigScreen extends Screen {
    private final Screen lastScreen;

    public ConfigScreen(Screen lastScreen) {
        super(Component.literal("SSRD Physics Settings (Outdated, use sodium video settings instead)"));
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int startY = 60;

        // Display current distance
        Component currentText = Component.literal("Current: " + Config.physicsRenderDistance + " chunks");
        
        // Buttons for distances
        int[] distances = {32, 64, 128, 256, 512, 1024};
        for (int i = 0; i < distances.length; i++) {
            int dist = distances[i];
            this.addRenderableWidget(Button.builder(Component.literal(dist + " Chunks"), (button) -> {
                Config.setPhysicsRenderDistance(dist);
                this.rebuildWidgets(); // Refresh text
            }).bounds(centerX - 100, startY + (i * 24), 200, 20).build());
        }

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> {
            this.minecraft.setScreen(this.lastScreen);
        }).bounds(centerX - 100, this.height - 40, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);
        guiGraphics.drawCenteredString(this.font, "Current: " + Config.physicsRenderDistance + " chunks", this.width / 2, 40, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }
}
