package fr.quentin.essentials.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ModButtonWidget extends ModPressableWidget {
    protected static final ModButtonWidget.NarrationSupplier DEFAULT_NARRATION_SUPPLIER = Supplier::get;
    protected final ModButtonWidget.PressAction onPress;
    protected final ModButtonWidget.NarrationSupplier narrationSupplier;

    public static ModButtonWidget.Builder builder(Text message, ModButtonWidget.PressAction onPress) {
        return new ModButtonWidget.Builder(message, onPress);
    }

    public ModButtonWidget(int x, int y, int width, int height, Text message, ModButtonWidget.PressAction onPress, ModButtonWidget.NarrationSupplier narrationSupplier) {
        super(x, y, width, height, message);
        this.onPress = onPress;
        this.narrationSupplier = narrationSupplier;
    }

    @Override
    public void onPress() {
        this.onPress.onPress(this);
    }

    @Override
    protected MutableText getNarrationMessage() {
        return this.narrationSupplier.createNarrationMessage(super::getNarrationMessage);
    }

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
    }

    @Environment(EnvType.CLIENT)
    public static class Builder {
        private final Text message;
        private final ModButtonWidget.PressAction onPress;
        @Nullable
        private Tooltip tooltip;
        private int x;
        private int y;
        private int width = 150;
        private int height = 20;
        private ModButtonWidget.NarrationSupplier narrationSupplier = ModButtonWidget.DEFAULT_NARRATION_SUPPLIER;

        public Builder(Text message, ModButtonWidget.PressAction onPress) {
            this.message = message;
            this.onPress = onPress;
        }

        public ModButtonWidget.Builder position(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public ModButtonWidget.Builder width(int width) {
            this.width = width;
            return this;
        }

        public ModButtonWidget.Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public ModButtonWidget.Builder dimensions(int x, int y, int width, int height) {
            return this.position(x, y).size(width, height);
        }

        public ModButtonWidget.Builder tooltip(@Nullable Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public ModButtonWidget.Builder narrationSupplier(ModButtonWidget.NarrationSupplier narrationSupplier) {
            this.narrationSupplier = narrationSupplier;
            return this;
        }

        public ModButtonWidget build() {
            ModButtonWidget modButtonWidget = new ModButtonWidget(this.x, this.y, this.width, this.height, this.message, this.onPress, this.narrationSupplier);
            modButtonWidget.setTooltip(this.tooltip);
            return modButtonWidget;
        }
    }

    @Environment(EnvType.CLIENT)
    public interface NarrationSupplier {
        MutableText createNarrationMessage(Supplier<MutableText> textSupplier);
    }

    @Environment(EnvType.CLIENT)
    public interface PressAction {
        void onPress(ModButtonWidget button);
    }
}