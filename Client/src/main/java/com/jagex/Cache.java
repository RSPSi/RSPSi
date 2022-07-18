package com.jagex;

import com.displee.cache.CacheLibrary;
import com.displee.cache.index.Index;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;
import lombok.Setter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.BiFunction;

import com.jagex.cache.graphics.Sprite;
import com.jagex.net.ResourceProvider;
import com.rspsi.cache.CacheFileType;
import com.rspsi.core.misc.FixedIntegerKeyMap;
import com.rspsi.core.misc.XTEAManager;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Cache {


    @Setter
    /**
     * Set this to override how all cache files except maps are loaded
     * args = [fileType, fileId]
     * return byte[] or Optional.empty() to continue with normal loading
     */
    private BiFunction<CacheFileType, Integer, Optional<byte[]>> fileRetrieverOverride;

    @Setter
    /**
     * Set this to override how the map files are loaded
     * args = [fileId, regionId]
     * return byte[] or Optional.empty() to continue with normal loading
     */
    private BiFunction<Integer, Integer, Optional<byte[]>> mapRetrieverOverride;


    @Getter
    private CacheLibrary indexedFileSystem;

    private Index modelArchive, mapArchive, configArchive, skeletonArchive, skinArchive, spriteIndex, textureIndex, spotAnimIndex, varbitIndex, locIndex;

    public Cache(Path path) {
        log.info("Loading cache at {}", path);
        indexedFileSystem = new CacheLibrary(path.toFile().toString(), false, null);
        if (indexedFileSystem.is317()) {
            modelArchive = indexedFileSystem.index(1);
            mapArchive = indexedFileSystem.index(4);
            configArchive = indexedFileSystem.index(0);
            skinArchive = indexedFileSystem.index(2);
            skeletonArchive = null;//317 loads inside skins
            log.info("Loaded cache in 317 format!");
        } else if (indexedFileSystem.isOSRS()) {
            modelArchive = indexedFileSystem.index(7);
            mapArchive = indexedFileSystem.index(5);
            configArchive = indexedFileSystem.index(2);
            skeletonArchive = indexedFileSystem.index(0);
            skinArchive = indexedFileSystem.index(1);
            spriteIndex = indexedFileSystem.index(8);
            textureIndex = indexedFileSystem.index(9);
            log.info("Loaded cache in OSRS format!");
        } else if (indexedFileSystem.isRS3()) {
            modelArchive = indexedFileSystem.index(7);
            mapArchive = indexedFileSystem.index(5);
            configArchive = indexedFileSystem.index(2);
            skeletonArchive = indexedFileSystem.index(0);
            skinArchive = indexedFileSystem.index(1);
            spriteIndex = indexedFileSystem.index(8);
            textureIndex = indexedFileSystem.index(9);
            spotAnimIndex = indexedFileSystem.index(21);
            varbitIndex = indexedFileSystem.index(22);
            locIndex = indexedFileSystem.index(16);
            log.info("Loaded cache in RS3 format!");
        } else if (indexedFileSystem.isRS3()) {
            throw new UnsupportedOperationException("Cache format not supported!");
        }
        resourceProvider = new ResourceProvider(this);
        Thread t = new Thread(resourceProvider);
        t.start();
    }

    public ResourceProvider resourceProvider;


    private FixedIntegerKeyMap<Sprite> spriteCache = new FixedIntegerKeyMap<Sprite>(100);


    public Sprite getSprite(int id) {
        if (spriteCache.contains(id))
            return spriteCache.get(id);
        if (!indexedFileSystem.isOSRS())
            throw new RuntimeException("Cannot grab sprite by ID on 317!");
        Sprite sprite = Sprite.decode(ByteBuffer.wrap(spriteIndex.archive(id).file(0).getData()));
        spriteCache.put(id, sprite);
        System.out.println("GETSPRITE " + id);
        return sprite;
    }

    public final Index getFile(CacheFileType index) {
        try {
            switch (index) {
                case CONFIG:
                    return configArchive;
                case MODEL:
                    return modelArchive;
                case ANIMATION:
                    return skinArchive;
                case SKELETON:
                    return skeletonArchive;
                case SOUND:
                    break;
                case MAP:
                    return mapArchive;
                case SPRITE:
                    return spriteIndex;
                case TEXTURE:
                    return textureIndex;
                case SPOT:
                    return spotAnimIndex;
                case VARBIT:
                    return varbitIndex;
                case LOC:
                    return locIndex;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public final byte[] readMap(int fileId, int regionId) {
        if (mapRetrieverOverride != null) {
            Optional<byte[]> data = mapRetrieverOverride.apply(fileId, regionId);
            if (data.isPresent())
                return data.get();
        }
        if (indexedFileSystem.is317())
            return mapArchive.archive(fileId).file(0).getData();
        return mapArchive.archive(fileId, XTEAManager.lookupMap(regionId)).file(0).getData();
    }

    public final byte[] getFile(CacheFileType type, int file) {
        try {
            if (fileRetrieverOverride != null) {
                Optional<byte[]> data = fileRetrieverOverride.apply(type, file);
                if (data.isPresent())
                    return data.get();
            }
            switch (type) {
                case CONFIG:
                    return configArchive.archive(file).file(0).getData();
                case MODEL:
                    return modelArchive.archive(file).file(0).getData();
                case ANIMATION:
                    return skinArchive.archive(file).file(0).getData();
                case SKELETON:
                    return skeletonArchive.archive(file).file(0).getData();
                case SOUND:
                    break;
                case MAP:
                    return mapArchive.archive(file).file(0).getData();
                case TEXTURE:
                    break;
                case SPOT:
                    return spotAnimIndex.archive(file >>> 8).file(file & 0xff).getData();
                case VARBIT:
                    return varbitIndex.archive(file >>> 1416501898).file(file & 0x3ffff).getData();
                case LOC:
                    return locIndex.archive(file >> 8).file((file) & (1 << 8) - 1).getData();
            }
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        return null;
    }


    public final File writegetFile(CacheFileType index, String name, int file, byte[] data, int[] xteas) {
        try {
            switch (index) {
                case CONFIG:
                   // configArchive.createIfNotExist(file);
                    return configArchive.archive(file).add(0, data);
                case MODEL:
                   // modelArchive.createIfNotExist(file);
                    return modelArchive.archive(file).add(0, data);
                case ANIMATION:
                    //skinArchive.createIfNotExist(file);
                    return skinArchive.archive(file).add(0, data);
                case SOUND:
                    break;
                case MAP:
                   // mapArchive.createIfNotExist(file);
                    return mapArchive.archive(file).add(0, data);
                case TEXTURE:
                    break;
                case SPOT:
                    return spotAnimIndex.add(file >>> 8).add(file & 0xff, data);
                case VARBIT:
                    return varbitIndex.add(file >>> 1416501898).add(file & 0x3ffff, data);
                case LOC:
                    return locIndex.add(file >> 8).add((file) & (1 << 8) - 1, data);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public final Archive createArchive(int file, String name) {
        return configArchive.archive(file);
    }

    public void close() throws IOException {
        indexedFileSystem.close();
    }

    public ResourceProvider getProvider() {
        return resourceProvider;
    }


}
