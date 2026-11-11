package ru.harimasa.ei.config.enums;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import ru.harimasa.ei.lang.Lang;

public enum EnchantmentOption {
    PROTECTION("ei.enchantment.protection", Enchantments.PROTECTION),
    FIRE_PROTECTION("ei.enchantment.fire_protection", Enchantments.FIRE_PROTECTION),
    BLAST_PROTECTION("ei.enchantment.blast_protection", Enchantments.BLAST_PROTECTION),
    PROJECTILE_PROTECTION("ei.enchantment.projectile_protection", Enchantments.PROJECTILE_PROTECTION),
    UNBREAKING("ei.enchantment.unbreaking", Enchantments.UNBREAKING),
    MENDING("ei.enchantment.mending", Enchantments.MENDING),
    THORNS("ei.enchantment.thorns", Enchantments.THORNS),
    BINDING_CURSE("ei.enchantment.binding_curse", Enchantments.BINDING_CURSE),
    VANISHING_CURSE("ei.enchantment.vanishing_curse", Enchantments.VANISHING_CURSE),
    RESPIRATION("ei.enchantment.respiration", Enchantments.RESPIRATION),
    AQUA_AFFINITY("ei.enchantment.aqua_affinity", Enchantments.AQUA_AFFINITY),
    SWIFT_SNEAK("ei.enchantment.swift_sneak", Enchantments.SWIFT_SNEAK),
    FEATHER_FALLING("ei.enchantment.feather_falling", Enchantments.FEATHER_FALLING),
    DEPTH_STRIDER("ei.enchantment.depth_strider", Enchantments.DEPTH_STRIDER),
    FROST_WALKER("ei.enchantment.frost_walker", Enchantments.FROST_WALKER),
    SOUL_SPEED("ei.enchantment.soul_speed", Enchantments.SOUL_SPEED);

    private final String translationKey;
    private final ResourceKey<Enchantment> key;

    EnchantmentOption(String translationKey, ResourceKey<Enchantment> key) {
        this.translationKey = translationKey;
        this.key = key;
    }

    public String label() {
        return Lang.get(translationKey);
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
