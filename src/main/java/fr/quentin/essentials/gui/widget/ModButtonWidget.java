package fr.quentin.essentials.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ModButtonWidget extends ModPressableWidget{
    protected static final ModButtonWidget.NarrationSupplier DEFAULT_NARRATION_SUPPLIER = Supplier::get;
    protected final ModButtonWidget.PressAction onPress;
    protected final ModButtonWidget.NarrationSupplier narrationSupplier;

    protected ModButtonWidget(int x, int y, int width, int height, Text message, ModButtonWidget.PressAction onPress, ModButtonWidget.NarrationSupplier narrationSupplier) {
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
    public interface NarrationSupplier {
        MutableText createNarrationMessage(Supplier<MutableText> textSupplier);
    }

    @Environment(EnvType.CLIENT)
    public interface PressAction {
        void onPress(ModButtonWidget button);
    }
}