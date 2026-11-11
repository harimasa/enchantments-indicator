package ru.harimasa.ei.config;

public final class ColorConfig {
    public float r;
    public float g;
    public float b;
    public float a;

    public ColorConfig(float r, float g, float b) {
        this(r, g, b, 1.0f);
    }

    public ColorConfig(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public float[] rgb() {
        return new float[]{r, g, b};
    }

    public float[] rgba() {
        return new float[]{r, g, b, a};
    }

    public void setRgb(float[] value) {
        r = value[0];
        g = value[1];
        b = value[2];
    }

    public void setRgba(float[] value) {
        setRgb(value);
        a = value[3];
    }

    public void clamp() {
        r = Math.clamp(r, 0.0f, 1.0f);
        g = Math.clamp(g, 0.0f, 1.0f);
        b = Math.clamp(b, 0.0f, 1.0f);
        a = Math.clamp(a, 0.0f, 1.0f);
    }
}
