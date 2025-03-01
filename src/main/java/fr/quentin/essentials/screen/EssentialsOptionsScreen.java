package fr.quentin.essentials.screen;

import fr.quentin.essentials.EssentialsClient;
import fr.quentin.essentials.command.ModCommand;
import fr.quentin.essentials.config.ModConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class EssentialsOptionsScreen extends GameOptionsScreen {
    private static final Text TITLE = Text.translatable("essentials.screen.configuration");
    private final ModConfig.ConfigData config;

    public EssentialsOptionsScreen(Screen parent, GameOptions gameOptions) {
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
                        "gamma.option.toggle",
                        value -> Tooltip.of(Text.translatable("gamma.tooltip")),
                        (text, value) -> value ? Text.translatable("gamma.button.on") : Text.translatable("gamma.button.off"),
                        SimpleOption.BOOLEAN,
                        config.gammaEnabled,
                        value -> {
                            config.gammaEnabled = value;
                            ModConfig.save();
                            if (value) {
                                ModCommand.setGamma(ModCommand.GammaSettings.GAMMA_ON);
                            } else {
                                ModCommand.setGamma(ModCommand.GammaSettings.GAMMA_OFF);
                            }
                        }
                ),
                new SimpleOption<>(
                        "coordinates.option.toggle",
                        value -> Tooltip.of(Text.translatable("coordinates.tooltip")),
                        (text, value) -> value ? Text.translatable("coordinates.button.on") : Text.translatable("coordinates.button.off"),
                        SimpleOption.BOOLEAN,
                        config.coordinatesEnabled,
                        value -> {
                            config.coordinatesEnabled = value;
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