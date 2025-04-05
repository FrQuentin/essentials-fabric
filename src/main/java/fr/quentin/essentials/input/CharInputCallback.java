package fr.quentin.essentials.input;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface CharInputCallback {
    Event<CharInputCallback> EVENT = EventFactory.createArrayBacked(CharInputCallback.class,
            listeners -> (window, codepoint, modifiers) -> {
                for (CharInputCallback listener : listeners) {
                    ActionResult result = listener.onCharInput(window, codepoint, modifiers);
                    if (result != ActionResult.PASS)
                        return result;
                }
                return ActionResult.PASS;
            });

    ActionResult onCharInput(long window, int codepoint, int modifiers);
}