package com.github.amusingimpala.bedifice.api.builders;

import com.github.amusingimpala.bedifice.api.util.FormatCodes;
import com.github.amusingimpala.bedifice.api.util.Processor;
import com.github.amusingimpala.bedifice.api.util.Vector3;
import com.github.amusingimpala.bedifice.impl.Builder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Locale;
import java.util.MissingFormatArgumentException;
import java.util.Optional;

/**
 * Builder class for manifest.json
 * */
//TODO: docs and subpacks
@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "unused", "UnusedReturnValue"})
public class ManifestBuilder implements Builder {

    private int formatVersion = 1;
    private Optional<JsonObject> header = Optional.empty();
    private final JsonArray modules = new JsonArray();
    private final JsonArray dependencies = new JsonArray();
    private final JsonArray capabilities = new JsonArray();
    private Optional<JsonObject> metaData = Optional.empty();

    /**
     * Sets the format version. Default it '1'
     *
     * @param num the format version
     * @return the current ManifestBuilder (this)
     * */
    public ManifestBuilder formatVersion(int num) {
        this.formatVersion = num;
        return this;
    }

    /**
     * Adds a header to the manifest, which is required
     *
     * @param name the pack name
     * @param version the version of the pack
     * @param builder the processor to add more entries to the builder, such as uuid, description, etc.
     * @return the current ManifestBuilder (this)
     * */
    public ManifestBuilder header(String name, Vector3 version, Processor<HeaderBuilder> builder) {
        this.header = Optional.of(builder.process(new HeaderBuilder(name, version)).build());
        return this;
    }

    /**
     * Adds a module to the pack.
     * At least one module is required for any functioning to apply
     * Calls {@link #module(ModuleType, Vector3, String, String)} with a default description of "[type] module"
     * Module types:
     *      - Resources: for resource packs
     *      - Data: for behaviour packs
     *      - Client Data: For behaviour packs that function on the client side only
     *      - Interface: ??
     *      - World Template: for world templates (.mcworld)
     * @param type the type of the module
     * @param version the version of the module (does not need to be the same as the pack version)
     * @param uuid the uuid of the pack, to distinguish from other packs. Use an online UUID generator to generate initially, but keep it consistent
     * @return the current ManifestBuilder (this)
     * */
    public ManifestBuilder module(ModuleType type, Vector3 version, String uuid) {
        return this.module(type, version, uuid, type.name().toLowerCase(Locale.ROOT) + "_module");
    }

    /**
     * Adds a module to the pack.
     * At least one module is required for any functioning to apply
     * Module types:
     *      - Resources: for resource packs
     *      - Data: for behaviour packs
     *      - Client Data: For behaviour packs that function on the client side only
     *      - Interface: ??
     *      - World Template: for world templates (.mcworld)
     * @param type the type of the module
     * @param version the version of the module (does not need to be the same as the pack version)
     * @param uuid the uuid of the pack, to distinguish from other packs. Use an online UUID generator to generate initially, but keep it consistent
     * @param description description of the module. Currently not displayed, only for your own use
     * @return the current ManifestBuilder (this)
     * */
    public ManifestBuilder module(ModuleType type, Vector3 version, String uuid, String description) {
        return this.module(type, version, uuid, description, null);
    }

    /**
     * Adds a module to the pack.
     * At least one module is required for any functioning to apply
     * Module types:
     *      - Resources: for resource packs
     *      - Data: for behaviour packs
     *      - Client Data: For behaviour packs that function on the client side only
     *      - Interface: ??
     *      - World Template: for world templates (.mcworld)
     * @param type the type of the module
     * @param version the version of the module (does not need to be the same as the pack version)
     * @param uuid the uuid of the pack, to distinguish from other packs. Use an online UUID generator to generate initially, but keep it consistent
     * @param description description of the module. Currently not displayed, only for your own use
     * @param entry the script entry of the program. Should be scripts/gametest/[name].js
     * @return the current ManifestBuilder (this)
     * */
    public ManifestBuilder module(ModuleType type, Vector3 version, String uuid, String description, String entry) {
        JsonObject moduleRoot = new JsonObject();

        moduleRoot.add("type", new JsonPrimitive(type.name().toLowerCase(Locale.ROOT)));
        moduleRoot.add("uuid", new JsonPrimitive(uuid));
        moduleRoot.add("version", version.toJson());
        moduleRoot.add("description", new JsonPrimitive(description));
        if (entry != null) {
            if (type != ModuleType.JAVASCRIPT) {
                throw new MissingFormatArgumentException("Cannot add an entry to a non Javascript module!");
            }
            moduleRoot.add("entry", new JsonPrimitive(entry));
        }

        modules.add(moduleRoot);
        return this;
    }

    /**
     * Adds a dependency of the pack. Dependencies are automatically added if this pack is added and the dependencies are
     * found. Useful for addons to self apply behaviour/resource when the other is added.
     *
     * @param version version of the pack depended on. Note, this is the pack version, not the module version
     * @param uuid UUID of the pack depended upon. Note, this is the PACK UUID, not the module UUID
     * @return the current ManifestBuilder (this)
     * */
    public ManifestBuilder dependency(String uuid, Vector3 version) {
        JsonObject dependencyRoot = new JsonObject();

        dependencyRoot.add("uuid", new JsonPrimitive(uuid));
        dependencyRoot.add("version", version.toJson());

        dependencies.add(dependencyRoot);
        return this;
    }

    /**
     * Adds a capability to the pack, requiring for certain *experimental* and/or non default vanilla behaviour,
     * such as raytracing, Edu-Edition, and Experimental UIs
     *
     * @param type type of the capability to require
     * @return the current ManifestBuilder (this)
     * */
    public ManifestBuilder capability(CapabilityType type) {
        capabilities.add(new JsonPrimitive(type.name().toLowerCase(Locale.ROOT)));
        return this;
    }

    /**
     * Adds metadata about the pack, such as the license, authors, and the pack URL.
     * Not required for functioning.
     *
     * @param proc Processor in which metadata info is added
     * @return the current ManifestBuilder (this)
     * */
    public ManifestBuilder metadata(Processor<MetaDataBuilder> proc) {
        this.metaData = Optional.of(proc.process(new MetaDataBuilder()).build());
        return this;
    }

    /**
     * @return the built manifest, ready to be saved.
     * */
    @Override
    public JsonObject build() {
        JsonObject root = new JsonObject();

        root.add("format_version", new JsonPrimitive(formatVersion));
        root.add("header", header.orElseThrow(() -> new MissingFormatArgumentException("Missing header object!")));
        root.add("modules", modules);
        root.add("dependencies", dependencies);
        metaData.ifPresent(md -> root.add("metadata", md));

        return root;
    }

    /**
     * Builder for the header section of the Manifest.json
     * */
    public static class HeaderBuilder implements Builder {

        private final String name;
        private final Vector3 version;
        private Optional<String> uuid = Optional.empty();
        private String description = FormatCodes.ITALIC + "Empty Description";
        private Optional<Vector3> minEngineVersion = Optional.empty();
        private Optional<Vector3> baseGameVersion = Optional.empty();
        private Optional<Boolean> lockTemplateOptions = Optional.empty();

        /**
         * Constructs a HeaderBuilder
         * Should only be called by {@link ManifestBuilder}
         * */
        private HeaderBuilder(String name, Vector3 version) {
            this.name = name;
            this.version = version;
        }

        /**
         * Adds a description to the header. Default is "\u00A7oEmptyDescription"
         *
         * @param desc description for the pack
         * @return the current HeaderBuilder (this)
         * */
        public HeaderBuilder description(String desc) {
            this.description = desc;
            return this;
        }

        /**
         * *Required*
         * Sets the UUID of the pack. Should be unique, use an online generator, but should remain consistent between
         * runs/updates
         *
         * @param uuid UUID of the pack
         * @return the current HeaderBuilder (this)
         * */
        public HeaderBuilder uuid(String uuid) {
            this.uuid = Optional.of(uuid);
            return this;
        }

        /**
         * Sets the minimum engine version, or the minimum Minecraft version required to run the pack.
         * Used for world templates (?)
         *
         * @param ver the minimum version of Minecraft required for the pack
         * @return the current HeaderBuilder (this)
         * */
        public HeaderBuilder minEngineVersion(Vector3 ver) {
            this.minEngineVersion = Optional.of(ver);
            return this;
        }

        /**
         * Sets the base game version required for the pack, used for Behaviour and Resource packs.
         * Should be set to the latest version
         *
         * @param ver minimum Minecraft version, should always be latest
         * @return the current HeaderBuilder (this)
         * */
        public HeaderBuilder baseGameVersion(Vector3 ver) {
            this.baseGameVersion = Optional.of(ver);
            return this;
        }

        /**
         * Locks the template options so the user cannot modify them, only used by world templates.
         *
         * @return the current HeaderBuilder (this)
         * */
        public HeaderBuilder lockTemplateOptions() {
            this.lockTemplateOptions = Optional.of(true);
            return this;
        }

        /**
         * Builds the header section into a JsonObject for the ManifestBuilder,
         * should only be called by {@link ManifestBuilder}
         *
         * @return the built header JsonObject
         * */
        @Override
        public JsonObject build() {
            JsonObject root = new JsonObject();

            root.add("name", new JsonPrimitive(name));
            root.add("version", version.toJson());

            root.add("description", new JsonPrimitive(description));
            root.add("uuid", new JsonPrimitive(uuid.orElseThrow(() -> new MissingFormatArgumentException("Missing Pack UUID!"))));

            minEngineVersion.ifPresent(ver -> root.add("min_engine_version", ver.toJson()));
            baseGameVersion.ifPresent(ver -> root.add("base_game_version", ver.toJson()));
            lockTemplateOptions.ifPresent(lock -> root.add("lock_template_options", new JsonPrimitive(lock)));

            return root;
        }
    }

    /**
     * Builder for the Manifest Metadata
     * */
    public static class MetaDataBuilder implements Builder {

        private final JsonArray authors = new JsonArray();
        private Optional<String> license = Optional.empty();
        private Optional<String> url = Optional.empty();

        /**
         * Adds the authors provided to the list of authors
         *
         * @param authors the list of authors to add
         * @return the current MetaDataBuilder (this)
         * */
        public MetaDataBuilder authors(String... authors) {
            for (String author : authors) {
                this.authors.add(new JsonPrimitive(author));
            }
            return this;
        }

        /**
         * Adds the license used by the addon to the metadata.
         * ONLY CALL ONCE!
         * License is not needed, but is advised. Addon license need not match the bEdifice-dependent project,
         * as the jsons outputted are output and as such are not covered by the GPL.
         *
         * While there are many licenses to choose, perhaps look at some <a href="">Free and Open Source licenses</a>.
         * Do note that addons would be listed as linking with proprietary code (Minecraft), and as such cannot use
         * the GNU GPL, but the LGPL, Expat (MIT), Apache 2.0, and CC-0 would work.
         *
         * @param license license of the addon
         * @return the current MetaDataBuilder (this)
         * */
        public MetaDataBuilder license(String license) {
            if (!license.isEmpty()) throw new MissingFormatArgumentException("Tried to add license, but license already existed!");
            this.license = Optional.of(license);
            return this;
        }

        /**
         * Sets the url for the pack
         *
         * @param url url of the pack
         * @return the current MetaDataBuilder (this)
         * */
        public MetaDataBuilder url(String url) {
            this.url = Optional.of(url);
            return this;
        }

        /**
         * Builds the MetaDataBuilder into a JsonObject for usage by the ManifestBuilder
         * Should only be called by {@link ManifestBuilder}
         *
         * @return the metadata JsonObject
         * */
        @Override
        public JsonObject build() {
            JsonObject root = new JsonObject();

            root.add("authors", authors);
            license.ifPresent(lic -> root.add("license", new JsonPrimitive(lic)));
            url.ifPresent(url -> root.add("url", new JsonPrimitive(url)));

            return root;
        }
    }

    /**
     * Type of a Pack Module
     * */
    public enum ModuleType {
        /**
         * For resource packs
         */
        RESOURCES,
        /**
         * For behaviour packs
         */
        DATA,
        /**
         * For behaviour packs
         */
        CLIENT_DATA,
        /**
         * Who knows what
         */
        INTERFACE,
        /**
         * For world templates
         */
        WORLD_TEMPLATE,
        /**
         * For the GameTest framework
         * */
        JAVASCRIPT,
        /**
         * For Skin Packs
         * */
        SKIN_PACK
    }

    /**
     * Types of disabled-by-default vanilla behaviours
     * */
    public enum CapabilityType {
        /**
         * Requires the <a href="https://aka.ms/minecraftscripting_turnbased">Custom HTML based UIs</a>,
         * only available on Win10 currently (see linked offical pack's resources)
         * */
        EXPERIMENTAL_CUSTOM_UI,
        /**
         * Requires Edu-Edition
         * */
        CHEMISTRY,
        /**
         * Require raytracing to be enabled. Useful for RTX-resource packs, however non RTX-compatible hardware will
         * not be able to run this.
         * */
        RAYTRACED
    }

}
