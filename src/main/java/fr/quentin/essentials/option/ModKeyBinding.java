package fr.quentin.essentials.option;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ModKeyBinding {
    public static KeyBinding configurationKey;
    public static KeyBinding gammaKey;
    public static KeyBinding coordinatesKey;

    public static void register() {
        configurationKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.essentials.config",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_H,
                        "key.categories.essentials"
                )
        );
        gammaKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.gamma.toggle",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_G,
                        "key.categories.essentials"
                )
        );
        coordinatesKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.coordinates.toggle",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_UNKNOWN,
                        "key.categories.essentials"
                )
        );
    }
}