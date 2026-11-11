package ru.harimasa.ei.config;

public final class GeneralConfig {

    public static final float MIN_UI_SCALE = 50.0f;
    public static final float MAX_UI_SCALE = 100.0f;

    public boolean modEnabled = true;
    public float uiScale = 100.0f;
    public ColorConfig mainColor = new ColorConfig(0.75f, 0.33f, 1.0f);
    public WindowPosition window = new WindowPosition();

    public void validate() {
        uiScale = Math.clamp(uiScale, MIN_UI_SCALE, MAX_UI_SCALE);

        if (mainColor == null) mainColor = new ColorConfig(0.75f, 0.33f, 1.0f);
        mainColor.clamp();

        if (window == null) window = new WindowPosition();
        window.validate();
    }
}