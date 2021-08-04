package com.github.amusingimpala.bedifice.api;

import com.google.gson.Gson;

import java.nio.file.Path;

public class BedificeBehvaiourPack extends BedificePack<BedificeBehvaiourPack> {

    /**
     * Constructs a BedificeBehaviourPack
     *
     * @param name Name of the pack
     */
    public BedificeBehvaiourPack(String name) {
        super(name);
    }

    @Override
    protected void onSave(Path outputFolder, Gson gson) {

    }
}
