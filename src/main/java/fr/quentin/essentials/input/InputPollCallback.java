package fr.quentin.essentials.input;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface InputPollCallback {
    Event<InputPollCallback> EVENT = EventFactory.createArrayBacked(InputPollCallback.class,
            listeners -> () -> {
                for (InputPollCallback listener : listeners) {
                    listener.onPoll();
                }
            });

    void onPoll();
}