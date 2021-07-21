package com.rspsi.jagex.cache;

import com.displee.cache.index.Index;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;

import java.util.Arrays;
import java.util.Comparator;

public class ArchiveUtils {


    public static Archive getHighestArchive(Index index) {
        return Arrays.stream(index.archives()).max(Comparator.comparingInt(Archive::getId)).orElse(null);
    }

    public static File getHighestFile(Archive archive) {
        return Arrays.stream(archive.files()).max(Comparator.comparingInt(File::getId)).orElse(null);
    }
}
