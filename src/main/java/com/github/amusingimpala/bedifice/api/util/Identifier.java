package com.github.amusingimpala.bedifice.api.util;

/**
 * Identifies items via namespace and path
 * */
public record Identifier(String namespace, String path) {

    @Override
    public String toString() {
        return namespace + ":" + path;
    }
}
