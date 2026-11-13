package ru.harimasa.ei.config;

import ru.harimasa.ei.config.enums.LanguageOption;

public final class GeneralConfig {
    public static final float MIN_UI_SCALE = 50.0f;
    public static final float MAX_UI_SCALE = 100.0f;

    public boolean modEnabled = true;
    public boolean liquidGlass = true;
    public float uiScale = 100.0f;
    public String language = "";
    public ColorConfig mainColor = new ColorConfig(0.75f, 0.33f, 1.0f);
    public WindowPosition window = new WindowPosition();

    public void validate() {
        uiScale = Math.clamp(uiScale, MIN_UI_SCALE, MAX_UI_SCALE);

        if (language == null || (!language.isEmpty() && LanguageOption.byCode(language) == null)) {
            language = "";
        }

        if (mainColor == null) mainColor = new ColorConfig(0.75f, 0.33f, 1.0f);
        mainColor.clamp();

        if (window == null) window = new WindowPosition();
        window.validate();
    }
}