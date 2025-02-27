package fr.quentin.essentials;

import fr.quentin.essentials.command.ModCommand;
import fr.quentin.essentials.config.ModConfig;
import fr.quentin.essentials.option.ModKeyBinding;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EssentialsClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("Essentials");

    @Override
    public void onInitializeClient() {
        ModConfig.load();
        registerClientCommands();
        ModKeyBinding.register();
        registerKeyInputs();

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            applyGammaFromConfig();
        });
    }

    private void registerClientCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            ModCommand.registerAll(dispatcher);
        });
    }

    private void registerKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (ModKeyBinding.gammaKey.wasPressed()) {
                boolean newState = !ModConfig.isGammaEnabled();
                ModConfig.setGammaEnabled(newState);

                if (newState) {
                    ModCommand.setGamma(ModCommand.GammaSettings.GAMMA_ON);

                    if (MinecraftClient.getInstance().player != null) {
                        MinecraftClient.getInstance().player.sendMessage(
                                Text.translatable("gamma.toggled_on"), true);
                    }
                } else {
                    ModCommand.setGamma(ModCommand.GammaSettings.GAMMA_OFF);

                    if (MinecraftClient.getInstance().player != null) {
                        MinecraftClient.getInstance().player.sendMessage(
                                Text.translatable("gamma.toggled_off"), true);
                    }
                }
            }
        });
    }

    private void applyGammaFromConfig() {
        try {
            double gammaValue = ModConfig.getGammaValue();
            boolean gammaEnabled = ModConfig.isGammaEnabled();
            LOGGER.info("Applying gamma from config: Enabled={}, Value={}", gammaEnabled, gammaValue);
            ModCommand.setGamma(gammaValue);
            ModConfig.setGammaEnabled(gammaValue > ModCommand.GammaSettings.GAMMA_OFF);
        } catch (Exception exception) {
            LOGGER.error("Failed to apply gamma from config", exception);
        }
    }
}