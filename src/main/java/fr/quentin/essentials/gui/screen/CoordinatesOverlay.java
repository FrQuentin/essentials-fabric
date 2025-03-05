package fr.quentin.essentials.gui.screen;

import fr.quentin.essentials.config.ModConfig;
import fr.quentin.essentials.utils.Constants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class CoordinatesOverlay {
    private static final int PADDING_TOP = 5;
    private static final int PADDING_LEFT = 8;
    private static final int PADDING_BOTTOM = 3;
    private static final int PADDING_RIGHT = 8;
    private static final int INITIAL_POSITION_X = 5;
    private static final int INITIAL_POSITION_Y = 5;

    public static void render(DrawContext context) {
        if (ModConfig.isCoordinatesEnabled()) return;

        if (Constants.client.player == null || Constants.client.world == null) return;

        DebugHud debugHud = Constants.client.getDebugHud();
        if (debugHud != null && debugHud.shouldShowDebugHud()) {
            return;
        }

        Vec3d playerPos = Constants.client.player.getPos();
        String coordinatesText = String.format("X: %.2f, Y: %.2f, Z: %.2f", playerPos.x, playerPos.y, playerPos.z);

        TextRenderer textRenderer = Constants.client.textRenderer;
        int textWidth = textRenderer.getWidth(coordinatesText);
        int textHeight = 9;
        int boxWidth = textWidth + PADDING_LEFT + PADDING_RIGHT;
        int boxHeight = textHeight + PADDING_TOP + PADDING_BOTTOM;
        int boxX = INITIAL_POSITION_X;
        int boxY = INITIAL_POSITION_Y;

        context.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0x80000000);
        context.drawText(textRenderer, coordinatesText, boxX + PADDING_LEFT, boxY + PADDING_TOP, 0xFFFFFFFF, false);
    }
}