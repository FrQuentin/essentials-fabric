package fr.quentin.essentials.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.EntryListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntryListWidget.class)
public abstract class EntryListWidgetMixin {
    @Shadow
    public abstract int getRowLeft();

    @Shadow
    public abstract int getRowRight();

    public EntryListWidgetMixin() {
        super();
    }

    @Inject(
            method = "drawSelectionHighlight",
            at = @At("HEAD"),
            cancellable = true
    )
    protected void drawSelectionHighlight(DrawContext context, int y, int entryWidth, int entryHeight, int borderColor, int fillColor, CallbackInfo ci) {
        int leftBorder = getRowLeft() - 2;
        int rightBorder = getRowRight() - 2;
        context.fill(leftBorder, y - 2, rightBorder, y + entryHeight + 2, borderColor);
        context.fill(leftBorder + 1, y - 1, rightBorder - 1, y + entryHeight + 1, fillColor);
        ci.cancel();
    }
}