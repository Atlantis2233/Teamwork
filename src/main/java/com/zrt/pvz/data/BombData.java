package com.zrt.pvz.data;


public record BombData (
        String imageName,
        int width,
        int height,
        int offsetX,
        int offsetY,
        int attackDamage,
        double prepareDuration,
        double boomDuration

){
}
