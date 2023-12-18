package com.zrt.pvz.data;

import java.util.List;

/**
 * 炮塔元数据,与json数据对应
 */
public record PlantData(
	int cost,
	List<AnimationData> animationData,
	String name,
	String icon,
	int width,
	BulletData bulletData,
	BombData bombData,
	TriggerData triggerData,
	StatusData statusData,
	double attackRate,
	int height,
	int hp,
	int CD,
	int offsetX,
	int offsetY,
	double shootInterval,
	List<String> components
) {
}
