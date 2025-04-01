package fr.quentin.essentials.mixin;

import fr.quentin.essentials.EssentialsClient;
import fr.quentin.essentials.gui.screen.EssentialsConfigurationScreen;
import fr.quentin.essentials.gui.screen.TitleScreenButtons;
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
            int buttonSize = 20;
            int padding = 4;

            settingsButton = this.addDrawableChild(TitleScreenButtons.createSettingsButton(
                    buttonSize,
                    button -> {
                        this.client.setScreen(new EssentialsConfigurationScreen(this, this.client.options));
                    }, true
            ));

            updateSettingsButtonPosition();
        } else {
            int buttonSize = 20;
            int padding = 4;
            int settingsX = this.width - buttonSize - padding;
            int y = padding;
            settingsButton = this.addDrawableChild(TitleScreenButtons.createSettingsButton(
                    buttonSize,
                    button -> {
                        this.client.setScreen(new EssentialsConfigurationScreen(this, this.client.options));
                    }, true
            ));
            settingsButton.setPosition(settingsX, y);
            EssentialsClient.LOGGER.warn("Credits & Attribution button not found, using fallback positioning");
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

            settingsButton.setPosition(settingsX, settingsY);
        }
    }
}