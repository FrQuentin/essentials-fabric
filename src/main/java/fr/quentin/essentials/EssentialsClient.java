package fr.quentin.essentials;

import fr.quentin.essentials.command.ModCommand;
import fr.quentin.essentials.config.ModConfig;
import fr.quentin.essentials.input.KeyInputHandler;
import fr.quentin.essentials.input.InputPollCallback;
import fr.quentin.essentials.input.CharInputCallback;
import fr.quentin.essentials.input.KeyPressCallback;
import fr.quentin.essentials.option.ModKeyBinding;
import fr.quentin.essentials.gui.screen.CoordinatesOverlay;
import fr.quentin.essentials.utils.Constants;
import fr.quentin.essentials.utils.DarknessEffectHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.impl.client.rendering.hud.HudElementRegistryImpl;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class EssentialsClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(Essentials.MOD_NAME);
    private long eventCounter;
    private long lastKeyPressTime;

    @Override
    public void onInitializeClient() {
        ModConfig.load();
        registerClientCommands();
        ModKeyBinding.register();
        KeyInputHandler.register();
        DarknessEffectHandler.register();

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            applyGammaFromConfig();
        });

        HudElementRegistryImpl.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.of(Essentials.MOD_ID, "coordinates_overlay"),
                (context, tickCounter) -> CoordinatesOverlay.render(context)
        );

        InputPollCallback.EVENT.register(() -> this.eventCounter++);

        KeyPressCallback.EVENT.register((window, key, scancode, action, modifiers) -> {
            if (action != GLFW.GLFW_PRESS || Constants.client.currentScreen != null || key == GLFW.GLFW_KEY_ENTER)
                return ActionResult.PASS;

            if (Constants.client.options.chatKey.matchesKey(key, scancode) || Constants.client.options.commandKey.matchesKey(key, scancode)) {
                this.lastKeyPressTime = this.eventCounter;
            } else {
                this.lastKeyPressTime = -1L;
            }
            return ActionResult.PASS;
        });

        CharInputCallback.EVENT.register((window, codepoint, modifiers) -> {
            if (this.lastKeyPressTime == -1 || this.eventCounter - this.lastKeyPressTime > 5)
                return ActionResult.PASS;

            this.lastKeyPressTime = -1;
            return ActionResult.FAIL;
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