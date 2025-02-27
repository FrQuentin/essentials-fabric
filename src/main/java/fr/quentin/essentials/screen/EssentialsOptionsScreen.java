package fr.quentin.essentials.screen;

import fr.quentin.essentials.EssentialsClient;
import fr.quentin.essentials.command.ModCommand;
import fr.quentin.essentials.config.ModConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
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
        final int SCALE_FACTOR = 100;

        return new SimpleOption[]{
                new SimpleOption<>(
                        "gamma.option.enabled",
                        SimpleOption.emptyTooltip(),
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
                        "gamma.option.value",
                        SimpleOption.emptyTooltip(),
                        (text, value) -> Text.literal(String.format("%.2f", value / (double) SCALE_FACTOR)),
                        new SimpleOption.ValidatingIntSliderCallbacks(
                                (int) (ModCommand.GammaSettings.GAMMA_MIN * SCALE_FACTOR),
                                (int) (ModCommand.GammaSettings.GAMMA_MAX * SCALE_FACTOR)
                        ),
                        (int) (config.gammaValue * SCALE_FACTOR),
                        value -> {
                            config.gammaValue = value / (double) SCALE_FACTOR;
                            ModConfig.save();
                            ModCommand.setGamma(config.gammaValue);
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