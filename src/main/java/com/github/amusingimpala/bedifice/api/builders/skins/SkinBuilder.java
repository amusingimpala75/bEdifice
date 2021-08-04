package com.github.amusingimpala.bedifice.api.builders.skins;

import com.github.amusingimpala.bedifice.impl.Builder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * For building the skins.json file
 * */
public class SkinBuilder implements Builder {

    private final JsonPrimitive packName;
    private final JsonArray skins = new JsonArray();

    /**
     * Creates a SkinBuilder with a given pack name
     *
     * @param packName Name of the pack
     * */
    public SkinBuilder(String packName) {
        this.packName = new JsonPrimitive(packName);
    }

    /**
     * Adds a skin to the 'skins' array, with a default image of 'name'.png and type of 'free'.
     * The skin type is only needed for marketplace partners
     *
     * @param name Name of the skin and its image
     * @return the current SkinBuilder (this)
     * */
    public SkinBuilder skin(String name) {
        return this.skin(name, name + ".png");
    }

    /**
     * Adds a skin, with a type of 'free'
     * The skin type is only needed for marketplace partners
     *
     * @param name name of the skin for locale usage
     * @param imageName name of the image, ending with '.png'
     * @return the current SkinBuilder (this)
     * */
    public SkinBuilder skin(String name, String imageName) {
        return this.skin(name, imageName, "free");
    }

    /**
     * Adds a skin
     * The skin type is only needed for marketplace partners
     *
     * @param name name of the skin for locale usage
     * @param imageName name of the image, ending with '.png'
     * @param type type of the skin
     * @return the current SkinBuilder (this)
     * */
    public SkinBuilder skin(String name, String imageName, String type) {
        JsonObject skinRoot = new JsonObject();

        skinRoot.add("localization_name", new JsonPrimitive(name));
        skinRoot.add("geometry", new JsonPrimitive("geometry.humanoid.custom"));
        skinRoot.add("texture", new JsonPrimitive(imageName));
        skinRoot.add("type", new JsonPrimitive(type));

        this.skins.add(skinRoot);
        return this;
    }

    /**
     * Builds this object into a JsonObject ready for saving.
     *
     * @return the built SkinBuilder/skins.json JsonObject
     * */
    @Override
    public JsonObject build() {
        JsonObject root = new JsonObject();

        root.add("geometry", new JsonPrimitive("skinpacks/skins.json"));
        root.add("serialize_name", packName);
        root.add("localization_name", packName);

        return root;
    }
}
