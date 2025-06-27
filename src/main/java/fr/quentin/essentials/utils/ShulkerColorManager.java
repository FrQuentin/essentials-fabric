package fr.quentin.essentials.utils;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.awt.*;
import java.util.Map;

public class ShulkerColorManager {
    public static final Map<Item, Color> SHULKER_COLORS = Map.ofEntries(
            Map.entry(Items.SHULKER_BOX, new Color(142, 108, 142)),
            Map.entry(Items.WHITE_SHULKER_BOX, new Color(225, 230, 230)),
            Map.entry(Items.LIGHT_GRAY_SHULKER_BOX, new Color(137, 137, 128)),
            Map.entry(Items.GRAY_SHULKER_BOX, new Color(60, 65, 68)),
            Map.entry(Items.BLACK_SHULKER_BOX, new Color(31, 31, 35)),
            Map.entry(Items.RED_SHULKER_BOX, new Color(152, 36, 34)),
            Map.entry(Items.ORANGE_SHULKER_BOX, new Color(241, 114, 15)),
            Map.entry(Items.YELLOW_SHULKER_BOX, new Color(249, 196, 35)),
            Map.entry(Items.LIME_SHULKER_BOX, new Color(110, 185, 24)),
            Map.entry(Items.GREEN_SHULKER_BOX, new Color(83, 107, 29)),
            Map.entry(Items.CYAN_SHULKER_BOX, new Color(22, 133, 144)),
            Map.entry(Items.LIGHT_BLUE_SHULKER_BOX, new Color(57, 177, 215)),
            Map.entry(Items.BLUE_SHULKER_BOX, new Color(49, 52, 152)),
            Map.entry(Items.PURPLE_SHULKER_BOX, new Color(113, 37, 166)),
            Map.entry(Items.MAGENTA_SHULKER_BOX, new Color(183, 61, 172)),
            Map.entry(Items.PINK_SHULKER_BOX, new Color(239, 135, 166)),
            Map.entry(Items.BROWN_SHULKER_BOX, new Color(113, 70, 39))
    );

    public static final Map<Item, Integer> SHULKER_TEXT_COLORS = Map.ofEntries(
            Map.entry(Items.SHULKER_BOX, 0xFFFFFFFF),
            Map.entry(Items.WHITE_SHULKER_BOX, 0xFF2A2A2A),
            Map.entry(Items.LIGHT_GRAY_SHULKER_BOX, 0xFF2A2A2A),
            Map.entry(Items.GRAY_SHULKER_BOX, 0xFFFFFFFF),
            Map.entry(Items.BLACK_SHULKER_BOX, 0xFFFFFFFF),
            Map.entry(Items.RED_SHULKER_BOX, 0xFFFFFFFF),
            Map.entry(Items.ORANGE_SHULKER_BOX, 0xFF2A2A2A),
            Map.entry(Items.YELLOW_SHULKER_BOX, 0xFF2A2A2A),
            Map.entry(Items.LIME_SHULKER_BOX, 0xFF2A2A2A),
            Map.entry(Items.GREEN_SHULKER_BOX, 0xFFFFFFFF),
            Map.entry(Items.CYAN_SHULKER_BOX, 0xFFFFFFFF),
            Map.entry(Items.LIGHT_BLUE_SHULKER_BOX, 0xFF2A2A2A),
            Map.entry(Items.BLUE_SHULKER_BOX, 0xFFFFFFFF),
            Map.entry(Items.PURPLE_SHULKER_BOX, 0xFFFFFFFF),
            Map.entry(Items.MAGENTA_SHULKER_BOX, 0xFFFFFFFF),
            Map.entry(Items.PINK_SHULKER_BOX, 0xFF2A2A2A),
            Map.entry(Items.BROWN_SHULKER_BOX, 0xFFFFFFFF)
    );

    public static Color getColor(Item item) {
        return SHULKER_COLORS.getOrDefault(item, new Color(160, 160, 160));
    }

    public static int getTextColor(Item item) {
        return SHULKER_TEXT_COLORS.getOrDefault(item, 0xFFFFFFFF);
    }

    public static Color getColorForShulker(Item item) {
        return getColor(item);
    }
}