package fr.quentin.essentials;

import fr.quentin.essentials.command.ModCommand;
import fr.quentin.essentials.config.ModConfig;
import fr.quentin.essentials.option.ModKeyBinding;
import fr.quentin.essentials.screen.CoordinatesOverlay;
import fr.quentin.essentials.screen.EssentialsOptionsScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
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

        HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> {
            layeredDrawer.attachLayerBefore(
                    IdentifiedLayer.CHAT,
                    Identifier.of(Essentials.MOD_ID, "coordinates_overlay"),
                    (context, tickCounter) -> CoordinatesOverlay.render(context)
            );
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
                double currentGammaValue = ModConfig.getGammaValue();

                boolean newState;
                newState = !(currentGammaValue > ModCommand.GammaSettings.GAMMA_OFF);

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
            if (ModKeyBinding.configurationKey.wasPressed()) {
                if (client != null && client.currentScreen == null) {
                    client.setScreen(new EssentialsOptionsScreen(null, client.options));
                }
            }
        });
    }

    private void applyGammaFromConfig() {
        try {
            double gammaValue = ModConfig.getGammaValue();
            boolean gammaEnabled = ModConfig.isGammaEnabled();
            LOGGER.info("Applying gamma from config: Enabled={}, Value={}", gammaEnabled, gammaValue);

            if (gammaEnabled) {
                ModCommand.setGamma(gammaValue);
            } else {
                ModCommand.setGamma(ModCommand.GammaSettings.GAMMA_OFF);
            }
        } catch (Exception exception) {
            LOGGER.error("Failed to apply gamma from config", exception);
        }
    }
}