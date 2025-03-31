package fr.quentin.essentials.utils;

import fr.quentin.essentials.config.ModConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class DarknessEffectHandler {
    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (ModConfig.isDarknessEffectEnabled()) return;

            if (client.player != null) {
                StatusEffectInstance darknessEffect = client.player.getStatusEffect(StatusEffects.DARKNESS);
                if (darknessEffect != null) {
                    client.player.removeStatusEffect(StatusEffects.DARKNESS);
                }
            }
        });
    }
}