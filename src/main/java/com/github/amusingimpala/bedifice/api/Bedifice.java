package com.github.amusingimpala.bedifice.api;

import com.github.amusingimpala.bedifice.api.util.Processor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Main class for constructing Minecraft Bedrock Edition Addons, Resource Packs, and Skin Packs
 * */
//Todo programmatically generated textures
public class Bedifice {

    public static Gson prettyPrint = (new GsonBuilder()).setPrettyPrinting().create();
    public static Gson noWhiteSpace = (new GsonBuilder()).create();

    /**
     * Creates a SkinPack given a name and processor
     *
     * @param proc the processor to add things to the pack
     * @param packName the name of the pack
     * @return the constructed skin pack
     * */
    public static BedificeSkinPack createSkinPack(String packName, Processor<BedificeSkinPack> proc) {
        return proc.process(new BedificeSkinPack(packName));
    }
}
