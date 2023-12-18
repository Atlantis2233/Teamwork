package com.zrt.pvz.data;

import java.util.List;
/**
 * 敌人元数据,与json数据对应
 */
public class ZombieData {
	private int reward;
	private String preview;
	private int moveSpeed;
	private List<AnimationData> animationData;
	private int hp;
	private int width;
	private BboxData bboxData;
	private BombData bombData;
	private StatusData statusData;
	private int height;
	private int attackDamage;
	private double damageDuration;
	private List<String> components;

	public int getAttackDamage() {
		return attackDamage;
	}

	public void setAttackDamage(int attackDamage) {
		this.attackDamage = attackDamage;
	}

	public double getDamageDuration() {
		return damageDuration;
	}

	public void setDamageDuration(double damageDuration) {
		this.damageDuration = damageDuration;
	}

	public int getReward(){
		return reward;
	}

	public String getPreview(){
		return preview;
	}

	public int getMoveSpeed(){
		return moveSpeed;
	}

	public List<AnimationData> getAnimationData(){
		return animationData;
	}

	public int getHp(){
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getWidth(){
		return width;
	}

	public BboxData getBboxData(){
		return bboxData;
	}

	public BombData getBombData() {
		return bombData;
	}

	public StatusData getStatusData() {
		return statusData;
	}

	public List<String> getComponents() {
		return components;
	}

	public int getHeight(){
		return height;
	}
}