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
            EssentialsClient.LOGGER.error(Constants.ERROR_CLIENT_NULL);
            return;
        }

        Optional<ButtonWidget> lastButton = this.children()
                .stream()
                .filter(widget -> widget instanceof ButtonWidget)
                .map(widget -> (ButtonWidget) widget)
                .filter(button -> {
                    String key = button.getMessage().getString();
                    Text translatedText = Text.translatable(Constants.CREDITS_TRANSLATION_KEY);
                    return key.equals(translatedText.getString());
                })
                .findFirst();

        if (lastButton.isPresent()) {
            creditsButton = lastButton.get();
            settingsButton = this.addDrawableChild(ButtonManager.createSettingsButton(
                    Constants.BUTTON_SIZE,
                    button -> this.client.setScreen(new EssentialsConfigurationScreen(this, this.client.options)),
                    true
            ));
            updateSettingsButtonPosition();
        } else {
            EssentialsClient.LOGGER.warn(Constants.WARN_CREDITS_BUTTON_MISSING);
            int settingsX = this.width - Constants.BUTTON_SIZE - Constants.SMALL_PADDING;
            int y = Constants.SMALL_PADDING;

            settingsButton = this.addDrawableChild(ButtonManager.createSettingsButton(
                    Constants.BUTTON_SIZE,
                    button -> this.client.setScreen(new EssentialsConfigurationScreen(this, this.client.options)),
                    true
            ));
            ButtonManager.positionButton(settingsButton, settingsX, y);
        }
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
}