package ru.harimasa.ei.config;

public final class InventoryConfig {
    public boolean enabled = true;
    public RenderMode renderMode = RenderMode.TEXTURE;

    public void validate() {
        if (renderMode == null) renderMode = RenderMode.TEXTURE;
    }
}