package com.fxmod.client;

import com.fxmod.CameraEffectsMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class CameraEffectsClient implements ClientModInitializer {
    public static float shakeIntensity = 0.0f;
    public static int shakeTicks = 0;

    public static int flashColor = 0xFFFFFF;
    public static int flashTicks = 0;
    public static int maxFlashTicks = 1;

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(CameraEffectsMod.ShakePayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                shakeIntensity = payload.intensity();
                shakeTicks = payload.duration();
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(CameraEffectsMod.FlashPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                flashColor = payload.color();
                flashTicks = payload.duration();
                maxFlashTicks = payload.duration();
            });
        });
    }
}
