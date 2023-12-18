package com.zrt.pvz.data;


public record TriggerData(
        int width,
        int height,
        int offsetX,
        int offsetY,
        boolean isReusable,
        String effect,
        double prepareDuration,
        double CD,
        double animDuration
) {
}
