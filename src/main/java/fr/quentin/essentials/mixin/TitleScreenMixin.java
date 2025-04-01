package fr.quentin.essentials.mixin;

import fr.quentin.essentials.EssentialsClient;
import fr.quentin.essentials.gui.screen.ButtonManager;
import fr.quentin.essentials.gui.screen.EssentialsConfigurationScreen;
import fr.quentin.essentials.utils.Constants;
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
    public TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void init(CallbackInfo info) {
        Optional<ButtonWidget> accessibilityButton = findAccessibilityButton();
        if (accessibilityButton.isPresent()) {
            ButtonWidget accessibility = accessibilityButton.get();
            createAndPositionButtons(accessibility.getY(), accessibility.getX());
        } else {
            EssentialsClient.LOGGER.warn("Accessibility button not found, using fallback positioning");
            int l = this.height / 4 + 48 + 72 + 12;
            int folderX = this.width / 2 + 104 + 34; // basePadding = buttonSize + 14
            createAndPositionButtons(l, folderX);
        }
    }

    private Optional<ButtonWidget> findAccessibilityButton() {
        return this.children()
                .stream()
                .filter(widget -> widget instanceof ButtonWidget)
                .map(widget -> (ButtonWidget) widget)
                .filter(button -> {
                    Text translatedText = Text.translatable(Constants.ACCESSIBILITY_TRANSLATION_KEY);
                    return button.getMessage().getString().equals(translatedText.getString());
                })
                .findFirst();
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