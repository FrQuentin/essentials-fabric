package fr.quentin.essentials.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import fr.quentin.essentials.EssentialsClient;
import fr.quentin.essentials.config.ModConfig;
import fr.quentin.essentials.utils.Constants;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ModCommand {
    public static void registerAll(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        registerGammaCommand(dispatcher);
        registerCoordinatesCommand(dispatcher);
    }

    public static void registerCoordinatesCommand(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                ClientCommandManager.literal("coordinates")
                        .then(ClientCommandManager.literal("toggle")
                                .executes(ModCommand::executeCoordinatesToggle))
        );
    }

    private static int executeCoordinatesToggle(CommandContext<FabricClientCommandSource> context) {
        boolean newState = ModConfig.isCoordinatesEnabled();
        ModConfig.setCoordinatesEnabled(newState);

        if (newState) {
            context.getSource().sendFeedback(Text.translatable("coordinates.toggled_on"));
        } else {
            context.getSource().sendFeedback(Text.translatable("coordinates.toggled_off"));
        }
        return 1;
    }

    private static void registerGammaCommand(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                ClientCommandManager.literal("gamma")
                        .executes(ModCommand::executeGammaStatus)
                        .then(ClientCommandManager.literal("toggle")
                                .executes(ModCommand::executeGammaToggle))
                        .then(ClientCommandManager.literal("increase")
                                .then(ClientCommandManager.argument("value", DoubleArgumentType.doubleArg(0.1, Constants.GAMMA_MAX))
                                        .executes(ModCommand::executeGammaIncrease)))
                        .then(ClientCommandManager.literal("decrease")
                                .then(ClientCommandManager.argument("value", DoubleArgumentType.doubleArg(0.1, Constants.GAMMA_MAX))
                                        .executes(ModCommand::executeGammaDecrease)))
                        .then(ClientCommandManager.literal("reset")
                                .executes(ModCommand::executeGammaReset))
        );
    }

    private static int executeGammaStatus(CommandContext<FabricClientCommandSource> context) {
        double value = ModConfig.getGammaValue();
        context.getSource().sendFeedback(Text.translatable("gamma.current", value));
        return 1;
    }

    private static int executeGammaToggle(CommandContext<FabricClientCommandSource> context) {
        boolean newState = !ModConfig.isGammaEnabled();
        ModConfig.setGammaEnabled(newState);

        if (newState) {
            setGamma(Constants.GAMMA_ON);
            context.getSource().sendFeedback(Text.translatable("gamma.toggled_on"));
        } else {
            setGamma(Constants.GAMMA_OFF);
            context.getSource().sendFeedback(Text.translatable("gamma.toggled_off"));
        }
        return 1;
    }

    private static int executeGammaIncrease(CommandContext<FabricClientCommandSource> context) {
        double increaseValue = DoubleArgumentType.getDouble(context, "value");
        double currentGamma = Constants.client.options.getGamma().getValue();
        double newGamma = Math.min(currentGamma + increaseValue, Constants.GAMMA_MAX);

        if (newGamma == Constants.GAMMA_MAX) {
            context.getSource().sendFeedback(Text.translatable("gamma.max_limit_reached").formatted(Formatting.YELLOW));
        } else {
            setGamma(newGamma);
            ModConfig.setGammaEnabled(true);
            context.getSource().sendFeedback(Text.translatable("gamma.increased", increaseValue, newGamma));
        }
        return 1;
    }

    private static int executeGammaDecrease(CommandContext<FabricClientCommandSource> context) {
        double decreaseValue = DoubleArgumentType.getDouble(context, "value");
        double currentGamma = Constants.client.options.getGamma().getValue();
        double newGamma = Math.max(currentGamma - decreaseValue, Constants.GAMMA_MIN);

        if (newGamma == Constants.GAMMA_MIN) {
            context.getSource().sendFeedback(Text.translatable("gamma.min_limit_reached").formatted(Formatting.YELLOW));
        } else {
            setGamma(newGamma);
            ModConfig.setGammaEnabled(newGamma > Constants.GAMMA_OFF);
            context.getSource().sendFeedback(Text.translatable("gamma.decreased", decreaseValue, newGamma));
        }
        return 1;
    }

    private static int executeGammaReset(CommandContext<FabricClientCommandSource> context) {
        double currentGamma = ModConfig.getGammaValue();
        double vanillaGamma = Constants.GAMMA_OFF;

        if (currentGamma == vanillaGamma) {
            context.getSource().sendFeedback(Text.translatable("gamma.already_reset").formatted(Formatting.YELLOW));
        } else {
            setGamma(vanillaGamma);
            ModConfig.setGammaEnabled(false);
            context.getSource().sendFeedback(Text.translatable("gamma.reset", vanillaGamma));
        }

        return 1;
    }

    public static void setGamma(double value) {
        if (Constants.client == null) {
            EssentialsClient.LOGGER.warn("Minecraft client is null, skipping gamma update");
            ModConfig.setGammaValue(value);
            return;
        }

        GameOptions options = Constants.client.options;
        if (options == null || options.getGamma() == null) {
            EssentialsClient.LOGGER.warn("Game options are unavailable, storing gamma value in config");
            ModConfig.setGammaValue(value);
            return;
        }

        try {
            options.getGamma().setValue(value);
            options.write();
            ModConfig.setGammaValue(value);
            ModConfig.setGammaEnabled(value > Constants.GAMMA_OFF);
        } catch (Exception exception) {
            EssentialsClient.LOGGER.error("An error occurred while setting the gamma value", exception);
        }
    }
}