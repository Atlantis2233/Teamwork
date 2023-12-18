package com.zrt.pvz.data;

/**
 * 子弹元数据,与json文件的数据对应
 */
public record BulletData(
	String imageName,
	EffectData effectData,
	int width,
	int range,
	int speed,
	int height,
	int offsetX,
	int offsetY,
	int delay,
	int FrameNumber,
	double channelDuration,
	int attackDamage,
	int number,
	int line
) {
}
