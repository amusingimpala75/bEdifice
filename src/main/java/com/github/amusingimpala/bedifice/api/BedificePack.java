package com.github.amusingimpala.bedifice.api;

import com.github.amusingimpala.bedifice.api.builders.ManifestBuilder;
import com.github.amusingimpala.bedifice.api.util.Processor;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingFormatArgumentException;
import java.util.Optional;

/**
 * A pack, whether it be a SkinPack, ResourcePack, or Behaviour pack extends from this
 * @param <T> This generic should be the class that extends this,
 *           useful for chaining calls off the manifest and including resource folders
 * */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public abstract class BedificePack<T> {

    private final String name;
    private final List<String> includes = new ArrayList<>();
    private Optional<JsonObject> manifest = Optional.empty();

    /**
     * Constructs a pack
     *
     * @param name Name of the pack
     * */
    public BedificePack(String name) {
        this.name = name;
    }

    /**
     * Returns the pack's name
     *
     * @return the name of the pack
     * */
    public String getPackName() {
        return this.name;
    }

    /**
     * Adds / sets the manifest for the pack
     *
     * @param manProc Processor to add things to the manifest
     * @return the current pack (this)
     * */
    public T manifest(Processor<ManifestBuilder> manProc) {
        this.manifest = Optional.of(manProc.process(new ManifestBuilder()).build());
        return (T) this;
    }

    /**
     * Includes a folder's contents from the src/resources/[name] folder into the pack
     *
     * @param resourcePath path from src/resources/[name] to include the contents
     * @return the current Pack (this)
     * */
    public T include(String resourcePath) {
        this.includes.add(resourcePath);
        return (T) this;
    }

    /**
     * Saves the pack to out/packs/[pack_name]/[pack_name], pretty printed
     * */
    public void save() {
        this.save(true);
    }

    /**
     * Saves the pack to out/packs/[pack_name]/[pack_name]
     *
     * @param prettyPrint whether to pretty print or not
     * */
    public void save(boolean prettyPrint) {
        this.save(null, prettyPrint);
    }

    /**
     * Saves the pack to out/packs/[pack_name]/folderName
     *
     * @param folderName name of the folder to which to save
     * @param prettyPrint whether or not to pretty print
     * */
    public void save(String folderName, boolean prettyPrint) {
        //Out folder:
        //out/pack_name
        Path outFolder = Paths.get("out", "packs", getPackName(), folderName == null ? getPackName() : folderName);

        try {
            FileUtils.deleteDirectory(outFolder.toFile());
        } catch (IOException e) {
            System.out.println("Error removing old output folder!");
            e.printStackTrace();
        }

        Gson gson = (prettyPrint ? Bedifice.prettyPrint : Bedifice.noWhiteSpace);

        try {
            Path superDir = Paths.get(outFolder.toString(), "..");
            Path superSuperDir = Paths.get(superDir.toString(), "..");
            if (!Files.exists(superSuperDir)) Files.createDirectory(superSuperDir);
            if (!Files.exists(superDir)) Files.createDirectory(superDir);
            if (includes.size() == 0) Files.createDirectory(outFolder);
            //Files.createDirectory(Paths.get("out", "packs", getPackName()));
        } catch (IOException e) {
            System.out.println("Error creating output directory");
            e.printStackTrace();
        }

        //any other included files
        for (String include : includes) {
            try {
                FileUtils.copyDirectory(Paths.get("src", "resources", include).toFile(), outFolder.toFile());
            } catch (IOException e) {
                System.out.println("Failed to copy resources from " +
                        "src/resources/"+include+" " +
                        "to "+outFolder.toString());
                e.printStackTrace();
            }
        }

        //detect manifest and skins.json
        boolean manifestAlready = Files.exists(Paths.get(outFolder.toString(), "manifest.json"));

        //Manifest
        if (!manifestAlready) {
            String manifest = gson.toJson(this.manifest.orElseThrow(
                    () -> new MissingFormatArgumentException("Missing skin pack manifest!")
            ));

            try {
                Files.writeString(Paths.get(outFolder.toString(), "manifest.json"), manifest);
            } catch (IOException e) {
                System.out.println("Error saving pack manifest for the " + getPackName() + " SkinPack!");
                e.printStackTrace();
            }
        }

        this.onSave(outFolder, gson);

        System.out.println("Saved skin pack "+getPackName()+" to "+outFolder.toString());
    }

    /**
     * Called when saving, for the child class to save any other files
     *
     * @param outputFolder Path of the output folder
     * @param gson the Gson object to use for serialization, with correct pretty-printing-or-not status
     * */
    protected abstract void onSave(Path outputFolder, Gson gson);
}
