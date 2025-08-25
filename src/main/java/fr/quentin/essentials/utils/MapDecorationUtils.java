package fr.quentin.essentials.utils;

import fr.quentin.essentials.EssentialsClient;
import net.minecraft.item.map.MapDecoration;
import net.minecraft.item.map.MapDecorationType;
import net.minecraft.item.map.MapDecorationTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.MathHelper;

import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public final class MapDecorationUtils {
    private static final byte[] ROTATION_TABLE = new byte[360];

    private static final ThreadLocal<Boolean> PLAYER_CONTEXT_FLAG =
            ThreadLocal.withInitial(() -> Boolean.FALSE);

    private static final AtomicReference<WeakReference<RegistryEntry<MapDecorationType>>> PLAYER_TYPE_CACHE =
            new AtomicReference<>(null);

    static {
        for (int angle = 0; angle < 360; angle++) {
            ROTATION_TABLE[angle] = (byte) (Math.round(angle / Constants.DEGREES_PER_ROTATION) & Constants.ROTATION_MASK);
        }
    }

    private MapDecorationUtils() {

    }

    public static byte computeRotationIndex(float yaw) {
        int normalized = ((int) MathHelper.wrapDegrees(yaw) + 360) % 360;
        return ROTATION_TABLE[normalized];
    }

    public static Optional<MapDecoration> toPlayerDecoration(MapDecoration input, byte rotation) {
        if (!isAnyOffMap(input)) return Optional.empty();

        PlayerMarkerType.from(input.type())
                .ifPresent(type -> EssentialsClient.LOGGER.debug("Converting {} → player", type.getDisplayName()));

        return Optional.of(
                new MapDecoration(
                        getCachedPlayerType(),
                        input.x(),
                        input.z(),
                        rotation,
                        input.name()
                )
        );
    }

    public static boolean isAnyOffMap(MapDecoration decoration) {
        return decoration != null && needsConversion(decoration.type());
    }

    public static boolean needsConversion(RegistryEntry<MapDecorationType> type) {
        return type != null &&
                (type.equals(MapDecorationTypes.PLAYER_OFF_MAP) || type.equals(MapDecorationTypes.PLAYER_OFF_LIMITS));
    }

    public static boolean isPlayerContext() {
        if (PLAYER_CONTEXT_FLAG.get()) return true;

        for (StackTraceElement frame : Thread.currentThread().getStackTrace()) {
            String name = frame.getClassName();
            if (name.contains("MapState") || name.contains("class_22")) return true;
        }
        return false;
    }

    public static void markPlayerContext(boolean flag) {
        PLAYER_CONTEXT_FLAG.set(flag);
    }

    public static void resetPlayerContext() {
        PLAYER_CONTEXT_FLAG.remove();
    }

    public static void rememberPlayerType(MapDecoration decoration) {
        if (decoration == null) return;

        if (decoration.type().equals(MapDecorationTypes.PLAYER)) {
            WeakReference<RegistryEntry<MapDecorationType>> ref = new WeakReference<>(decoration.type());
            PLAYER_TYPE_CACHE.compareAndSet(null, ref);
            EssentialsClient.LOGGER.debug("Cached PLAYER decoration type from runtime instance");
        }
    }

    public static RegistryEntry<MapDecorationType> getCachedPlayerType() {
        WeakReference<RegistryEntry<MapDecorationType>> ref = PLAYER_TYPE_CACHE.get();
        if (ref != null) {
            RegistryEntry<MapDecorationType> value = ref.get();
            if (value != null) return value;
            PLAYER_TYPE_CACHE.compareAndSet(ref, null);
        }
        return MapDecorationTypes.PLAYER;
    }

    public enum PlayerMarkerType {
        PLAYER(MapDecorationTypes.PLAYER, "player"),
        OFF_MAP(MapDecorationTypes.PLAYER_OFF_MAP, "off-map"),
        OFF_LIMITS(MapDecorationTypes.PLAYER_OFF_LIMITS, "off-limits");

        private final RegistryEntry<MapDecorationType> entry;
        private final String display;

        PlayerMarkerType(RegistryEntry<MapDecorationType> entry, String display) {
            this.entry = entry;
            this.display = display;
        }

        public String getDisplayName() {
            return display;
        }

        public static Optional<PlayerMarkerType> from(RegistryEntry<MapDecorationType> type) {
            for (PlayerMarkerType t : values()) {
                if (t.entry.equals(type)) return Optional.of(t);
            }
            return Optional.empty();
        }
    }
}