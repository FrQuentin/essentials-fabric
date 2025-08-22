package fr.quentin.essentials.input;

import fr.quentin.essentials.EssentialsClient;
import fr.quentin.essentials.command.ModCommand;
import fr.quentin.essentials.config.ModConfig;
import fr.quentin.essentials.gui.screen.EssentialsSettingsScreen;
import fr.quentin.essentials.gui.screen.ShulkerPreviewScreen;
import fr.quentin.essentials.option.ModKeyBinding;
import fr.quentin.essentials.utils.Constants;
import fr.quentin.essentials.utils.ShulkerColorManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class KeyInputHandler {
    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            handleGammaKey();
            handleConfigurationKey();
            handleCoordinatesKey();
            handleZoomKey();
            handleShulkerKey();
        });
    }

    private static void handleGammaKey() {
        if (ModKeyBinding.gammaKey.wasPressed()) {
            double currentGammaValue = ModConfig.getGammaValue();
            boolean newState = !(currentGammaValue > Constants.GAMMA_OFF);

            ModConfig.setGammaEnabled(newState);

            if (newState) {
                ModCommand.setGamma(Constants.GAMMA_ON);
                sendPlayerMessage("command.essentials.gamma.toggled_on");
            } else {
                ModCommand.setGamma(Constants.GAMMA_OFF);
                sendPlayerMessage("command.essentials.gamma.toggled_off");
            }
        }
    }

    private static void handleConfigurationKey() {
        if (ModKeyBinding.settingsKey.wasPressed()) {
            if (Constants.client != null && Constants.client.currentScreen == null) {
                Constants.client.setScreen(new EssentialsSettingsScreen(null, Constants.client.options));
            }
        }
    }

    private static void handleCoordinatesKey() {
        if (ModKeyBinding.coordinatesKey.wasPressed()) {
            boolean newState = ModConfig.isCoordinatesEnabled();
            ModConfig.setCoordinatesEnabled(newState);

            if (newState) {
                sendPlayerMessage("command.essentials.coordinates.toggled_off");
            } else {
                sendPlayerMessage("command.essentials.coordinates.toggled_on");
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

    private static void handleShulkerKey() {
        try {
            if (Constants.client == null || Constants.client.player == null) {
                return;
            }
            if (!ModKeyBinding.shulkerKey.wasPressed()) {
                return;
            }

            ItemStack stack = getRelevantItemStack();

            if (stack.isEmpty()) {
                sendPlayerMessage("command.essentials.shulker.empty_hand");
                return;
            }
            if (ShulkerColorManager.getColorForShulker(stack.getItem()) == null) {
                sendPlayerMessage("command.essentials.shulker.not_a_shulker");
                return;
            }

            try {
                Constants.client.setScreen(new ShulkerPreviewScreen(stack, Constants.client.currentScreen));
            } catch (Exception e) {
                sendPlayerMessage("command.essentials.shulker.error_opening");
            }

        } catch (Exception e) {
            EssentialsClient.LOGGER.error("Error in handleShulkerKey", e);
        }
    }

    private static ItemStack getRelevantItemStack() {
        if (Constants.client == null || Constants.client.player == null) {
            return ItemStack.EMPTY;
        }
        if (Constants.client.currentScreen != null) {
            ItemStack cursorStack = Constants.client.player.currentScreenHandler.getCursorStack();
            if (!cursorStack.isEmpty()) {
                return cursorStack;
            }
        }

        return Constants.client.player.getMainHandStack();
    }

    private static void sendPlayerMessage(String translationKey) {
        if (Constants.client.player != null) {
            Constants.client.player.sendMessage(Text.translatable(translationKey), true);
        }
    }
}