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

    public static void render(DrawContext context) {
        if (!ModConfig.isCoordinatesEnabled()) return;

        if (Constants.client.player == null || Constants.client.world == null) return;

        DebugHud debugHud = Constants.client.getDebugHud();
        if (debugHud != null && debugHud.shouldShowDebugHud()) {
            return;
        }
        Vec3d playerPos = Constants.client.player.getPos();

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

        drawRoundedRect(context, boxX, boxY, boxX + boxWidth, boxY + boxHeight);

        context.drawText(
                textRenderer,
                coordinatesText,
                boxX + PADDING_LEFT,
                boxY + PADDING_TOP,
                TEXT_COLOR,
                false
        );
        context.drawText(
                textRenderer,
                cachedBiomeText,
                boxX + PADDING_LEFT,
                boxY + PADDING_TOP + 12,
                TEXT_COLOR,
                false
        );
    }

    private static void drawRoundedRect(DrawContext context, int left, int top, int right, int bottom) {
        context.fill(left + 1, top, right - 1, bottom, BACKGROUND_COLOR);
        context.fill(left, top + 1, left + 1, bottom - 1, BACKGROUND_COLOR);
        context.fill(right - 1, top + 1, right, bottom - 1, BACKGROUND_COLOR);
        context.fill(left + 1, top, left + 1, top + 1, BACKGROUND_COLOR);
        context.fill(right - 1, top, right - 1, top + 1, BACKGROUND_COLOR);
        context.fill(left + 1, bottom - 1, left + 1, bottom, BACKGROUND_COLOR);
        context.fill(right - 1, bottom - 1, right - 1, bottom, BACKGROUND_COLOR);
    }
}