package fr.quentin.essentials.mixin;

import fr.quentin.essentials.EssentialsClient;
import fr.quentin.essentials.gui.screen.ButtonManager;
import fr.quentin.essentials.gui.screen.EssentialsSettingsScreen;
import fr.quentin.essentials.gui.widget.ModTextIconButtonWidget;
import fr.quentin.essentials.utils.Constants;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    private static final int TOP_PADDING = Constants.MEDIUM_PADDING;
    private static final int RIGHT_PADDING = Constants.MEDIUM_PADDING;

    public TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void init(CallbackInfo info) {
        createAndPositionButtonsTopRight();
    }

    private void createAndPositionButtonsTopRight() {
        ModTextIconButtonWidget folderButton = ButtonManager.createFolderButton(
                Constants.BUTTON_SIZE,
                button -> {
                    if (this.client != null) {
                        try {
                            Util.getOperatingSystem().open(this.client.runDirectory.toPath().toFile());
                        } catch (Exception e) {
                            EssentialsClient.LOGGER.error(Constants.ERROR_OPEN_DIRECTORY, e);
                        }
                    } else {
                        logClientNull();
                    }
                }, true
        );

        ModTextIconButtonWidget settingsButton = ButtonManager.createSettingsButton(
                Constants.BUTTON_SIZE,
                button -> {
                    if (this.client != null) {
                        this.client.setScreen(new EssentialsSettingsScreen(this, this.client.options));
                    } else {
                        logClientNull();
                    }
                }, true
        );

        ModTextIconButtonWidget[] buttons = {settingsButton, folderButton};
        ButtonManager.positionButtonsTopRight(buttons, this.width, RIGHT_PADDING, Constants.SMALL_PADDING);

        for (ModTextIconButtonWidget button : buttons) {
            button.setY(TOP_PADDING);
        }

        this.addDrawableChild(folderButton);
        this.addDrawableChild(settingsButton);
    }

    private void logClientNull() {
        EssentialsClient.LOGGER.error(Constants.ERROR_CLIENT_NULL);
    }
}