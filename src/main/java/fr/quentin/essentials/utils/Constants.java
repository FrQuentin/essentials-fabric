package fr.quentin.essentials.utils;

import net.minecraft.client.MinecraftClient;

public final class Constants {
    // Minecraft Client
    public static final MinecraftClient client = MinecraftClient.getInstance();

    // Gamma Settings
    public static final double GAMMA_ON = 1500.0;
    public static final double GAMMA_OFF = 1.0;
    public static final double GAMMA_MIN = -10000.0;
    public static final double GAMMA_MAX = 10000.0;

    // Button Sizes and Padding
    public static final int BUTTON_SIZE = 20;
    public static final int SMALL_PADDING = 4;
    public static final int MEDIUM_PADDING = 8;

    // Error messages
    public static final String ERROR_CLIENT_NULL = "Cannot perform action: client is null";
    public static final String ERROR_OPEN_DIRECTORY = "Failed to open Minecraft directory";

    // ShulkerPreviewScreen Constants
    public static final int SHULKER_BACKGROUND_WIDTH = 176;
    public static final int SHULKER_BACKGROUND_HEIGHT = 78;
    public static final int SHULKER_INVENTORY_START_X = 8;
    public static final int SHULKER_INVENTORY_START_Y = 18;
    public static final int SHULKER_TITLE_POS_X = 8;
    public static final int SHULKER_TITLE_POS_Y = 6;
    public static final int SHULKER_SLOTS_PER_ROW = 9;
    public static final int SHULKER_SLOT_SIZE = 18;
    public static final int SHULKER_ROWS = 3;

    // MapDecorationUtils Constants
    public static final float DEGREES_PER_ROTATION = 22.5f;
    public static final int ROTATION_MASK = 15;

    private Constants() {
        throw new UnsupportedOperationException("Cannot instantiate utility class.");
    }
}