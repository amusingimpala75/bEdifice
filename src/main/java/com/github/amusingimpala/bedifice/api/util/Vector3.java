package com.github.amusingimpala.bedifice.api.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

/**
 * Collection of 3 ints, used for Semantic Versioning
 * */
public record Vector3(int x, int y, int z) {

    /**
     * Writes the Vector3 to a JsonArray
     *
     * @return the array containing the 3 ints
     * */
    public JsonElement toJson() {
        JsonArray arr = new JsonArray();
        arr.add(x);
        arr.add(y);
        arr.add(z);
        return arr;
    }
}
