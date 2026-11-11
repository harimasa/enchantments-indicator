package ru.harimasa.ei.config;

public final class EIConfig {

    public GeneralConfig general = new GeneralConfig();
    public ColorsConfig colors = new ColorsConfig();
    public InventoryConfig inventory = new InventoryConfig();
    public transient int page = 0;

    public void validate() {
        if (general == null) general = new GeneralConfig();
        if (colors == null) colors = new ColorsConfig();
        if (inventory == null) inventory = new InventoryConfig();

        general.validate();
        colors.validate();
        inventory.validate();
    }
}