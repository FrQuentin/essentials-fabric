package fr.quentin.essentials.gui.screen;

import fr.quentin.essentials.Essentials;
import fr.quentin.essentials.utils.ShulkerColorManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class ShulkerPreviewScreen extends Screen {
    private static final Identifier TEXTURE = Identifier.of(Essentials.MOD_ID, "textures/gui/container/preview_shulker_box.png");
    private final int backgroundWidth = 176;
    private final int backgroundHeight = 78;
    private final Color color;
    private final Text title;
    private final List<ItemStack> inventory;
    private final Screen parent;
    private int x = 0;
    private int y = 0;

    public ShulkerPreviewScreen(ItemStack itemStack, Screen parent) {
        super(Text.literal("Preview Shulker"));
        this.color = ShulkerColorManager.SHULKER_COLORS.get(itemStack.getItem());
        this.title = itemStack.getName();
        this.inventory = Objects.requireNonNull(itemStack.get(DataComponentTypes.CONTAINER)).stream().toList();
        this.parent = parent;

    }

    @Override
    protected void init() {
        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.renderBackground(context, mouseX, mouseY, delta);
        this.renderItems(context, this.inventory, this.x + 8, this.y + 18);

        int selectedSlot = getSlot(mouseX, mouseY);
        if (   -1 < selectedSlot && selectedSlot < this.inventory.size()
                && !this.inventory.get(selectedSlot).isOf(Items.AIR)) {
            this.renderTooltip(context, this.inventory.get(selectedSlot), mouseX, mouseY);
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderDarkening(context);
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, this.x, this.y, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 256, 256, this.color.getRGB());
        context.drawText(textRenderer, this.title, this.x + 8, this.y + 6, 0x404040, false);
    }

    @Override
    public void close() {
        if (client != null) {
            client.setScreen(parent);
        }
    }

    private void renderItems(DrawContext context, List<ItemStack> inventory, int x, int y) {
        int baseX = x;
        int count = 0;
        for (ItemStack item : inventory) {
            count++;
            context.drawItem(item, x, y);
            context.drawStackOverlay(textRenderer, item, x, y);
            x += 18;
            if (count % 9 == 0) {
                x = baseX;
                y += 18;
            }
        }
    }

    private int getSlot(int i, int j) {
        int x = this.x + 7;
        int y = this.y + 17;
        int slotX = (i - x) / 18;
        int slotY = (j - y) / 18;

        if (i < x || j < y || i > x + 9 * 18 - 1 || j > y + 3 * 18 - 1) {
            return -1;
        }
        return slotX + slotY * 9;
    }

    private void renderTooltip(DrawContext context, ItemStack stack, int x, int y) {
        context.drawItemTooltip(textRenderer, stack, x, y);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}