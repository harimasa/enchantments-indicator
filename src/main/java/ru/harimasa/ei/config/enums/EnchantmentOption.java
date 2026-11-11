package ru.harimasa.ei.config.enums;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public enum EnchantmentOption {
    PROTECTION("Protection", Enchantments.PROTECTION),
    FIRE_PROTECTION("Fire Protection", Enchantments.FIRE_PROTECTION),
    BLAST_PROTECTION("Blast Protection", Enchantments.BLAST_PROTECTION),
    PROJECTILE_PROTECTION("Projectile Protection", Enchantments.PROJECTILE_PROTECTION),
    UNBREAKING("Unbreaking", Enchantments.UNBREAKING),
    MENDING("Mending", Enchantments.MENDING),
    THORNS("Thorns", Enchantments.THORNS),
    BINDING_CURSE("Curse of Binding", Enchantments.BINDING_CURSE),
    VANISHING_CURSE("Curse of Vanishing", Enchantments.VANISHING_CURSE),
    RESPIRATION("Respiration", Enchantments.RESPIRATION),
    AQUA_AFFINITY("Aqua Affinity", Enchantments.AQUA_AFFINITY),
    SWIFT_SNEAK("Swift Sneak", Enchantments.SWIFT_SNEAK),
    FEATHER_FALLING("Feather Falling", Enchantments.FEATHER_FALLING),
    DEPTH_STRIDER("Depth Strider", Enchantments.DEPTH_STRIDER),
    FROST_WALKER("Frost Walker", Enchantments.FROST_WALKER),
    SOUL_SPEED("Soul Speed", Enchantments.SOUL_SPEED);

    private final String label;
    private final ResourceKey<Enchantment> key;

    EnchantmentOption(String label, ResourceKey<Enchantment> key) {
        this.label = label;
        this.key = key;
    }

    public String label() {
        return label;
    }

    public ResourceKey<Enchantment> key() {
        return key;
    }

    public static int clampIndex(int index) {
        return Math.max(0, Math.min(index, values().length - 1));
    }

    public static EnchantmentOption byIndex(int index) {
        return values()[clampIndex(index)];
    }
}