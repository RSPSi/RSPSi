package com.jagex.cache.loader;

import com.displee.cache.index.archive.Archive;

public interface DataLoaderBase<T> {

    T forId(final int id);

    int count();

    void init(final Archive archive);

}
