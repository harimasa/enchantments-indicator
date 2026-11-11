package ru.harimasa.ei;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import ru.harimasa.ei.config.ConfigManager;
import ru.harimasa.ei.gui.ImGuiConfigScreen;
import ru.harimasa.ei.gui.ImGuiRuntime;
import ru.harimasa.ei.render.EIGlintShaders;
import ru.harimasa.ei.render.EIItemRenderTypes;

public class EnchantmentsIndicator implements ClientModInitializer {
	private static final KeyMapping.Category EI_CATEGORY = new KeyMapping.Category(Identifier.fromNamespaceAndPath("enchantments-indicator", "main"));

	@Override
	public void onInitializeClient() {
		ConfigManager.load();
		ImGuiRuntime.setUiScale(ConfigManager.get().general.uiScale / 100.0f);
		EIGlintShaders.ensureInitialized();
		EIItemRenderTypes.ensureInitialized();

		KeyMapping openMenu = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.enchantments-indicator.open_gui", InputConstants.Type.KEYSYM, InputConstants.KEY_RSHIFT, EI_CATEGORY));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (openMenu.consumeClick()) {
				if (client.screen instanceof ImGuiConfigScreen) {
					client.setScreen(null);
				} else {
					client.setScreen(new ImGuiConfigScreen());
				}
			}
			ConfigManager.tick();
		});
		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
			ConfigManager.save();
			ImGuiRuntime.shutdown();
		});
	}
}