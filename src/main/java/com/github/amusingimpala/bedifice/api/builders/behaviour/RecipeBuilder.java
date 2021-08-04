package com.github.amusingimpala.bedifice.api.builders.behaviour;

import com.github.amusingimpala.bedifice.api.util.Identifier;
import com.github.amusingimpala.bedifice.impl.Builder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Locale;
import java.util.MissingFormatArgumentException;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class RecipeBuilder implements Builder {

    private String formatVersion = "1.12";
    private Optional<JsonObject> recipeData = Optional.empty();

    public RecipeBuilder formatVersion(String version) {
        this.formatVersion = version;
        return this;
    }

    @Override
    public JsonObject build() {
        JsonObject root = new JsonObject();

        root.add("format_version", new JsonPrimitive(formatVersion));
        JsonObject recipeData = this.recipeData.orElseThrow(() -> new MissingFormatArgumentException("Missing recipe data!"));
        root.add("minecraft:"+recipeData.remove("type").getAsString(), recipeData);

        return root;
    }

    public static class RecipeDataBuilder implements Builder {

        private final RecipeType type;
        private final Identifier id;
        private JsonArray tags = new JsonArray();
        private Optional<Integer> priority = Optional.empty();

        public RecipeDataBuilder(RecipeType type, Identifier id) {
            this.type = type;
            this.id = id;
        }

        public RecipeDataBuilder tags(String... tags) {
            for (String tag : tags) this.tags.add(tag);
            return this;
        }

        public RecipeDataBuilder priority(int priority) {
            this.priority = Optional.of(priority);
            return this;
        }

        @Override
        public JsonObject build() {
            JsonObject root = new JsonObject();

            root.add("type", new JsonPrimitive(type.name().toLowerCase(Locale.ROOT)));

            JsonObject desc = new JsonObject();
            desc.add("identifier", new JsonPrimitive(id.toString()));
            root.add("description", desc);

            priority.ifPresent(p -> root.add("priority", new JsonPrimitive(p)));

            return root;
        }
    }

    //Todo: brewing
    public enum RecipeType {
        RECIPE_FURNACE,
        RECIPE_SHAPED,
        RECIPE_SHAPELESS
    }
}
