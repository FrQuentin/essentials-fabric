package fr.quentin.essentials.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class CameraMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @ModifyConstant(
            method = "getBasicProjectionMatrix",
            constant = @Constant(floatValue = 0.05F)
    )
    private float adjustProjection(float original) {
        LivingEntity focused = (LivingEntity) ((GameRenderer)(Object)this).getCamera().getFocusedEntity();
        float scale = (float) focused.getAttributeValue(EntityAttributes.SCALE);

        float scaled = original * scale;
        return MathHelper.clamp(scaled, 0.005f, 0.05f);
    }

    @Inject(
            method = "bobView",
            at = @At("HEAD"),
            cancellable = true
    )
    private void customBobbing(MatrixStack stack, float delta, CallbackInfo ci) {
        if (this.client.getCameraEntity() instanceof AbstractClientPlayerEntity player) {
            float scale = (float) player.getAttributeValue(EntityAttributes.SCALE) * 2f;
            float multiplier = Math.min(scale, 1f);

            float distanceDiff = player.distanceMoved - player.lastDistanceMoved;
            float movementPhase = -(player.distanceMoved + distanceDiff * delta);
            float stride = MathHelper.lerp(delta, player.lastStrideDistance, player.strideDistance);

            stack.translate(
                    MathHelper.sin(movementPhase * (float) Math.PI) * multiplier * stride * 0.5F,
                    -Math.abs(MathHelper.cos(movementPhase * (float) Math.PI) * multiplier * stride),
                    0.0F
            );

            stack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(
                    MathHelper.sin(movementPhase * (float) Math.PI) * stride * 3.0F
            ));

            stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(
                    Math.abs(MathHelper.cos(movementPhase * (float) Math.PI - 0.2F) * stride) * 5.0F
            ));

            ci.cancel();
        }
    }
}