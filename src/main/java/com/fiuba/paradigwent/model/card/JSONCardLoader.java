package com.fiuba.paradigwent.model.card;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import com.fiuba.paradigwent.model.effect.Effect;
import com.fiuba.paradigwent.model.effect.EffectFactory;

public class JSONCardLoader {
    private static final String CARD_JSON_PATH = "/json/cards.json";
    /**
     * Executes the operation represented by loadCards.
     * @return the value produced by the method
     */
    public static List<Card> loadCards() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(Card.class, new CardDeserializer());

        gsonBuilder.registerTypeAdapter(Effect.class, (JsonDeserializer<Effect>) (json, typeOfT, context) -> {
            JsonObject obj = json.getAsJsonObject();
            String type = obj.has("type") ? obj.get("type").getAsString() : null;
            String param = obj.has("param") ? obj.get("param").getAsString() : null;
            return EffectFactory.createEffect(type, param);
        });

        Gson gson = gsonBuilder.create();

        try (InputStream inputStream = JSONCardLoader.class.getResourceAsStream(CARD_JSON_PATH)) {
            if (inputStream == null) {
                throw new IllegalStateException("Resource not found: " + CARD_JSON_PATH);
            }
            try (Reader reader = new InputStreamReader(inputStream)) {
                Card[] cards = gson.fromJson(reader, Card[].class);
                return cards != null ? new ArrayList<>(Arrays.asList(cards)) : new ArrayList<>();
            }
        } catch (java.io.IOException | JsonParseException e) {
            throw new IllegalStateException("Could not load the cards from "
                + CARD_JSON_PATH, e);
        }
    }

    private static class CardDeserializer implements JsonDeserializer<Card> {
        /**
         * Executes the operation represented by deserialize.
         * @param json the value used by the method
         * @param typeOfT the value used by the method
         * @param context the value used by the method
         * @return the value produced by the method
         */
        @Override
        public Card deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String type = jsonObject.has("type") ? jsonObject.get("type").getAsString() : "";

            switch (type.toUpperCase()) {
                case "MINION":
                    return context.deserialize(jsonObject, Minion.class);
                case "SPELL":
                    return context.deserialize(jsonObject, Spell.class);
                case "WEATHER":
                    return context.deserialize(jsonObject, Weather.class);
                default:
                    throw new JsonParseException("Unknown card type: " + type);
            }
        }
    }
}
