package com.zrt.pvz.data;

import java.util.List;

public record StatusData(
        String type,
        int numOfChange,
        List<Double> changeCondition
) {
}
