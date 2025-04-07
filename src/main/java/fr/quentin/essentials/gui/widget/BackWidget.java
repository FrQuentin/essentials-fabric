package fr.quentin.essentials.gui.widget;

import fr.quentin.essentials.Essentials;
import fr.quentin.essentials.gui.screen.NavigatorScreen;
import fr.quentin.essentials.utils.Constants;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class BackWidget extends ClickableWidget {
    private static final Identifier TEXTURE = Identifier.of(Essentials.MOD_ID, "textures/gui/sprites/widget/back.png");
    private static final Identifier TEXTURE_HIGHLIGHTED = Identifier.of(Essentials.MOD_ID, "textures/gui/sprites/widget/back_highlighted.png");
    private final int x;
    private final int y;

    public BackWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Text.empty());
        this.x = x;
        this.y = y;

        this.setTooltip(Tooltip.of(Text.translatable("tooltip.essentials.balloons.back").formatted(Formatting.GRAY)));
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, this.x, this.y, 0.0F, 0.0F, this.width, this.height, 16, 14);
        if (isHovered()) {
            context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_HIGHLIGHTED, this.x, this.y, 0.0F, 0.0F, this.width, this.height, 16, 14);
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        return;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (Constants.client != null) {
            Constants.client.setScreen(new NavigatorScreen(Text.translatable("screen.essentials.navigator.title")));
        }
    }
}
