package fr.quentin.essentials.option;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ModKeyBinding {
    public static KeyBinding gammaKey;

    public static void register() {
        gammaKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.gamma.toggle",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_G,
                        "key.categories.essentials"
                )
        );
    }
}