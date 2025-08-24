package fr.quentin.essentials.mixin;

import fr.quentin.essentials.EssentialsClient;
import fr.quentin.essentials.gui.screen.ButtonManager;
import fr.quentin.essentials.gui.screen.EssentialsSettingsScreen;
import fr.quentin.essentials.gui.widget.ModTextIconButtonWidget;
import fr.quentin.essentials.utils.Constants;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin extends Screen {
    private static final int TOP_PADDING = Constants.MEDIUM_PADDING;
    private static final int RIGHT_PADDING = Constants.MEDIUM_PADDING;

    private ModTextIconButtonWidget settingsButton;

    public OptionsScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void init(CallbackInfo info) {
        if (this.client == null) {
            logClientNull();
            return;
        }

        createAndPositionSettingsButtonTopRight();
    }

    @Inject(method = "refreshWidgetPositions", at = @At("RETURN"))
    private void refreshWidgetPositions(CallbackInfo info) {
        if (settingsButton != null) {
            updateSettingsButtonPositionTopRight();
        }
    }

    private void createAndPositionSettingsButtonTopRight() {
        settingsButton = this.addDrawableChild(ButtonManager.createSettingsButton(
                Constants.BUTTON_SIZE,
                button -> {
                    if (this.client != null) {
                        this.client.setScreen(new EssentialsSettingsScreen(this, this.client.options));
                    } else {
                        logClientNull();
                    }
                },
                true
        ));
        updateSettingsButtonPositionTopRight();
    }

    private void updateSettingsButtonPositionTopRight() {
        if (settingsButton != null) {
            int settingsX = this.width - Constants.BUTTON_SIZE - RIGHT_PADDING;
            ButtonManager.positionButton(settingsButton, settingsX, TOP_PADDING);
        }
    }

    private void logClientNull() {
        EssentialsClient.LOGGER.error(Constants.ERROR_CLIENT_NULL);
    }
}