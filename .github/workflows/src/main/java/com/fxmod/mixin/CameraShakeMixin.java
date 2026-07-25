package com.fxmod.mixin;

import com.fxmod.client.CameraEffectsClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class CameraShakeMixin {
    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;tiltViewWhenHurt(Lnet/minecraft/client/util/math/MatrixStack;F)V"))
    private void applyCameraShake(CallbackInfo ci) {
        if (CameraEffectsClient.shakeTicks > 0) {
            CameraEffectsClient.shakeTicks--;
            float offset = (float) (Math.random() - 0.5) * CameraEffectsClient.shakeIntensity;
            // Лёгкое смещение камеры для эффекта тряски
        }
    }
}
