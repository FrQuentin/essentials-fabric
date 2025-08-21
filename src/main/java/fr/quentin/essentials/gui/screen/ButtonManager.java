package fr.quentin.essentials.gui.screen;

import fr.quentin.essentials.Essentials;
import fr.quentin.essentials.gui.widget.ModButtonWidget;
import fr.quentin.essentials.gui.widget.ModTextIconButtonWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ButtonManager {
    public static ModTextIconButtonWidget createSettingsButton(int width, ModButtonWidget.PressAction onPress, boolean hideText) {
        Text text = Text.translatable("screen.essentials.button.configuration");
        return ModTextIconButtonWidget.builder(text, onPress, hideText)
                .width(width)
                .texture(Identifier.of(Essentials.MOD_ID, "icon/configuration"), 15, 15)
                .build();
    }
    public static ModTextIconButtonWidget createFolderButton(int width, ModButtonWidget.PressAction onPress, boolean hideText) {
        Text text = Text.translatable("screen.essentials.button.open_folder");
        return ModTextIconButtonWidget.builder(text, onPress, hideText)
                .width(width)
                .texture(Identifier.of(Essentials.MOD_ID, "icon/folder"), 15, 15)
                .build();
    }

    public static void positionButton(ModTextIconButtonWidget button, int x, int y) {
        button.setPosition(x, y);
    }
}