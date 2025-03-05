package fr.quentin.essentials.mixin;

import fr.quentin.essentials.gui.screen.ShulkerPreviewScreen;
import fr.quentin.essentials.option.ModKeyBinding;
import fr.quentin.essentials.utils.ShulkerColorManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {
    @Shadow
    @Nullable
    protected Slot focusedSlot;

    @Inject(at = @At("HEAD"), method = "keyPressed")
    private void keyPressed(int keyCode, int scanCode, int category, CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (ModKeyBinding.shulkerKey.matchesKey(keyCode, scanCode) && this.focusedSlot != null) {
            ItemStack itemStack = this.focusedSlot.getStack();
            if (ShulkerColorManager.getColorForShulker(itemStack.getItem()) != null) {
                client.setScreen(new ShulkerPreviewScreen(itemStack, client.currentScreen));
            }
        }
    }
}