package ru.harimasa.ei.config;

import ru.harimasa.ei.config.enums.RenderMode;

public final class InventoryConfig {
    public boolean enabled = true;
    public RenderMode renderMode = RenderMode.TEXTURE;

    public void validate() {
        if (renderMode == null) renderMode = RenderMode.TEXTURE;
    }
}