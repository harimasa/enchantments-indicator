package ru.harimasa.ei.config;

public final class ColorsConfig {
    public RenderMode renderMode = RenderMode.TEXTURE;
    public int enchantment = 0;
    public ColorConfig enchantmentColor = new ColorConfig(0.67f, 0.33f, 1.0f);
    public boolean gradient = false;
    public ColorConfig gradientFrom = new ColorConfig(0.67f, 0.33f, 1.0f);
    public ColorConfig gradientTo = new ColorConfig(0.33f, 0.67f, 1.0f);
    public float intensity = 1.0f;

    public void validate() {
        if (renderMode == null) renderMode = RenderMode.TEXTURE;

        if (enchantmentColor == null) enchantmentColor = new ColorConfig(0.67f, 0.33f, 1.0f);
        enchantmentColor.clamp();

        if (gradientFrom == null) gradientFrom = new ColorConfig(0.67f, 0.33f, 1.0f);
        gradientFrom.clamp();

        if (gradientTo == null) gradientTo = new ColorConfig(0.33f, 0.67f, 1.0f);
        gradientTo.clamp();
        enchantment = EnchantmentOption.clampIndex(enchantment);
        intensity = Math.clamp(intensity, 0.0f, 2.0f);
    }
}