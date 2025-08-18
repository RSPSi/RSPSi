package com.rspsi.core.misc;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class XTEA {

    @SerializedName(value="region", alternate = {"mapsquare"})
    private final int region;

    @SerializedName(value="keys", alternate = {"key"})
    private final int[] keys;
}
