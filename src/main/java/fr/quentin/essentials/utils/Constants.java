package fr.quentin.essentials.utils;

import net.minecraft.client.MinecraftClient;

public final class Constants {
    public static final MinecraftClient client = MinecraftClient.getInstance();
    public static final double GAMMA_ON = 1500.0;
    public static final double GAMMA_OFF = 1.0;
    public static final double GAMMA_MIN = -10000.0;
    public static final double GAMMA_MAX = 10000.0;
    public static final String ORIGIN_REALMS_ADDRESS = "play.originrealms.com";
    public static final String ACCESSIBILITY_TRANSLATION_KEY = "narrator.button.accessibility";
    public static final String CREDITS_TRANSLATION_KEY = "options.credits_and_attribution";

    private Constants() {
        throw new UnsupportedOperationException("Cannot instantiate utility class.");
    }
}