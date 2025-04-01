package fr.quentin.essentials.gui.screen;

import fr.quentin.essentials.Essentials;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ButtonManager {
    public static TextIconButtonWidget createSettingsButton(int width, ButtonWidget.PressAction onPress, boolean hideText) {
        Text text = Text.translatable("screen.essentials.button.configuration");
        return TextIconButtonWidget.builder(text, onPress, hideText)
                .width(width)
                .texture(Identifier.of(Essentials.MOD_ID, "icon/configuration"), 15, 15)
                .build();
    }
    public static TextIconButtonWidget createFolderButton(int width, ButtonWidget.PressAction onPress, boolean hideText) {
        Text text = Text.translatable("screen.essentials.button.open_folder");
        return TextIconButtonWidget.builder(text, onPress, hideText)
                .width(width)
                .texture(Identifier.of(Essentials.MOD_ID, "icon/folder"), 15, 15)
                .build();
    }

    public static void positionButton(TextIconButtonWidget button, int x, int y) {
        button.setPosition(x, y);
    }
}