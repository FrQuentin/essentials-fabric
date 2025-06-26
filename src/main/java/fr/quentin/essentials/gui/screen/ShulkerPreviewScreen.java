package fr.quentin.essentials.gui.screen;

import fr.quentin.essentials.Essentials;
import fr.quentin.essentials.utils.Constants;
import fr.quentin.essentials.utils.ShulkerColorManager;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ShulkerPreviewScreen extends Screen {
    private static final Identifier TEXTURE = Identifier.of(Essentials.MOD_ID, "textures/gui/container/preview_shulker_box.png");

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
        this.inventory = itemStack.get(DataComponentTypes.CONTAINER) != null
                ? Objects.requireNonNull(itemStack.get(DataComponentTypes.CONTAINER)).stream().toList()
                : Collections.emptyList();
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.x = (this.width - Constants.SHULKER_BACKGROUND_WIDTH) / 2;
        this.y = (this.height - Constants.SHULKER_BACKGROUND_HEIGHT) / 2;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.renderBackground(context, mouseX, mouseY, delta);
        this.renderItems(context, this.inventory, this.x + Constants.SHULKER_INVENTORY_START_X, this.y + Constants.SHULKER_INVENTORY_START_Y, mouseX, mouseY);

        int selectedSlot = getSlot(mouseX, mouseY);
        if (selectedSlot >= 0 && selectedSlot < this.inventory.size() && !this.inventory.get(selectedSlot).isOf(Items.AIR)) {
            this.renderTooltip(context, this.inventory.get(selectedSlot), mouseX, mouseY);
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderDarkening(context);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, this.x, this.y, 0.0F, 0.0F,
                Constants.SHULKER_BACKGROUND_WIDTH, Constants.SHULKER_BACKGROUND_HEIGHT, 256, 256, this.color.getRGB());
        context.drawText(textRenderer, this.title, this.x + Constants.SHULKER_TITLE_POS_X, this.y + Constants.SHULKER_TITLE_POS_Y, Constants.SHULKER_TITLE_COLOR, false);
    }

    @Override
    public void close() {
        if (Constants.client != null) {
            Constants.client.setScreen(parent);
        } else {
            System.err.println(Constants.ERROR_CLIENT_NULL);
        }
    }

    private void renderItems(DrawContext context, List<ItemStack> inventory, int startX, int startY, int mouseX, int mouseY) {
        int x = startX;
        int y = startY;

        int hoveredSlot = getSlot(mouseX, mouseY);

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack item = inventory.get(i);

            int slotX = x;
            int slotY = y;

            if (i == hoveredSlot) {
                context.fill(slotX, slotY, slotX + Constants.SHULKER_SLOT_SIZE - 1, slotY + Constants.SHULKER_SLOT_SIZE - 1, 0x80FFFFFF);
            }

            context.drawItem(item, slotX, slotY);
            context.drawStackOverlay(textRenderer, item, slotX, slotY);

            if ((i + 1) % Constants.SHULKER_SLOTS_PER_ROW == 0) {
                x = startX;
                y += Constants.SHULKER_SLOT_SIZE;
            } else {
                x += Constants.SHULKER_SLOT_SIZE;
            }
        }
    }

    private int getSlot(int mouseX, int mouseY) {
        int slotAreaX = this.x + Constants.SHULKER_INVENTORY_START_X - 1;
        int slotAreaY = this.y + Constants.SHULKER_INVENTORY_START_Y - 1;
        int slotAreaWidth = Constants.SHULKER_SLOTS_PER_ROW * Constants.SHULKER_SLOT_SIZE;
        int slotAreaHeight = Constants.SHULKER_ROWS * Constants.SHULKER_SLOT_SIZE;

        if (mouseX < slotAreaX || mouseY < slotAreaY ||
                mouseX >= slotAreaX + slotAreaWidth || mouseY >= slotAreaY + slotAreaHeight) {
            return -1;
        }

        int slotX = (mouseX - slotAreaX) / Constants.SHULKER_SLOT_SIZE;
        int slotY = (mouseY - slotAreaY) / Constants.SHULKER_SLOT_SIZE;

        return slotX + slotY * Constants.SHULKER_SLOTS_PER_ROW;
    }

    private void renderTooltip(DrawContext context, ItemStack stack, int x, int y) {
        context.drawItemTooltip(textRenderer, stack, x, y);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}