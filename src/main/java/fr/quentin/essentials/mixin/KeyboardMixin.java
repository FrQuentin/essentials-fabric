package fr.quentin.essentials.mixin;

import fr.quentin.essentials.input.CharInputCallback;
import fr.quentin.essentials.input.KeyPressCallback;
import net.minecraft.client.Keyboard;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(
            method = "onKey",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/client/Keyboard;debugCrashStartTime:J",
                    ordinal = 0),
            cancellable = true
    )
    private void injectOnKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        ActionResult result = KeyPressCallback.EVENT.invoker().onKeyPress(window, key, scancode, action, modifiers);
        if (result == ActionResult.FAIL)
            ci.cancel();
    }

    @Inject(
            method = "onChar",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;",
                    ordinal = 0),
            cancellable = true
    )
    private void injectOnChar(long window, int codepoint, int modifiers, CallbackInfo ci) {
        ActionResult result = CharInputCallback.EVENT.invoker().onCharInput(window, codepoint, modifiers);
        if (result == ActionResult.FAIL)
            ci.cancel();
    }
}