package fr.quentin.essentials.mixin;

import fr.quentin.essentials.utils.MapDecorationUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.map.MapDecoration;
import net.minecraft.item.map.MapState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(MapState.class)
public class MapStateMixin {
    @Inject(
            method = "isInBounds",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void keepPlayerAlwaysVisible(float dx, float dz, CallbackInfoReturnable<Boolean> cir) {
        if (MapDecorationUtils.isPlayerContext()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(
            method = "getDecorations",
            at = @At("RETURN"),
            cancellable = true
    )
    private void rewritePlayerDecorations(CallbackInfoReturnable<Iterable<MapDecoration>> cir) {
        Iterable<MapDecoration> original = cir.getReturnValue();
        MinecraftClient client = MinecraftClient.getInstance();

        if (client == null || client.player == null || original == null) return;

        List<MapDecoration> adjusted = null;
        byte rotationIndex = 0;
        boolean hasRotation = false;

        MapDecorationUtils.markPlayerContext(true);
        try {
            for (MapDecoration deco : original) {
                MapDecorationUtils.rememberPlayerType(deco);

                if (MapDecorationUtils.isAnyOffMap(deco)) {
                    if (adjusted == null) {
                        adjusted = new ArrayList<>();
                        for (MapDecoration prev : original) {
                            if (prev == deco) break;
                            adjusted.add(prev);
                        }
                    }

                    if (!hasRotation) {
                        rotationIndex = MapDecorationUtils.computeRotationIndex(client.player.getYaw());
                        hasRotation = true;
                    }

                    MapDecorationUtils.toPlayerDecoration(deco, rotationIndex)
                            .ifPresent(adjusted::add);

                } else if (adjusted != null) {
                    adjusted.add(deco);
                }
            }

            if (adjusted != null) {
                cir.setReturnValue(adjusted);
            }
        } finally {
            MapDecorationUtils.resetPlayerContext();
        }
    }
}