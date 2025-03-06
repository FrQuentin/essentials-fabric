package fr.quentin.essentials.utils;

import fr.quentin.essentials.config.ModConfig;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

public class ZoomManager {
    private static final ZoomManager INSTANCE = new ZoomManager();
    private double currentLevel = 3.0;
    private double defaultMouseSensitivity = 0.5;
    private boolean wasZoomActiveLastTick = false;

    public static ZoomManager getInstance() {
        return INSTANCE;
    }

    public float changeFovBasedOnZoom(float fov) {
        if (!ModConfig.isZoomEnabled()) {
            resetZoom();
            return fov;
        }
        SimpleOption<Double> mouseSensitivitySetting = Constants.client.options.getMouseSensitivity();

        if (!wasZoomActiveLastTick) {
            defaultMouseSensitivity = mouseSensitivitySetting.getValue();
        }
        mouseSensitivitySetting.setValue(defaultMouseSensitivity * (1.0 / currentLevel));
        float zoomedFov = (float) (fov / currentLevel);
        wasZoomActiveLastTick = true;

        return zoomedFov;
    }

    private void resetZoom() {
        if (wasZoomActiveLastTick) {
            SimpleOption<Double> mouseSensitivitySetting = Constants.client.options.getMouseSensitivity();
            mouseSensitivitySetting.setValue(defaultMouseSensitivity);
            currentLevel = 3.0;
            defaultMouseSensitivity = 0.5;
        }
        wasZoomActiveLastTick = false;
    }

    public void onMouseScroll(double amount) {
        if (!ModConfig.isZoomEnabled()) {
            return;
        }
        if (amount > 0) {
            currentLevel *= 1.05;
        } else if (amount < 0) {
            currentLevel *= 0.95;
        }
        currentLevel = Math.max(1, Math.min(currentLevel, 50));

        double zoomPercent = (currentLevel - 1) / 49 * 100;

        if (Constants.client != null && Constants.client.inGameHud != null) {
            Text message = Text.translatable("message.essentials.zoom.level", String.format("%.1f", zoomPercent));
            if (ModConfig.isZoomOverlayEnabled()) {
                if (Constants.client.player != null) {
                    Constants.client.player.sendMessage(message, true);
                }
            }
        }
    }
}