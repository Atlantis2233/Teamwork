package com.zrt.pvz.data;

import java.util.List;
import java.util.Map;

/**
 * 关卡元数据,与json数据对应
 */
public record LevelData(
	int amount, //总数量
	String name, // 关卡名称
	Map<String, Integer> zombieMap, //僵尸种类及其权重
	double interval, //产生僵尸的间隔
	double produceSunshineInterval,//生成阳光间隔
	List<String>plants, //本关可用植物
	String reward,
	String map, //地图
	List<Double> waves, //波次
	int width //地图长度
) {
}
