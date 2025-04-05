package fr.quentin.essentials.input;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface KeyPressCallback {
    Event<KeyPressCallback> EVENT = EventFactory.createArrayBacked(KeyPressCallback.class,
            listeners -> (window, key, scancode, action, modifiers) -> {
                for (KeyPressCallback listener : listeners) {
                    ActionResult result = listener.onKeyPress(window, key, scancode, action, modifiers);
                    if (result != ActionResult.PASS)
                        return result;
                }
                return ActionResult.PASS;
            });

    ActionResult onKeyPress(long window, int key, int scancode, int action, int modifiers);
}