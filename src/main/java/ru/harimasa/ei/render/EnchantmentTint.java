package ru.harimasa.ei.render;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import ru.harimasa.ei.config.ColorConfig;
import ru.harimasa.ei.config.EIConfig;
import ru.harimasa.ei.config.enums.EnchantmentOption;

public final class EnchantmentTint {
    private EnchantmentTint() {}

    public static boolean applies(EIConfig config, ItemStack stack, EquipmentClientInfo.LayerType layerType, Object state) {
        if (!config.general.modEnabled) return false;
        if (!(state instanceof HumanoidRenderState)) return false;
        if (layerType != EquipmentClientInfo.LayerType.HUMANOID && layerType != EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS) {
            return false;
        }
        return hasEnchantment(stack, config.colors.enchantment);
    }

    public static boolean hasEnchantment(ItemStack stack, int optionIndex) {
        var key = EnchantmentOption.byIndex(optionIndex).key();
        ItemEnchantments enchantments = stack.get(DataComponents.ENCHANTMENTS);
        ItemEnchantments stored = stack.get(DataComponents.STORED_ENCHANTMENTS);
        return hasEnchantment(enchantments, key) || hasEnchantment(stored, key);
    }

    private static boolean hasEnchantment(ItemEnchantments enchantments, ResourceKey<Enchantment> key) {
        if (enchantments == null || enchantments.isEmpty()) return false;
        for (Holder<Enchantment> enchantment : enchantments.keySet()) {
            if (enchantment.unwrapKey().filter(key::equals).isPresent()) {
                return enchantments.getLevel(enchantment) > 0;
            }
        }
        return false;
    }

    public static int color(ColorConfig color, boolean useAlpha) {
        int alpha = useAlpha ? channel(color.a) : 255;
        return (alpha << 24) | (channel(color.r) << 16) | (channel(color.g) << 8) | channel(color.b);
    }

    private static int channel(float value) {
        return Math.max(0, Math.min(255, Math.round(value * 255.0f)));
    }
}