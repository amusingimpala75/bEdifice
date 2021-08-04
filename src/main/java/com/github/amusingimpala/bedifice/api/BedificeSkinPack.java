package com.github.amusingimpala.bedifice.api;

import com.github.amusingimpala.bedifice.api.builders.LangBuilder;
import com.github.amusingimpala.bedifice.api.builders.skins.SkinBuilder;
import com.github.amusingimpala.bedifice.api.util.Processor;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Class for bEdifice created skin packs
 * */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class BedificeSkinPack extends BedificePack<BedificeSkinPack> {

    private Optional<JsonObject> skinsJson = Optional.empty();
    private Optional<List<LangBuilder>> langs = Optional.empty();

    /**
     * Constructs a SkinPack with the given name
     *
     * @param name the Name of the pack
     * */
    public BedificeSkinPack(String name) {
        super(name);
    }

    /**
     * Creates a skins.json, with the details specified in the processor
     *
     * @param skinProc the Processor used in the creation of the skins.json
     * @return the current BedificeSkinPack (this)
     * */
    public BedificeSkinPack skins(Processor<SkinBuilder> skinProc) {
        this.skinsJson = Optional.of(skinProc.process(new SkinBuilder(this.getPackName())).build());
        return this;
    }

    /**
     * Adds a locale with translations
     *
     * @param locale the locale to add
     * @param langProc the Processor to add translations
     * @return the current BedificeSkinPack (this)
     */
    public BedificeSkinPack translations(String locale, Processor<LangBuilder> langProc) {
        if (langs.isEmpty()) langs = Optional.of(new ArrayList<>());
        langs.get().add(langProc.process(new LangBuilder(locale)));
        return this;
    }

    /**
     * Saves the skins.json and any langs
     *
     * @param gson the correctly configured Gson for serialization
     * @param outFolder the folder into which to output files
     * */
    @Override
    protected void onSave(Path outFolder, Gson gson) {
        boolean skinsJsonAlready = Files.exists(Paths.get(outFolder.toString(), "skins.json"));

        //langs
        if (langs.isPresent()) {
            try {
                Files.createDirectory(Paths.get(outFolder.toString(), "texts"));
            } catch (IOException e) {
                System.out.println("Error creating lang 'texts' folder!");
                e.printStackTrace();
            }
            for (LangBuilder lang : langs.get()) {
                try {
                    Files.writeString(
                            Paths.get(outFolder.toString(), "texts", lang.getLocale()+".lang"),
                            lang.assemble()
                    );
                } catch (IOException e) {
                    System.out.println("Error saving "+lang.getLocale()+" lang!");
                    e.printStackTrace();
                }
            }
        }

        //skins.json
        if (!skinsJsonAlready) {
            String skinsJson = gson.toJson(this.skinsJson.orElseThrow(
                    () -> new MissingFormatArgumentException("Missing skins.json!")
            ));
            try {
                Files.writeString(Paths.get(outFolder.toString(), "skins.json"), skinsJson);
            } catch (IOException e) {
                System.out.println("Error saving skins.json!");
            }
        }
    }
}
