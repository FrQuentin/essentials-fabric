package fr.quentin.essentials.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import fr.quentin.essentials.option.ModKeyBinding;
import net.minecraft.client.Mouse;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.quentin.essentials.utils.ZoomManager;

@Mixin(Mouse.class)
public class MouseMixin {
    @Inject(at = @At("RETURN"), method = "onMouseScroll(JDD)V")
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        ZoomManager.getInstance().onMouseScroll(vertical);
    }

    @WrapWithCondition(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;setSelectedSlot(I)V"),
            method = "onMouseScroll(JDD)V"
    )
    private boolean setSelectedSlot(PlayerInventory inventory, int index) {
        return !ModKeyBinding.zoomKey.isPressed();
    }
}