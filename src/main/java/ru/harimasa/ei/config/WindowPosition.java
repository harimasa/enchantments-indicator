package ru.harimasa.ei.config;

public final class WindowPosition {
    public boolean saved = false;
    public float x = 0.0f;
    public float y = 0.0f;

    public void validate() {
        if (!Float.isFinite(x) || !Float.isFinite(y)) {
            saved = false;
            x = 0.0f;
            y = 0.0f;
        }
    }
}