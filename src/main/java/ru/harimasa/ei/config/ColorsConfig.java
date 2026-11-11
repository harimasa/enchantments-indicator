package ru.harimasa.ei.config;

public final class ColorsConfig {
    public RenderMode renderMode = RenderMode.TEXTURE;
    public int enchantment = 0;
    public ColorConfig enchantmentColor = new ColorConfig(0.67f, 0.33f, 1.0f);
    public boolean gradient = false;

    public void validate() {
        if (renderMode == null) renderMode = RenderMode.TEXTURE;
        if (enchantmentColor == null) enchantmentColor = new ColorConfig(0.67f, 0.33f, 1.0f);
        enchantmentColor.clamp();
        enchantment = EnchantmentOption.clampIndex(enchantment);
    }
}