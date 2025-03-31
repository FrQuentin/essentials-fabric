package fr.quentin.essentials;

import fr.quentin.essentials.command.ModCommand;
import fr.quentin.essentials.config.ModConfig;
import fr.quentin.essentials.input.KeyInputHandler;
import fr.quentin.essentials.notification.MarketRestockNotifier;
import fr.quentin.essentials.option.ModKeyBinding;
import fr.quentin.essentials.gui.screen.CoordinatesOverlay;
import fr.quentin.essentials.utils.Constants;
import fr.quentin.essentials.utils.DarknessEffectHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EssentialsClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(Essentials.MOD_NAME);

    @Override
    public void onInitializeClient() {
        ModConfig.load();
        registerClientCommands();
        ModKeyBinding.register();
        KeyInputHandler.register();
        DarknessEffectHandler.register();
        MarketRestockNotifier.register();

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

    private void applyGammaFromConfig() {
        try {
            double gammaValue = ModConfig.getGammaValue();
            boolean gammaEnabled = ModConfig.isGammaEnabled();

            if (gammaValue < Constants.GAMMA_MIN || gammaValue > Constants.GAMMA_MAX) {
                LOGGER.warn("Gamma value out of range: {}. Defaulting to 1.0.", gammaValue);
                gammaValue = 1.0;
            }

            LOGGER.info("Applying gamma from config: Enabled={}, Value={}", gammaEnabled, gammaValue);

            if (gammaEnabled) {
                ModCommand.setGamma(gammaValue);
            } else {
                ModCommand.setGamma(Constants.GAMMA_OFF);
            }
        } catch (NumberFormatException e) {
            LOGGER.error("Invalid gamma value in config. Using default gamma.", e);
            ModCommand.setGamma(Constants.GAMMA_OFF);
        } catch (Exception e) {
            LOGGER.error("Failed to apply gamma from config", e);
        }
    }
}