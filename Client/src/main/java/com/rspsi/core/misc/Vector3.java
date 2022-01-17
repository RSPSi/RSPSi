package com.rspsi.core.misc;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Vector3 {

    public Vector3(){

    }

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    private double x, y, z;

}
