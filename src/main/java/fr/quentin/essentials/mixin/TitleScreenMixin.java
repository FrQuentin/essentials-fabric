package fr.quentin.essentials.mixin;

import fr.quentin.essentials.EssentialsClient;
import fr.quentin.essentials.gui.screen.EssentialsConfigurationScreen;
import fr.quentin.essentials.gui.screen.TitleScreenButtons;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    private static final String ACCESSIBILITY_TRANSLATION_KEY = "narrator.button.accessibility";

    public TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void init(CallbackInfo info) {
        int buttonSize = 20;
        int padding = 4;
        int basePadding = buttonSize + 14;

        Optional<ButtonWidget> accessibilityButton = this.children()
                .stream()
                .filter(widget -> widget instanceof ButtonWidget)
                .map(widget -> (ButtonWidget) widget)
                .filter(button -> {
                    Text translatedText = Text.translatable(ACCESSIBILITY_TRANSLATION_KEY);
                    return button.getMessage().getString().equals(translatedText.getString());
                })
                .findFirst();

        if (accessibilityButton.isPresent()) {
            ButtonWidget accessibility = accessibilityButton.get();

            int l = accessibility.getY() + (accessibility.getHeight() - buttonSize) / 2;
            int folderX = accessibility.getX() + accessibility.getWidth() + basePadding;
            int settingsX = folderX + buttonSize + padding;

            TextIconButtonWidget folderButton = this.addDrawableChild(TitleScreenButtons.createFolderButton(
                    buttonSize,
                    button -> {
                        if (this.client != null) {
                            try {
                                Util.getOperatingSystem().open(this.client.runDirectory.toPath().toFile());
                            } catch (Exception e) {
                                EssentialsClient.LOGGER.error("Failed to open Minecraft directory", e);
                            }
                        } else {
                            EssentialsClient.LOGGER.error("Cannot open Minecraft directory: client is null");
                        }
                    }, true
            ));
            folderButton.setPosition(folderX, l);

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
            settingsButton.setPosition(settingsX, l);
        } else {
            EssentialsClient.LOGGER.warn("Accessibility button not found, using fallback positioning");

            int l = this.height / 4 + 48 + 72 + 12;
            int folderX = this.width / 2 + 104 + basePadding;
            int settingsX = folderX + buttonSize + padding;

            TextIconButtonWidget folderButton = this.addDrawableChild(TitleScreenButtons.createFolderButton(
                    buttonSize,
                    button -> {
                        if (this.client != null) {
                            try {
                                Util.getOperatingSystem().open(this.client.runDirectory.toPath().toFile());
                            } catch (Exception e) {
                                EssentialsClient.LOGGER.error("Failed to open Minecraft directory", e);
                            }
                        } else {
                            EssentialsClient.LOGGER.error("Cannot open Minecraft directory: client is null");
                        }
                    }, true
            ));
            folderButton.setPosition(folderX, l);

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
            settingsButton.setPosition(settingsX, l);
        }
    }
}