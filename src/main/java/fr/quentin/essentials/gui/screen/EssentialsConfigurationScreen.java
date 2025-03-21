package fr.quentin.essentials.gui.screen;

import fr.quentin.essentials.EssentialsClient;
import fr.quentin.essentials.command.ModCommand;
import fr.quentin.essentials.config.ModConfig;
import fr.quentin.essentials.utils.Constants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class EssentialsConfigurationScreen extends GameOptionsScreen {
    private static final Text TITLE = Text.translatable("screen.essentials.configuration.title");
    private final ModConfig.ConfigData config;

    public EssentialsConfigurationScreen(Screen parent, GameOptions gameOptions) {
        super(parent, gameOptions, TITLE);
        this.config = ModConfig.getConfigData();
    }

    @Override
    protected void addOptions() {
        if (this.body != null) {
            SimpleOption<?>[] options = getOptions();
            this.body.addAll(options);
        } else {
            EssentialsClient.LOGGER.error("Failed to add options: option list is null");
        }
    }

    private SimpleOption<?>[] getOptions() {
        return new SimpleOption[]{
                new SimpleOption<>(
                        "screen.essentials.option.gamma",
                        value -> Tooltip.of(Text.translatable("tooltip.essentials.gamma.description")),
                        (text, value) -> value ? Text.translatable("screen.essentials.button.on") : Text.translatable("screen.essentials.button.off"),
                        SimpleOption.BOOLEAN,
                        config.gammaEnabled,
                        value -> {
                            config.gammaEnabled = value;
                            ModConfig.save();
                            if (value) {
                                ModCommand.setGamma(Constants.GAMMA_ON);
                            } else {
                                ModCommand.setGamma(Constants.GAMMA_OFF);
                            }
                        }
                ),
                new SimpleOption<>(
                        "screen.essentials.option.coordinates",
                        value -> Tooltip.of(Text.translatable("tooltip.essentials.coordinates.description")),
                        (text, value) -> value ? Text.translatable("screen.essentials.button.on") : Text.translatable("screen.essentials.button.off"),
                        SimpleOption.BOOLEAN,
                        config.coordinatesEnabled,
                        value -> {
                            config.coordinatesEnabled = value;
                            ModConfig.save();
                        }
                ),
                new SimpleOption<>(
                        "screen.essentials.option.zoom_percentage",
                        value -> Tooltip.of(Text.translatable("tooltip.essentials.zoom_percentage.description")),
                        (text, value) -> value ? Text.translatable("screen.essentials.button.on") : Text.translatable("screen.essentials.button.off"),
                        SimpleOption.BOOLEAN,
                        config.zoomOverlayEnabled,
                        value -> {
                            config.zoomOverlayEnabled = value;
                            ModConfig.save();
                        }
                ),
                new SimpleOption<>(
                        "screen.essentials.option.darkness_effect",
                        value -> Tooltip.of(Text.translatable("tooltip.essentials.darkness_effect.description")),
                        (text, value) -> value ? Text.translatable("screen.essentials.button.off") : Text.translatable("screen.essentials.button.on"),
                        SimpleOption.BOOLEAN,
                        config.darknessEffectEnabled,
                        value -> {
                            config.darknessEffectEnabled = value;
                            ModConfig.save();
                        }
                )
        };
    }

    @Override
    public void removed() {
        ModConfig.save();
        super.removed();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}