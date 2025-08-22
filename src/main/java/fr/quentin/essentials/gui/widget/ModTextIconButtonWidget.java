package fr.quentin.essentials.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public abstract class ModTextIconButtonWidget extends ModButtonWidget {
    protected final Identifier texture;
    protected final int textureWidth;
    protected final int textureHeight;

    ModTextIconButtonWidget(
            int width,
            int height,
            Text message,
            int textureWidth,
            int textureHeight,
            Identifier texture,
            ModButtonWidget.PressAction onPress,
            @Nullable ModButtonWidget.NarrationSupplier narrationSupplier
    ) {
        super(0, 0, width, height, message, onPress, narrationSupplier == null ? DEFAULT_NARRATION_SUPPLIER : narrationSupplier);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.texture = texture;
    }

    public static ModTextIconButtonWidget.Builder builder(Text text, ModButtonWidget.PressAction onPress, boolean hideLabel) {
        return new ModTextIconButtonWidget.Builder(text, onPress, hideLabel);
    }

    @Environment(EnvType.CLIENT)
    public static class Builder {
        private final Text text;
        private final ModButtonWidget.PressAction onPress;
        private final boolean hideText;
        private int width = 150;
        @Nullable
        private Identifier texture;
        private int textureWidth;
        private int textureHeight;
        @Nullable
        ModButtonWidget.NarrationSupplier narrationSupplier;

        public Builder(Text text, ModButtonWidget.PressAction onPress, boolean hideText) {
            this.text = text;
            this.onPress = onPress;
            this.hideText = hideText;
        }

        public ModTextIconButtonWidget.Builder width(int width) {
            this.width = width;
            return this;
        }

        public ModTextIconButtonWidget.Builder texture(Identifier texture, int width, int height) {
            this.texture = texture;
            this.textureWidth = width;
            this.textureHeight = height;
            return this;
        }

        public ModTextIconButtonWidget build() {
            if (this.texture == null) {
                throw new IllegalStateException("Sprite not set");
            } else {
                int height = 20;
                return this.hideText
                        ? new IconOnly(
                        this.width, height, this.text, this.textureWidth, this.textureHeight, this.texture, this.onPress, this.narrationSupplier
                )
                        : new WithText(
                        this.width, height, this.text, this.textureWidth, this.textureHeight, this.texture, this.onPress, this.narrationSupplier
                );
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public static class IconOnly extends ModTextIconButtonWidget {
        protected IconOnly(
                int i,
                int j,
                Text text,
                int k,
                int l,
                Identifier identifier,
                ModButtonWidget.PressAction pressAction,
                @Nullable ModButtonWidget.NarrationSupplier narrationSupplier
        ) {
            super(i, j, text, k, l, identifier, pressAction, narrationSupplier);
        }

        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
            super.renderWidget(context, mouseX, mouseY, deltaTicks);
            int i = this.getX() + this.getWidth() / 2 - this.textureWidth / 2;
            int j = this.getY() + this.getHeight() / 2 - this.textureHeight / 2;
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.texture, i, j, this.textureWidth, this.textureHeight, this.alpha);
        }

        @Override
        public void drawMessage(DrawContext context, TextRenderer textRenderer, int color) {
        }
    }

    @Environment(EnvType.CLIENT)
    public static class WithText extends ModTextIconButtonWidget {
        protected WithText(
                int i,
                int j,
                Text text,
                int k,
                int l,
                Identifier identifier,
                ModButtonWidget.PressAction pressAction,
                @Nullable ModButtonWidget.NarrationSupplier narrationSupplier
        ) {
            super(i, j, text, k, l, identifier, pressAction, narrationSupplier);
        }

        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
            super.renderWidget(context, mouseX, mouseY, deltaTicks);
            int i = this.getX() + this.getWidth() - this.textureWidth - 2;
            int j = this.getY() + this.getHeight() / 2 - this.textureHeight / 2;
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.texture, i, j, this.textureWidth, this.textureHeight, this.alpha);
        }

        @Override
        public void drawMessage(DrawContext context, TextRenderer textRenderer, int color) {
            int i = this.getX() + 2;
            int j = this.getX() + this.getWidth() - this.textureWidth - 4;
            int k = this.getX() + this.getWidth() / 2;
            drawScrollableText(context, textRenderer, this.getMessage(), k, i, this.getY(), j, this.getY() + this.getHeight(), color);
        }
    }
}