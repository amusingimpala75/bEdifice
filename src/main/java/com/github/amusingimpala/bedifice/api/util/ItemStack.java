package com.github.amusingimpala.bedifice.api.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public record ItemStack(Identifier id, int data, int count) {

    public ItemStack(Identifier id, int count) {
        this(id, 0, count);
    }

    public ItemStack(Identifier id) {
        this(id, 1);
    }

    public JsonObject toJson() {
        JsonObject root = new JsonObject();

        root.add("item", new JsonPrimitive(id.toString()));
        root.add("count", new JsonPrimitive(count));
        root.add("data", new JsonPrimitive(data));

        return root;
    }
}
