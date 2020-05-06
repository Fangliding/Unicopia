package com.minelittlepony.unicopia.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.lwjgl.glfw.GLFW;

import com.minelittlepony.unicopia.ability.AbilitySlot;
import com.minelittlepony.unicopia.entity.player.Pony;

import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

class KeyBindingsHandler {
    private final String KEY_CATEGORY = "unicopia.category.name";

    private final Map<KeyBinding, AbilitySlot> keys = new HashMap<>();

    private final Set<KeyBinding> pressed = new HashSet<>();

    public KeyBindingsHandler() {
        KeyBindingRegistry.INSTANCE.addCategory(KEY_CATEGORY);

        addKeybind(GLFW.GLFW_KEY_O, AbilitySlot.PRIMARY);
        addKeybind(GLFW.GLFW_KEY_P, AbilitySlot.SECONDARY);
        addKeybind(GLFW.GLFW_KEY_L, AbilitySlot.TERTIARY);
    }

    public void addKeybind(int code, AbilitySlot slot) {
        KeyBindingRegistry.INSTANCE.addCategory(KEY_CATEGORY);

        FabricKeyBinding b = FabricKeyBinding.Builder.create(new Identifier("unicopia", slot.name().toLowerCase()), InputUtil.Type.KEYSYM, code, KEY_CATEGORY).build();
        KeyBindingRegistry.INSTANCE.register(b);

        keys.put(b, slot);
    }

    public void tick(MinecraftClient client) {
        if (client.currentScreen != null
            || client.player == null) {
            return;
        }
        Pony iplayer = Pony.of(client.player);

        for (KeyBinding i : keys.keySet()) {
            AbilitySlot slot = keys.get(i);
            if (slot == AbilitySlot.PRIMARY && client.options.keySneak.isPressed()) {
                slot = AbilitySlot.PASSIVE;
            }

            if (i.isPressed()) {
                if (pressed.add(i)) {
                    System.out.println("Key down " + slot);
                    iplayer.getAbilities().activate(slot);
                }
            } else if (pressed.remove(i)) {
                System.out.println("Key up " + slot);
                iplayer.getAbilities().cancelAbility(slot);
            }
        }
    }
}
