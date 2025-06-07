package fr.quentin.essentials.gui.screen;

import fr.quentin.essentials.config.ModConfig;
import fr.quentin.essentials.utils.Constants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;

@Environment(EnvType.CLIENT)
public class CoordinatesOverlay {
    private static final int BACKGROUND_COLOR = 0x70000000;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int PADDING_TOP = 5;
    private static final int PADDING_LEFT = 8;
    private static final int PADDING_BOTTOM = 7;
    private static final int PADDING_RIGHT = 8;
    private static final int INITIAL_POSITION_X = 5;
    private static final int INITIAL_POSITION_Y = 5;
    private static Text cachedBiomeText = null;
    private static Vec3d lastPlayerPos = null;

    private static float alpha = 0.0f;
    private static final float FADE_SPEED = 0.05f;

    public static void render(DrawContext context) {
        if (!ModConfig.isCoordinatesEnabled()) {
            alpha = 0.0f;
            return;
        }

        if (Constants.client.player == null || Constants.client.world == null) return;

        DebugHud debugHud = Constants.client.getDebugHud();
        if (debugHud != null && debugHud.shouldShowDebugHud()) {
            return;
        }

        Vec3d playerPos = Constants.client.player.getPos();

        if (alpha < 1.0f) {
            alpha = Math.min(alpha + FADE_SPEED, 1.0f);
        }

        if (lastPlayerPos == null || !lastPlayerPos.equals(playerPos)) {
            RegistryEntry<Biome> biomeEntry = Constants.client.world.getBiome(Constants.client.player.getBlockPos());
            String biomeId = biomeEntry.getKey().map(key -> key.getValue().toString()).orElse("unknown");
            String biomeTranslationKey = "biome." + biomeId.replace(":", ".");
            Text biomeNameText = Text.translatable(biomeTranslationKey);

            cachedBiomeText = Text.translatable("overlay.essentials.biome_id", biomeNameText);
            lastPlayerPos = playerPos;
        }

        Text coordinatesText = Text.translatable("overlay.essentials.coordinates_xyz",
                String.format("%.2f", playerPos.x),
                String.format("%.2f", playerPos.y),
                String.format("%.2f", playerPos.z));
        TextRenderer textRenderer = Constants.client.textRenderer;

        int coordinatesWidth = textRenderer.getWidth(coordinatesText);
        int biomeWidth = textRenderer.getWidth(cachedBiomeText);
        int maxWidth = Math.max(coordinatesWidth, biomeWidth);
        int boxWidth = maxWidth + PADDING_LEFT + PADDING_RIGHT;
        int boxHeight = (9 * 2) + PADDING_TOP + PADDING_BOTTOM;
        int boxX = INITIAL_POSITION_X;
        int boxY = INITIAL_POSITION_Y;

        drawRoundedRectWithFade(context, boxX, boxY, boxX + boxWidth, boxY + boxHeight, alpha);

        int fadedTextColor = applyAlpha(TEXT_COLOR, alpha);
        context.drawText(
                textRenderer,
                coordinatesText,
                boxX + PADDING_LEFT,
                boxY + PADDING_TOP,
                fadedTextColor,
                false
        );
        context.drawText(
                textRenderer,
                cachedBiomeText,
                boxX + PADDING_LEFT,
                boxY + PADDING_TOP + 12,
                fadedTextColor,
                false
        );
    }

    private static int applyAlpha(int color, float alpha) {
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;
        int originalAlpha = (color >> 24) & 0xFF;
        int newAlpha = (int) (originalAlpha * alpha) & 0xFF;

        return (newAlpha << 24) | (red << 16) | (green << 8) | blue;
    }

    private static void drawRoundedRectWithFade(DrawContext context, int left, int top, int right, int bottom, float alpha) {
        int fadedBackgroundColor = applyAlpha(BACKGROUND_COLOR, alpha);

        context.fill(left + 1, top, right - 1, bottom, fadedBackgroundColor);
        context.fill(left, top + 1, left + 1, bottom - 1, fadedBackgroundColor);
        context.fill(right - 1, top + 1, right, bottom - 1, fadedBackgroundColor);
        context.fill(left + 1, top, left + 1, top + 1, fadedBackgroundColor);
        context.fill(right - 1, top, right - 1, top + 1, fadedBackgroundColor);
        context.fill(left + 1, bottom - 1, left + 1, bottom, fadedBackgroundColor);
        context.fill(right - 1, bottom - 1, right - 1, bottom, fadedBackgroundColor);
    }
}