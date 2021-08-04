package com.github.amusingimpala.bedifice.api.util;

import java.util.function.Consumer;

/**
 * Consumer that returns consumed value
 * */
public interface Processor<T> extends Consumer<T> {

    /**
     * Consumes and returns the value inputted
     *
     * @return the value inputted and consumed
     * */
    default T process(T val) {
        this.accept(val);
        return val;
    }

}
