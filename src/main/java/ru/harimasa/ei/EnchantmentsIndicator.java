package ru.harimasa.ei;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import ru.harimasa.ei.gui.ImGuiConfigScreen;
import ru.harimasa.ei.gui.ImGuiRuntime;

public class EnchantmentsIndicator implements ClientModInitializer {
	private static KeyMapping openMenu;

	@Override
	public void onInitializeClient() {
		openMenu = KeyBindingHelper.registerKeyBinding(new KeyMapping("ru.harimasa.ei.open_gui", InputConstants.Type.KEYSYM, InputConstants.KEY_RSHIFT, KeyMapping.Category.MISC));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (openMenu.consumeClick()) {
				if (client.screen instanceof ImGuiConfigScreen) {
					client.setScreen(null);
				} else {
					client.setScreen(new ImGuiConfigScreen());
				}
			}
		});

		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> ImGuiRuntime.shutdown());
	}
}