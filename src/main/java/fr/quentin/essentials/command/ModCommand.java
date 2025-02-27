package fr.quentin.essentials.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
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
    }

    private static void registerGammaCommand(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                ClientCommandManager.literal("gamma")
                        .executes(ModCommand::executeGammaStatus)
                        .then(ClientCommandManager.literal("on")
                                .executes(ModCommand::executeGammaOn))
                        .then(ClientCommandManager.literal("off")
                                .executes(ModCommand::executeGammaOff))
                        .then(ClientCommandManager.literal("value")
                                .executes(context -> {
                                    context.getSource().sendFeedback(Text.translatable("gamma.usage").formatted(Formatting.RED));
                                    return 1;
                                })
                                .then(ClientCommandManager.argument("value", FloatArgumentType.floatArg(GammaSettings.GAMMA_MIN, GammaSettings.GAMMA_MAX))
                                        .executes(ModCommand::executeGammaSetValue)))
        );
    }

    public static class GammaSettings {
        public static final double GAMMA_ON = 1500.0;
        public static final double GAMMA_OFF = 1.0;
        public static final float GAMMA_MIN = -10000.0f;
        public static final float GAMMA_MAX = 10000.0f;
    }

    private static int executeGammaStatus(CommandContext<FabricClientCommandSource> context) {
        boolean isEnabled = ModConfig.isGammaEnabled();
        context.getSource().sendFeedback(Text.translatable(
                "gamma.current", isEnabled ? Text.translatable("enabled") : Text.translatable("disabled"))
        );
        return 1;
    }

    private static int executeGammaOn(CommandContext<FabricClientCommandSource> context) {
        if (!ModConfig.isGammaEnabled()) {
            setGamma(GammaSettings.GAMMA_ON);
            ModConfig.setGammaEnabled(true);
            context.getSource().sendFeedback(Text.translatable("gamma.on"));
        } else {
            context.getSource().sendFeedback(Text.translatable("gamma.already_on"));
        }
        return 1;
    }

    private static int executeGammaOff(CommandContext<FabricClientCommandSource> context) {
        if (ModConfig.isGammaEnabled()) {
            setGamma(GammaSettings.GAMMA_OFF);
            ModConfig.setGammaEnabled(false);
            context.getSource().sendFeedback(Text.translatable("gamma.off"));
        } else {
            context.getSource().sendFeedback(Text.translatable("gamma.already_off"));
        }
        return 1;
    }

    private static int executeGammaSetValue(CommandContext<FabricClientCommandSource> context) {
        float value = FloatArgumentType.getFloat(context, "value");
        setGamma(value);

        ModConfig.setGammaEnabled(value > GammaSettings.GAMMA_OFF);

        context.getSource().sendFeedback(Text.translatable("gamma.value", value));
        return 1;
    }

    public static void setGamma(double value) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) {
            ModConfig.setGammaValue(value);
            return;
        }

        GameOptions options = client.options;
        if (options.getGamma() == null) {
            ModConfig.setGammaValue(value);
            return;
        }

        try {
            options.getGamma().setValue(value);
            options.write();
            ModConfig.setGammaValue(value);
        } catch (Exception exception) {
            EssentialsClient.LOGGER.error("An error occurred while setting the gamma value", exception);
        }
    }
}