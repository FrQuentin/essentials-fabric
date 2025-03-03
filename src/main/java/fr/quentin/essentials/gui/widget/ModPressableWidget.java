package fr.quentin.essentials.gui.widget;

import fr.quentin.essentials.Essentials;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.input.KeyCodes;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class ModPressableWidget extends ClickableWidget {
    private static final ButtonTextures TEXTURES = new ButtonTextures(
            Identifier.of(Essentials.MOD_ID, "widget/button"), Identifier.of(Essentials.MOD_ID, "widget/button_disabled"), Identifier.of(Essentials.MOD_ID, "widget/button_highlighted")
    );

    public ModPressableWidget(int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);
    }

    public abstract void onPress();

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        context.drawGuiTexture(
                RenderLayer::getGuiTextured,
                TEXTURES.get(this.active, this.isSelected()),
                this.getX(),
                this.getY(),
                this.getWidth(),
                this.getHeight(),
                ColorHelper.getWhite(this.alpha)
        );
        int textColor = this.active ? 16777215 : 10526880;
        this.drawMessage(context, minecraftClient.textRenderer, textColor | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }

    public void drawMessage(DrawContext context, TextRenderer textRenderer, int color) {
        this.drawScrollableText(context, textRenderer, 2, color);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.onPress();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.active || !this.visible) {
            return false;
        } else if (KeyCodes.isToggle(keyCode)) {
            this.playDownSound(MinecraftClient.getInstance().getSoundManager());
            this.onPress();
            return true;
        } else {
            return false;
        }
    }
}