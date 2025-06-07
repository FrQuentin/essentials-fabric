package fr.quentin.essentials;

import fr.quentin.essentials.config.ModConfig;
import fr.quentin.essentials.utils.ResourcePackManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class Essentials implements ModInitializer {
	public static final String MOD_ID = "essentials";
	public static final String MOD_NAME = "Essentials";

	@Override
	public void onInitialize() {
		ResourcePackManager.cleanupOldResourcePacks();
		ResourcePackManager.copyResourcePacks();
		ServerLivingEntityEvents.AFTER_DEATH.register(this::onEntityDeath);
	}

	private void onEntityDeath(LivingEntity entity, DamageSource damageSource) {
		if (entity instanceof ServerPlayerEntity player && ModConfig.isDeathCoordsEnabled()) {
			BlockPos deathPos = player.getBlockPos();

			Text message = Text.translatable("message.essentials.chat.death_coords")
					.append(Text.literal(String.format("X: %d, Y: %d, Z: %d",
									deathPos.getX(),
									deathPos.getY(),
									deathPos.getZ())
							)
					);
			player.sendMessage(message, false);
		}
	}
}