package com.oldustry.client;

import org.lwjgl.glfw.GLFW;

import com.oldustry.Oldustry;
import com.oldustry.network.KeysPayload;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public final class OldustryKeys {
    public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(Identifier.fromNamespaceAndPath(Oldustry.MOD_ID, "keys"));
    public static final KeyMapping MODE = new KeyMapping("key.oldustry.mode", GLFW.GLFW_KEY_M, CATEGORY);
    public static final KeyMapping ALT = new KeyMapping("key.oldustry.alt", GLFW.GLFW_KEY_LEFT_ALT, CATEGORY);
    public static final KeyMapping BOOST = new KeyMapping("key.oldustry.boost", GLFW.GLFW_KEY_LEFT_CONTROL, CATEGORY);

    private static byte last;

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.getConnection() == null) {
            return;
        }
        byte flags = 0;
        if (MODE.isDown()) {
            flags |= KeysPayload.MODE;
        }
        if (ALT.isDown()) {
            flags |= KeysPayload.ALT;
        }
        if (BOOST.isDown()) {
            flags |= KeysPayload.BOOST;
        }
        if (mc.options.keyJump.isDown()) {
            flags |= KeysPayload.JUMP;
        }
        if (mc.options.keyUp.isDown()) {
            flags |= KeysPayload.FORWARD;
        }
        if (MODE.consumeClick()) {
            flags |= KeysPayload.MODE_CLICK;
        }
        if (flags != last || (flags & KeysPayload.MODE_CLICK) != 0) {
            last = flags;
            ClientPacketDistributor.sendToServer(new KeysPayload(flags));
        }
    }

    private OldustryKeys() {}
}
