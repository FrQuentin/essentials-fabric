package fr.quentin.essentials.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import fr.quentin.essentials.EssentialsClient;
import fr.quentin.essentials.config.ModConfig;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
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
                                .executes(context -> {
                                    boolean newState = ModConfig.isCoordinatesEnabled();
                                    ModConfig.setCoordinatesEnabled(newState);

                                    if (newState) {
                                        context.getSource().sendFeedback(Text.translatable("coordinates.toggled_on"));
                                    } else {
                                        context.getSource().sendFeedback(Text.translatable("coordinates.toggled_off"));
                                    }
                                    return 1;
                                }))
        );
    }

    private static void registerGammaCommand(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                ClientCommandManager.literal("gamma")
                        .executes(ModCommand::executeGammaStatus)
                        .then(ClientCommandManager.literal("toggle")
                                .executes(ModCommand::executeGammaToggle))
                        .then(ClientCommandManager.literal("increase")
                                .then(ClientCommandManager.argument("value", DoubleArgumentType.doubleArg(0.1, GammaSettings.GAMMA_MAX))
                                        .executes(ModCommand::executeGammaIncrease)))
                        .then(ClientCommandManager.literal("decrease")
                                .then(ClientCommandManager.argument("value", DoubleArgumentType.doubleArg(0.1, GammaSettings.GAMMA_MAX))
                                        .executes(ModCommand::executeGammaDecrease)))
                        .then(ClientCommandManager.literal("reset")
                                .executes(ModCommand::executeGammaReset))
        );
    }

    public static class GammaSettings {
        public static final double GAMMA_ON = 1500.0;
        public static final double GAMMA_OFF = 1.0;
        public static final double GAMMA_MIN = -10000.0;
        public static final double GAMMA_MAX = 10000.0;
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
            setGamma(GammaSettings.GAMMA_ON);
            context.getSource().sendFeedback(Text.translatable("gamma.toggled_on"));
        } else {
            setGamma(GammaSettings.GAMMA_OFF);
            context.getSource().sendFeedback(Text.translatable("gamma.toggled_off"));
        }
        return 1;
    }

    private static int executeGammaIncrease(CommandContext<FabricClientCommandSource> context) {
        double increaseValue = DoubleArgumentType.getDouble(context, "value");
        double currentGamma = MinecraftClient.getInstance().options.getGamma().getValue();
        double newGamma = Math.min(currentGamma + increaseValue, GammaSettings.GAMMA_MAX);

        if (newGamma == GammaSettings.GAMMA_MAX) {
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
        double currentGamma = MinecraftClient.getInstance().options.getGamma().getValue();
        double newGamma = Math.max(currentGamma - decreaseValue, GammaSettings.GAMMA_MIN);

        if (newGamma == GammaSettings.GAMMA_MIN) {
            context.getSource().sendFeedback(Text.translatable("gamma.min_limit_reached").formatted(Formatting.YELLOW));
        } else {
            setGamma(newGamma);
            ModConfig.setGammaEnabled(newGamma > GammaSettings.GAMMA_OFF);
            context.getSource().sendFeedback(Text.translatable("gamma.decreased", decreaseValue, newGamma));
        }
        return 1;
    }

    private static int executeGammaReset(CommandContext<FabricClientCommandSource> context) {
        double currentGamma = ModConfig.getGammaValue();
        double vanillaGamma = GammaSettings.GAMMA_OFF;

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
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            EssentialsClient.LOGGER.warn("Minecraft client is null, skipping gamma update");
            ModConfig.setGammaValue(value);
            return;
        }

        GameOptions options = client.options;
        if (options == null || options.getGamma() == null) {
            EssentialsClient.LOGGER.warn("Game options are unavailable, storing gamma value in config");
            ModConfig.setGammaValue(value);
            return;
        }

        try {
            options.getGamma().setValue(value);
            options.write();
            ModConfig.setGammaValue(value);
            ModConfig.setGammaEnabled(value > GammaSettings.GAMMA_OFF);
        } catch (Exception exception) {
            EssentialsClient.LOGGER.error("An error occurred while setting the gamma value", exception);
        }
    }
}