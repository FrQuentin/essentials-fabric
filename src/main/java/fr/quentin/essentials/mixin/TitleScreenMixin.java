package fr.quentin.essentials.mixin;

import fr.quentin.essentials.EssentialsClient;
import fr.quentin.essentials.gui.screen.EssentialsOptionsScreen;
import fr.quentin.essentials.gui.screen.TitleScreenButtons;
import fr.quentin.essentials.gui.widget.ModTextIconButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    public TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void init(CallbackInfo info) {
        int buttonSize = 20;
        int padding = 4;
        int settingsX = this.width - buttonSize - padding;
        int y = padding;

        ModTextIconButtonWidget settingsButton = this.addDrawableChild(TitleScreenButtons.createSettingsButton(
                20,
                button -> {
                    if (this.client != null) {
                        this.client.setScreen(new EssentialsOptionsScreen(this, this.client.options));
                    } else {
                        EssentialsClient.LOGGER.error("Cannot open settings screen: client is null");
                    }
                }, true
        ));
        settingsButton.setPosition(settingsX, y);

        if (this.client == null) {
            EssentialsClient.LOGGER.error("Cannot initialize folder button: client is null");
            return;
        }
        int folderX = settingsX - buttonSize - padding;
        final Path minecraftDir = this.client.runDirectory.toPath();

        ModTextIconButtonWidget folderButton = this.addDrawableChild(TitleScreenButtons.createFolderButton(
                20,
                button -> {
                    if (this.client != null) {
                        try {
                            Util.getOperatingSystem().open(minecraftDir.toFile());
                        } catch (Exception e) {
                            EssentialsClient.LOGGER.error("Failed to open Minecraft directory", e);
                        }
                    } else {
                        EssentialsClient.LOGGER.error("Cannot open Minecraft directory: client is null");
                    }
                }, true
        ));
        folderButton.setPosition(folderX, y);
    }
}