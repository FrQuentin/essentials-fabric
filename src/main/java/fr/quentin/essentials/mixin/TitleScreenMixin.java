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
        Optional<ButtonWidget> accessibilityButton = this.children()
                .stream()
                .filter(widget -> widget instanceof ButtonWidget)
                .map(widget -> (ButtonWidget) widget)
                .filter(button -> {
                    Text translatedText = Text.translatable(Constants.ACCESSIBILITY_TRANSLATION_KEY);
                    return button.getMessage().getString().equals(translatedText.getString());
                })
                .findFirst();

        if (accessibilityButton.isPresent()) {
            ButtonWidget accessibility = accessibilityButton.get();
            int referenceY = accessibility.getY() + (accessibility.getHeight() - Constants.BUTTON_SIZE) / 2;
            int folderX = accessibility.getX() + accessibility.getWidth() + Constants.BASE_PADDING;
            createAndPositionButtons(referenceY, folderX);
        } else {
            EssentialsClient.LOGGER.warn(Constants.WARN_ACCESSIBILITY_BUTTON_MISSING);
            int referenceY = this.height / 4 + Constants.FALLBACK_BUTTON_Y_OFFSET;
            int folderX = this.width / 2 + Constants.FALLBACK_FOLDER_X_OFFSET + Constants.BASE_PADDING;
            createAndPositionButtons(referenceY, folderX);
        }
    }

    private void createAndPositionButtons(int referenceY, int folderX) {
        TextIconButtonWidget folderButton = ButtonManager.createFolderButton(
                Constants.BUTTON_SIZE,
                button -> {
                    if (this.client != null) {
                        try {
                            Util.getOperatingSystem().open(this.client.runDirectory.toPath().toFile());
                        } catch (Exception e) {
                            EssentialsClient.LOGGER.error(Constants.ERROR_OPEN_DIRECTORY, e);
                        }
                    } else {
                        EssentialsClient.LOGGER.error(Constants.ERROR_CLIENT_NULL);
                    }
                }, true
        );

        TextIconButtonWidget settingsButton = ButtonManager.createSettingsButton(
                Constants.BUTTON_SIZE,
                button -> {
                    if (this.client != null) {
                        this.client.setScreen(new EssentialsConfigurationScreen(this, this.client.options));
                    } else {
                        EssentialsClient.LOGGER.error(Constants.ERROR_CLIENT_NULL);
                    }
                }, true
        );

        int settingsX = folderX + Constants.BUTTON_SIZE + Constants.SMALL_PADDING;
        ButtonManager.positionButton(folderButton, folderX, referenceY);
        ButtonManager.positionButton(settingsButton, settingsX, referenceY);

        this.addDrawableChild(folderButton);
        this.addDrawableChild(settingsButton);
    }
}