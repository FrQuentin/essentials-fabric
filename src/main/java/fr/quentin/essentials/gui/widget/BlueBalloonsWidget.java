package fr.quentin.essentials.gui.widget;

import fr.quentin.essentials.Essentials;
import fr.quentin.essentials.utils.Constants;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class BlueBalloonsWidget extends ClickableWidget {
    private static final Identifier TEXTURE = Identifier.of(Essentials.MOD_ID, "textures/gui/sprites/widget/blue_balloons.png");
    private static final Identifier TEXTURE_HIGHLIGHTED = Identifier.of(Essentials.MOD_ID, "textures/gui/sprites/widget/blue_balloons_highlighted.png");
    private final int x;
    private final int y;

    public BlueBalloonsWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Text.empty());
        this.x = x;
        this.y = y;

        MutableText tooltipText = Text.translatable("tooltip.essentials.balloons.blue_balloons.line1").formatted(Formatting.GRAY)
                .append("\n")
                .append(Text.translatable("tooltip.essentials.balloons.blue_balloons.line2").formatted(Formatting.GRAY))
                .append("\n\n")
                .append(Text.translatable("tooltip.essentials.balloons.click_to_travel").formatted(Formatting.GREEN));

        this.setTooltip(Tooltip.of(tooltipText));
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, this.x, this.y, 0.0F, 0.0F, this.width, this.height, 79, 34);
        if (isHovered()) {
            context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_HIGHLIGHTED, this.x, this.y, 0.0F, 0.0F, this.width, this.height, 79, 34);
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        return;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
            if (Constants.client != null && Constants.client.player != null) {
                Objects.requireNonNull(Constants.client.getNetworkHandler()).sendChatCommand("balloon blue-balloon");
                Constants.client.setScreen(null);
            }
    }
}
