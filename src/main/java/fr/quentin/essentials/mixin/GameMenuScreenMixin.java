package fr.quentin.essentials.mixin;

import fr.quentin.essentials.EssentialsClient;
import fr.quentin.essentials.gui.screen.ButtonManager;
import fr.quentin.essentials.gui.screen.EssentialsConfigurationScreen;
import fr.quentin.essentials.utils.Constants;
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
            EssentialsClient.LOGGER.error(Constants.ERROR_CLIENT_NULL);
            return;
        }

        int folderX = exitButton.getX() + exitButton.getWidth() + Constants.LARGE_PADDING;
        int settingsX = folderX + Constants.BUTTON_SIZE + Constants.SMALL_PADDING;
        int referenceY = exitButton.getY() + (exitButton.getHeight() - Constants.BUTTON_SIZE) / 2;

        createAndPositionButtons(referenceY, folderX, settingsX);
    }

    private void createAndPositionButtons(int referenceY, int folderX, int settingsX) {
        TextIconButtonWidget folderButton = ButtonManager.createFolderButton(
                Constants.BUTTON_SIZE,
                button -> {
                    try {
                        if (this.client != null) {
                            Util.getOperatingSystem().open(this.client.runDirectory.toPath().toFile());
                        }
                    } catch (Exception e) {
                        EssentialsClient.LOGGER.error(Constants.ERROR_OPEN_DIRECTORY, e);
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

        ButtonManager.positionButton(folderButton, folderX, referenceY);
        ButtonManager.positionButton(settingsButton, settingsX, referenceY);

        this.addDrawableChild(folderButton);
        this.addDrawableChild(settingsButton);
    }
}