package me.basil.otherworld.character;

public class Scaler {

    private final double base;
    private final double perLevel;
    private final ScalerType scalerType;

    public Scaler(double base, double perLevel, ScalerType scalerType) {
        this.base = base;
        this.perLevel = perLevel;
        this.scalerType = scalerType;
    }

    public double calculate(int level) {
        return switch (scalerType) {
            case LINEAR -> base + perLevel * level;
            case EXPONENTIAL -> base * Math.pow(perLevel, level);
            case LOGARITHMIC -> base + perLevel * Math.log(level + 1);
        };
    }

    public double getBase() {
        return base;
    }

    public double getPerLevel() {
        return perLevel;
    }

    public ScalerType getScalerType() {
        return scalerType;
    }

    public enum ScalerType {
        LINEAR,
        EXPONENTIAL,
        LOGARITHMIC
    }
}

