package com.rspsi.core.misc;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Vector2 {

    public Vector2(){

    }

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }
    private double x, y;

}
