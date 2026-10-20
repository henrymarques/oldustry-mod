package com.oldustry.network;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.oldustry.Oldustry;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record KeysPayload(byte flags) implements CustomPacketPayload {
    public static final byte MODE = 1;
    public static final byte ALT = 2;
    public static final byte BOOST = 4;
    public static final byte JUMP = 8;
    public static final byte FORWARD = 16;
    public static final byte MODE_CLICK = 32;

    public static final Type<KeysPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "keys"));
    public static final StreamCodec<RegistryFriendlyByteBuf, KeysPayload> STREAM = StreamCodec.composite(
        ByteBufCodecs.BYTE,
        KeysPayload::flags,
        KeysPayload::new
    );

    private static final Map<UUID, Byte> STATE = new ConcurrentHashMap<>();

    public static byte of(Player player) {
        return STATE.getOrDefault(player.getUUID(), (byte) 0);
    }

    public static boolean mode(Player player) {
        return (of(player) & MODE) != 0;
    }

    public static boolean alt(Player player) {
        return (of(player) & ALT) != 0;
    }

    public static boolean boost(Player player) {
        return (of(player) & BOOST) != 0;
    }

    public static boolean jump(Player player) {
        return (of(player) & JUMP) != 0;
    }

    public static boolean forward(Player player) {
        return (of(player) & FORWARD) != 0;
    }

    public static boolean modeClick(Player player) {
        return (of(player) & MODE_CLICK) != 0;
    }

    public static void handle(KeysPayload payload, IPayloadContext context) {
        Player player = context.player();
        STATE.put(player.getUUID(), payload.flags);
        if ((payload.flags & MODE_CLICK) != 0) {
            com.oldustry.item.ModeSwitch.toggle(player);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
