package fr.quentin.essentials.input;

import fr.quentin.essentials.command.ModCommand;
import fr.quentin.essentials.config.ModConfig;
import fr.quentin.essentials.gui.screen.EssentialsOptionsScreen;
import fr.quentin.essentials.gui.screen.ShulkerPreviewScreen;
import fr.quentin.essentials.option.ModKeyBinding;
import fr.quentin.essentials.utils.Constants;
import fr.quentin.essentials.utils.ShulkerColorManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class KeyInputHandler {
    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            handleGammaKey(client);
            handleConfigurationKey(client);
            handleCoordinatesKey(client);
            handleZoomKey();
            handleShulkerKey(client);
        });
    }

    private static void handleGammaKey(MinecraftClient client) {
        if (ModKeyBinding.gammaKey.wasPressed()) {
            double currentGammaValue = ModConfig.getGammaValue();

            boolean newState = !(currentGammaValue > Constants.GAMMA_OFF);

            ModConfig.setGammaEnabled(newState);

            if (newState) {
                ModCommand.setGamma(Constants.GAMMA_ON);
                sendPlayerMessage("gamma.toggled_on");
            } else {
                ModCommand.setGamma(Constants.GAMMA_OFF);
                sendPlayerMessage("gamma.toggled_off");
            }
        }
    }

    private static void handleConfigurationKey(MinecraftClient client) {
        if (ModKeyBinding.configurationKey.wasPressed()) {
            if (client != null && client.currentScreen == null) {
                client.setScreen(new EssentialsOptionsScreen(null, client.options));
            }
        }
    }

    private static void handleCoordinatesKey(MinecraftClient client) {
        if (ModKeyBinding.coordinatesKey.wasPressed()) {
            boolean newState = !ModConfig.isCoordinatesEnabled();
            ModConfig.setCoordinatesEnabled(newState);

            if (newState) {
                sendPlayerMessage("coordinates.toggled_on");
            } else {
                sendPlayerMessage("coordinates.toggled_off");
            }
        }
    }

    private static void handleZoomKey() {
        if (ModKeyBinding.zoomKey.isPressed()) {
            ModConfig.setZoomEnabled(true);
        } else if (ModConfig.isZoomEnabled()) {
            ModConfig.setZoomEnabled(false);
        }
    }

    private static void handleShulkerKey(MinecraftClient client) {
        if (client != null && client.player != null) {
            ItemStack stack = client.player.getMainHandStack();
            while (ModKeyBinding.shulkerKey.wasPressed()) {
                if (ShulkerColorManager.getColorForShulker(stack.getItem()) != null) {
                    client.setScreen(new ShulkerPreviewScreen(stack, null));
                }
            }
        }
    }

    private static void sendPlayerMessage(String translationKey) {
        if (Constants.client.player != null) {
            Constants.client.player.sendMessage(Text.translatable(translationKey), true);
        }
    }
}