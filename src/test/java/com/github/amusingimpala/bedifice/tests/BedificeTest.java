package com.github.amusingimpala.bedifice.tests;

import com.github.amusingimpala.bedifice.api.Bedifice;
import com.github.amusingimpala.bedifice.api.BedificeSkinPack;
import com.github.amusingimpala.bedifice.api.builders.ManifestBuilder;
import com.github.amusingimpala.bedifice.api.util.Vector3;

public class BedificeTest {

    public static void main(String[] args) {
        //Test Skin Pack
        BedificeSkinPack skinPack = Bedifice.createSkinPack("lotr_skin_pack", pack -> pack
                .include("lotr_skin_pack")
                .manifest(man -> man
                        .header("Lord of the Rings Skin Pack", new Vector3(1, 0, 0), head -> head
                                .uuid("b3cb6267-9d55-4d2c-a10c-76121f5db096"))
                        .module(
                                ManifestBuilder.ModuleType.SKIN_PACK,
                                new Vector3(1, 0, 0),
                                "70e9e379-4a18-4c79-b3d0-cd4b394232ed"
                        )
                        .metadata(meta -> meta
                                .authors("graceforthewin, anonymous")
                        )
                )
                .skins(skin -> skin
                        .skin("frodo")
                        .skin("legolas")
                ).translations("en_US", lang -> lang
                        .translation("skin.lotr_skin_pack.frodo", "Frodo")
                        .translation("skin.lotr_skin_pack.legolas", "Legolas")
                        .translation("skinpack.lotr_skin_pack", "Lord of the Rings Skin Pack")
                )
        );
        skinPack.save("pretty_print", true);
        skinPack.save("space_saver", false);
        skinPack.save();
    }

}
