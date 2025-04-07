package fr.quentin.essentials.gui.widget;

import fr.quentin.essentials.Essentials;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class OpenWorldWidget extends ClickableWidget {
    private static final Identifier TEXTURE = Identifier.of(Essentials.MOD_ID, "textures/gui/sprites/widget/openworld.png");
    private static final Identifier TEXTURE_HIGHLIGHTED = Identifier.of(Essentials.MOD_ID, "textures/gui/sprites/widget/openworld_highlighted.png");
    private final int x;
    private final int y;

    public OpenWorldWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Text.empty());
        this.x = x;
        this.y = y;

        MutableText tooltipText = Text.translatable("tooltip.essentials.navigator.open_world.line1").formatted(Formatting.GRAY)
                .append("\n")
                .append(Text.translatable("tooltip.essentials.navigator.open_world.line2").formatted(Formatting.GRAY))
                .append("\n\n")
                .append(Text.translatable("tooltip.essentials.navigator.click_to_open").formatted(Formatting.GREEN));

        this.setTooltip(Tooltip.of(tooltipText));
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, this.x, this.y, 0.0F, 0.0F, this.width, this.height, 70, 52);
        if (isHovered()) {
            context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_HIGHLIGHTED, this.x, this.y, 0.0F, 0.0F, this.width, this.height, 70, 52);
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        return;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        System.out.println("Open World button clicked!");
    }
}
