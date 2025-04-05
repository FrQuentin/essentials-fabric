package fr.quentin.essentials.mixin;

import fr.quentin.essentials.input.InputPollCallback;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(
            method = "tick",
            at = @At(value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/platform/GLX;pollEvents()V",
                    shift = At.Shift.AFTER,
                    ordinal = 0
            )
    )
    private void injectGlfwPoll(CallbackInfo ci) {
        InputPollCallback.EVENT.invoker().onPoll();
    }
}