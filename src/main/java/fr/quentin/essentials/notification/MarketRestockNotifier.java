package fr.quentin.essentials.notification;

import fr.quentin.essentials.config.ModConfig;
import fr.quentin.essentials.toast.MarketRestockToast;
import fr.quentin.essentials.utils.Constants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class MarketRestockNotifier {
    private static long lastNotificationTime = 0;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!ModConfig.isMarketRestockNotificationEnabled()) return;
            if (!isPlayerOnOriginRealms()) return;

            long currentTime = 0;
            if (Constants.client.world != null) {
                currentTime = Constants.client.world.getTimeOfDay() % 24000;
            }

            long restockTime = 1000;

            if (currentTime >= restockTime && currentTime < restockTime + 20 && System.currentTimeMillis() - lastNotificationTime > 60000) {
                sendNotification();
                lastNotificationTime = System.currentTimeMillis();
            }
        });
    }

    private static boolean isPlayerOnOriginRealms() {
        if (Constants.client == null || Constants.client.getCurrentServerEntry() == null) {
            return false;
        }
        ServerInfo serverInfo = Constants.client.getCurrentServerEntry();
        String serverAddress = serverInfo.address.toLowerCase();

        return serverAddress.contains(Constants.ORIGIN_REALMS_ADDRESS);
    }

    private static void sendNotification() {
        if (Constants.client == null || Constants.client.player == null) return;

        Constants.client.getToastManager().add(new MarketRestockToast(
                MarketRestockToast.Type.MARKET_RESTOCK,
                Text.translatable("notification.essentials.market_restock.title"),
                Text.translatable("notification.essentials.market_restock.description"),
                false));
    }
}