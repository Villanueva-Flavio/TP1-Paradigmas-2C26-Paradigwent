package com.fiuba.paradigwent.model.effect;

import com.fiuba.paradigwent.model.enums.AttackLineType;

public class EffectFactory {
    public static Effect createEffect(String type, String param){
        if (type != null){
            return switch (type.toUpperCase()) {
                case "BUFF" -> new ModifyPowerEffect(parseParamToInt(param, 1));
                case "DESTROY", "SCORCH" -> new DestroyCreatureEffect(parseLineType(param));
                case "DUPLICATE_LINE", "HORN" -> new DuplicateLineEffect(parseLineType(param));
                case "STEAL_CARD" -> new StealCardEffect(parseParamToInt(param, 1));
                case "WEATHER" -> new WeatherEffect(parseWeatherType(param));
                default -> throw new IllegalArgumentException("Invalid effect type: " + type);
            };
        }
        throw new IllegalArgumentException("Effect type cannot be null");
    }

    private static int parseParamToInt(String param, int defaultValue) {
        if (param == null || param.isBlank()) return defaultValue;
        try {
            return Integer.parseInt(param);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static AttackLineType parseLineType(String param){
        if (param == null || param.isBlank()) return AttackLineType.NONE;
        try {
            return AttackLineType.valueOf(param.toUpperCase());
        } catch (IllegalArgumentException e) {
            return AttackLineType.NONE;
        }
    }

    private static AttackLineType parseWeatherType(String param){
        if (param == null || param.isBlank()) return AttackLineType.NONE;
        try {
            return AttackLineType.valueOf(param.toUpperCase());
        } catch (IllegalArgumentException e) {
            return AttackLineType.NONE;
        }
    }
}
