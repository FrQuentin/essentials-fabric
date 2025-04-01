package fr.quentin.essentials.mixin;

import fr.quentin.essentials.EssentialsClient;
import fr.quentin.essentials.gui.screen.ButtonManager;
import fr.quentin.essentials.gui.screen.EssentialsConfigurationScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {
    @Shadow
    private ButtonWidget exitButton;

    public GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void init(CallbackInfo info) {
        if (this.client == null || exitButton == null) {
            EssentialsClient.LOGGER.error("Cannot initialize buttons: client or exitButton is null");
            return;
        }

        int buttonSize = 20;
        int padding = 4;
        int largePadding = 12;

        int folderX = exitButton.getX() + exitButton.getWidth() + largePadding;
        int referenceY = exitButton.getY() + (exitButton.getHeight() - buttonSize) / 2;

        createAndPositionButtons(referenceY, folderX);
    }

    private void createAndPositionButtons(int referenceY, int folderX) {
        int buttonSize = 20;
        int padding = 4;

        TextIconButtonWidget folderButton = ButtonManager.createFolderButton(buttonSize, button ->
                tryOpenMinecraftDirectory(), true);

        TextIconButtonWidget settingsButton = ButtonManager.createSettingsButton(buttonSize, button ->
                tryOpenSettingsScreen(), true);

        int settingsX = folderX + buttonSize + padding;
        ButtonManager.positionButton(folderButton, folderX, referenceY);
        ButtonManager.positionButton(settingsButton, settingsX, referenceY);

        this.addDrawableChild(folderButton);
        this.addDrawableChild(settingsButton);
    }

    private void tryOpenMinecraftDirectory() {
        if (this.client != null) {
            try {
                Util.getOperatingSystem().open(this.client.runDirectory.toPath().toFile());
            } catch (Exception e) {
                EssentialsClient.LOGGER.error("Failed to open Minecraft directory", e);
            }
        } else {
            EssentialsClient.LOGGER.error("Cannot open Minecraft directory: client is null");
        }
    }

    private void tryOpenSettingsScreen() {
        if (this.client != null) {
            this.client.setScreen(new EssentialsConfigurationScreen(this, this.client.options));
        } else {
            EssentialsClient.LOGGER.error("Cannot open settings screen: client is null");
        }
    }
}