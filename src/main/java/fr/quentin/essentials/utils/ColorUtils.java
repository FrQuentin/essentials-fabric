package fr.quentin.essentials.utils;

import java.awt.*;

public class ColorUtils {
    public static int getContrastingTextColor(Color background) {
        double luminance = 0.2126 * background.getRed()
                + 0.7152 * background.getGreen()
                + 0.0722 * background.getBlue();

        int gray = 255 - (int) luminance;
        gray = Math.max(64, Math.min(224, gray));

        return (0xFF << 24) | (gray << 16) | (gray << 8) | gray;
    }
}