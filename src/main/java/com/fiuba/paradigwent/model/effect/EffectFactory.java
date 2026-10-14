package com.fiuba.paradigwent.model.effect;

import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.enums.WeatherType;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class EffectFactory {
    private static final Map<String, Function<String, Effect>> CREATORS = createCreators();

    private static Map<String, Function<String, Effect>> createCreators() {
        Map<String, Function<String, Effect>> creators = new HashMap<>();
        creators.put("MODIFYPOWER", param -> new ModifyPowerEffect(parseParamToInt(param, 1)));
        creators.put("DESTROY", param -> new DestroyCreatureEffect(parseLineType(param)));
        creators.put("SCORCH", param -> new DestroyCreatureEffect(parseLineType(param)));
        creators.put("DUPLICATELINE", param -> new DuplicateLineEffect(parseLineType(param)));
        creators.put("HORN", param -> new DuplicateLineEffect(parseLineType(param)));
        creators.put("STEALCARD", param -> new StealCardEffect(parseParamToInt(param, 1)));
        creators.put("WEATHER", param -> new WeatherEffect(parseWeatherType(param)));
        creators.put("REVIVE", param -> new ReviveEffect(parseParamToInt(param, 1)));
        return Map.copyOf(creators);
    }

    /**
     * Creates an Effect instance based on the provided type and parameter. The method supports various effect types, including MODIFYPOWER, DESTROY, SCORCH, DUPLICATELINE, HORN, STEALCARD, and WEATHER. If the type is null or invalid, an IllegalArgumentException is thrown.
     * @param type
     * @param param
     * @return An instance of the corresponding Effect subclass based on the provided type and parameter.
    **/
    public static Effect createEffect(String type, String param){
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Effect type cannot be null or blank");
        }
        Function<String, Effect> creator = CREATORS.get(type.trim().toUpperCase());
        if (creator == null) {
            throw new IllegalArgumentException("Invalid effect type: " + type);
        }
        return creator.apply(param);
    }

    /**
     * Parses the provided parameter string to an integer. If the parameter is null, blank, or cannot be parsed as an integer, the method returns the specified default value.
     * @param param
     * @param defaultValue
     * @return The parsed integer value from the parameter string, or the default value if parsing fails.
     */
    private static int parseParamToInt(String param, int defaultValue) {
        if (param == null || param.isBlank()) return defaultValue;
        try {
            return Integer.parseInt(param);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Parses the provided parameter string to an AttackLineType. If the parameter is null, blank, or cannot be parsed as a valid AttackLineType, the method returns AttackLineType.NONE.
     * @param param
     * @return The parsed AttackLineType from the parameter string, or AttackLineType.NONE if parsing fails.
     */
    private static AttackLineType parseLineType(String param){
        if (param == null || param.isBlank()) return AttackLineType.NONE;
        try {
            return AttackLineType.valueOf(param.toUpperCase());
        } catch (IllegalArgumentException e) {
            return AttackLineType.NONE;
        }
    }

    /**
     * Parses the provided parameter string to a WeatherType. If the parameter is null, blank, or cannot be parsed as a valid WeatherType, the method returns WeatherType.CLEAR.
     * @param param
     * @return The parsed WeatherType from the parameter string, or WeatherType.CLEAR if parsing fails.
     */
    private static WeatherType parseWeatherType(String param){
        if (param == null || param.isBlank()) return WeatherType.CLEAR;
        try {
            return WeatherType.valueOf(param.toUpperCase());
        } catch (IllegalArgumentException e) {
            return WeatherType.CLEAR;
        }
    }
}
