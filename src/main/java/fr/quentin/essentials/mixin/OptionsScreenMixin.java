package fr.quentin.essentials.mixin;

import fr.quentin.essentials.EssentialsClient;
import fr.quentin.essentials.gui.screen.ButtonManager;
import fr.quentin.essentials.gui.screen.EssentialsConfigurationScreen;
import fr.quentin.essentials.utils.Constants;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin extends Screen {
    private TextIconButtonWidget settingsButton;
    private ButtonWidget creditsButton;

    public OptionsScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void init(CallbackInfo info) {
        if (this.client == null) {
            EssentialsClient.LOGGER.error("Cannot initialize settings button: client is null");
            return;
        }

        Optional<ButtonWidget> lastButton = findCreditsButton();
        if (lastButton.isPresent()) {
            creditsButton = lastButton.get();
            createAndPositionSettingsButton();
        } else {
            createFallbackSettingsButton();
        }
    }

    private Optional<ButtonWidget> findCreditsButton() {
        return this.children()
                .stream()
                .filter(widget -> widget instanceof ButtonWidget)
                .map(widget -> (ButtonWidget) widget)
                .filter(button -> {
                    String key = button.getMessage().getString();
                    Text translatedText = Text.translatable(Constants.CREDITS_TRANSLATION_KEY);
                    return key.equals(translatedText.getString());
                })
                .findFirst();
    }

    private void createAndPositionSettingsButton() {
        int buttonSize = 20;
        int padding = 4;

        settingsButton = ButtonManager.createSettingsButton(buttonSize, button -> tryOpenSettingsScreen(), true);

        updateSettingsButtonPosition();
        this.addDrawableChild(settingsButton);
    }

    private void createFallbackSettingsButton() {
        int buttonSize = 20;
        int padding = 4;
        int settingsX = this.width - buttonSize - padding;
        int y = padding;

        settingsButton = ButtonManager.createSettingsButton(buttonSize, button -> tryOpenSettingsScreen(), true);

        ButtonManager.positionButton(settingsButton, settingsX, y);
        this.addDrawableChild(settingsButton);

        EssentialsClient.LOGGER.warn("Credits & Attribution button not found, using fallback positioning");
    }

    @Inject(method = "refreshWidgetPositions", at = @At("RETURN"))
    private void refreshWidgetPositions(CallbackInfo info) {
        if (settingsButton != null && creditsButton != null) {
            updateSettingsButtonPosition();
        }
    }

    private void updateSettingsButtonPosition() {
        if (creditsButton != null && settingsButton != null) {
            int buttonSize = settingsButton.getWidth();
            int padding = 4;
            int settingsX = creditsButton.getX() + creditsButton.getWidth() + padding;
            int settingsY = creditsButton.getY() + (creditsButton.getHeight() - buttonSize) / 2;
            ButtonManager.positionButton(settingsButton, settingsX, settingsY);
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