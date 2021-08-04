package com.github.amusingimpala.bedifice.impl;

import com.google.gson.JsonObject;

/**
 * An Object that can be built into a {@link JsonObject}
 * */
public interface Builder {

    /**
     * Builds into a {@link JsonObject}
     * @return the built object
     * */
    JsonObject build();
}
