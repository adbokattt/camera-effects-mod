package com.fxmod;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class CameraEffectsMod implements ModInitializer {
    public static final String MOD_ID = "cameraeffects";

    public record ShakePayload(float intensity, int duration) implements CustomPayload {
        public static final CustomPayload.Id<ShakePayload> ID = new CustomPayload.Id<>(Identifier.of(MOD_ID, "shake"));
        public static final PacketCodec<PacketByteBuf, ShakePayload> CODEC = CustomPayload.codecOf(
                (payload, buf) -> {
                    buf.writeFloat(payload.intensity);
                    buf.writeInt(payload.duration);
                },
                buf -> new ShakePayload(buf.readFloat(), buf.readInt())
        );

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() { return ID; }
    }

    public record FlashPayload(int color, int duration) implements CustomPayload {
        public static final CustomPayload.Id<FlashPayload> ID = new CustomPayload.Id<>(Identifier.of(MOD_ID, "flash"));
        public static final PacketCodec<PacketByteBuf, FlashPayload> CODEC = CustomPayload.codecOf(
                (payload, buf) -> {
                    buf.writeInt(payload.color);
                    buf.writeInt(payload.duration);
                },
                buf -> new FlashPayload(buf.readInt(), buf.readInt())
        );

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() { return ID; }
    }

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(ShakePayload.ID, ShakePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(FlashPayload.ID, FlashPayload.CODEC);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("shake")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.argument("intensity", FloatArgumentType.floatArg(0.1f, 10.0f))
                .then(CommandManager.argument("duration", IntegerArgumentType.integer(1, 1000))
                .executes(context -> {
                    float intensity = FloatArgumentType.getFloat(context, "intensity");
                    int duration = IntegerArgumentType.getInteger(context, "duration");
                    for (ServerPlayerEntity player : context.getSource().getWorld().getPlayers()) {
                        ServerPlayNetworking.send(player, new ShakePayload(intensity, duration));
                    }
                    return 1;
                }))));

            dispatcher.register(CommandManager.literal("skyflash")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.argument("hexColor", StringArgumentType.string())
                .then(CommandManager.argument("duration", IntegerArgumentType.integer(1, 1000))
                .executes(context -> {
                    String hex = StringArgumentType.getString(context, "hexColor").replace("#", "");
                    int color = (int) Long.parseLong(hex, 16);
                    int duration = IntegerArgumentType.getInteger(context, "duration");
                    for (ServerPlayerEntity player : context.getSource().getWorld().getPlayers()) {
                        ServerPlayNetworking.send(player, new FlashPayload(color, duration));
                    }
                    return 1;
                }))));
        });
    }
}
