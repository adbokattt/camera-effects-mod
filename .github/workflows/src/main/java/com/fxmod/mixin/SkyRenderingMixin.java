package com.fxmod.mixin;

import com.fxmod.client.CameraEffectsClient;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldRenderer.class)
public class SkyRenderingMixin {
    @Inject(method = "getSkyColor", at = @At("HEAD"), cancellable = true)
    private void overrideSkyColor(CallbackInfoReturnable<Integer> cir) {
        if (CameraEffectsClient.flashTicks > 0) {
            CameraEffectsClient.flashTicks--;
            cir.setReturnValue(CameraEffectsClient.flashColor);
        }
    }
}
