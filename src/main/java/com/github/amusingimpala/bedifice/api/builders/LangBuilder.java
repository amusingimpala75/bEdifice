package com.github.amusingimpala.bedifice.api.builders;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for building locales that when saved follow the format of:
 * key=translation
 * */
public class LangBuilder {

    private final Map<String, String> translations = new HashMap<>();
    private final String locale;

    /**
     * Creates a LangBuilder given a locale
     *
     * @param locale the locale of the .lang file, in format of language_COUNTRY (en_US, cs_CZ, etc.)
     * */
    public LangBuilder(String locale) {
        this.locale = locale;
    }

    /**
     * Returns the locale of this LangBuilder
     *
     * @return the locale of this, in format of language_COUNTRY (en_US, cs_CZ, etc.)
     * */
    public String getLocale() {
        return this.locale;
    }

    /**
     * Adds a Translation, given a key and a translation of the key
     *
     * @param key the key
     * @param translation the translation
     * @return the current LangBuilder (this)
     * */
    public LangBuilder translation(String key, String translation) {
        this.translations.put(key, translation);
        return this;
    }

    /**
     * Assembles the LangBuilder into a String, with entries in the format of:
     * key=translation
     * and entries separated by newline (\n)
     *
     * @return the assembled lang String, ready for saving to a .lang file
     * */
    public String assemble() {
        StringBuilder file = new StringBuilder();

        for (Map.Entry<String, String> entry : translations.entrySet()) {
            file.append(entry.getKey());
            file.append("=");
            file.append(entry.getValue());
            file.append("\n");
        }
        file.append("\n");

        return file.toString();
    }
}
