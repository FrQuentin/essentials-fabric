package fr.quentin.essentials.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.quentin.essentials.utils.ZoomManager;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(
            method = "getFov(Lnet/minecraft/client/render/Camera;FZ)F",
            at = @At(value = "RETURN", ordinal = 1),
            cancellable = true
    )
    private void getFov(Camera camera, float tickDelta, boolean firstPerson, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(ZoomManager.getInstance().changeFovBasedOnZoom(cir.getReturnValueF()));
    }
}