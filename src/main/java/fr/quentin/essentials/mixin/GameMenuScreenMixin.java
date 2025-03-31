package fr.quentin.essentials.mixin;

import fr.quentin.essentials.EssentialsClient;
import fr.quentin.essentials.gui.screen.EssentialsConfigurationScreen;
import fr.quentin.essentials.gui.screen.TitleScreenButtons;
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

import java.nio.file.Path;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {
    private static final String EXIT_TRANSLATION_KEY = "menu.returnToMenu";

    @Shadow
    private ButtonWidget exitButton;

    public GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void init(CallbackInfo info) {
        if (this.client == null) {
            EssentialsClient.LOGGER.error("Cannot initialize buttons: client is null");
            return;
        }

        ButtonWidget exitButtonFound = null;
        for (var child : this.children()) {
            if (child instanceof ButtonWidget button) {
                Text translatedText = Text.translatable(EXIT_TRANSLATION_KEY);
                if (button.getMessage().getString().equals(translatedText.getString())) {
                    exitButtonFound = button;
                    break;
                }
            }
        }

        if (exitButtonFound == null) {
            EssentialsClient.LOGGER.error("Cannot initialize buttons: exit button not found");
            return;
        }

        this.exitButton = exitButtonFound;

        int buttonSize = 20;
        int padding = 4;
        int basePadding = 14;
        int exitButtonX = exitButton.getX();
        int exitButtonY = exitButton.getY();
        int exitButtonWidth = exitButton.getWidth();
        int folderX = exitButtonX + exitButtonWidth + basePadding;
        int settingsX = folderX + buttonSize + padding;
        int y = exitButtonY + (exitButton.getHeight() - buttonSize) / 2;

        final Path minecraftDir = this.client.runDirectory.toPath();
        TextIconButtonWidget folderButton = this.addDrawableChild(TitleScreenButtons.createFolderButton(
                buttonSize,
                button -> {
                    try {
                        Util.getOperatingSystem().open(minecraftDir.toFile());
                    } catch (Exception e) {
                        EssentialsClient.LOGGER.error("Failed to open Minecraft directory", e);
                    }
                }, true
        ));
        folderButton.setPosition(folderX, y);

        TextIconButtonWidget settingsButton = this.addDrawableChild(TitleScreenButtons.createSettingsButton(
                buttonSize,
                button -> {
                    if (this.client != null) {
                        this.client.setScreen(new EssentialsConfigurationScreen(this, this.client.options));
                    } else {
                        EssentialsClient.LOGGER.error("Cannot open settings screen: client is null");
                    }
                }, true
        ));
        settingsButton.setPosition(settingsX, y);
    }
}