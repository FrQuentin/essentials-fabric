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
    private static final int PADDING_TOP = 5;
    private static final int PADDING_LEFT = 8;
    private static final int PADDING_BOTTOM = 3;
    private static final int PADDING_RIGHT = 8;
    private static final int INITIAL_POSITION_X = 5;
    private static final int INITIAL_POSITION_Y = 5;
    private static String cachedBiomeName = null;
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
            String biomeId = biomeEntry.getKey().map(key -> key.getValue().toString()).orElse("Unknown Biome");

            String biomeTranslationKey = "biome." + biomeId.replace(":", ".");
            cachedBiomeName = Text.translatable(biomeTranslationKey).getString();
            lastPlayerPos = playerPos;
        }

        String coordinatesText = String.format("X: %.2f, Y: %.2f, Z: %.2f", playerPos.x, playerPos.y, playerPos.z);
        TextRenderer textRenderer = Constants.client.textRenderer;

        int coordinatesWidth = textRenderer.getWidth(coordinatesText);
        int biomeWidth = textRenderer.getWidth("Biome: " + cachedBiomeName);
        int maxWidth = Math.max(coordinatesWidth, biomeWidth);
        int boxWidth = maxWidth + PADDING_LEFT + PADDING_RIGHT;
        int boxHeight = (9 * 2) + PADDING_TOP + PADDING_BOTTOM;
        int boxX = INITIAL_POSITION_X;
        int boxY = INITIAL_POSITION_Y;

        context.fill(
                boxX,
                boxY,
                boxX + boxWidth,
                boxY + boxHeight,
                0x80000000
        );
        context.drawText(
                textRenderer,
                coordinatesText,
                boxX + PADDING_LEFT,
                boxY + PADDING_TOP,
                0xFFFFFFFF,
                false
        );
        context.drawText(
                textRenderer,
                "Biome: " + cachedBiomeName,
                boxX + PADDING_LEFT,
                boxY + PADDING_TOP + 11,
                0xFFFFFFFF,
                false
        );
    }
}