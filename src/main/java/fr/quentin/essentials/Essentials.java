package fr.quentin.essentials;

import fr.quentin.essentials.utils.ResourcePackManager;
import net.fabricmc.api.ModInitializer;

public class Essentials implements ModInitializer {
	public static final String MOD_ID = "essentials";
	public static final String MOD_NAME = "Essentials";

	@Override
	public void onInitialize() {
		ResourcePackManager.cleanupOldResourcePacks();
		ResourcePackManager.copyResourcePacks();
	}
}