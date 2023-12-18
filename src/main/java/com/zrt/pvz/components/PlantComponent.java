package com.zrt.pvz.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.time.LocalTimer;
import com.zrt.pvz.EntityType;
import com.zrt.pvz.PVZApp;
import com.zrt.pvz.data.*;
import javafx.geometry.Point2D;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LeeWyatt
 * 
 * 植物组件, 攻击进入射程范围的敌人
 */
public class PlantComponent extends Component {

    private Map<String,AnimationChannel> animMap = new HashMap<>();
    private AnimatedTexture at;
    private PlantData plantData;
    private String plantName;
    private Point2D plantPosition;
    private int type = 0;
    private int row;
    private int column;
    private LocalTimer timer;  //用来控制持续掉血的计时器
    private Duration damageDuration;  //记录多久掉血一次
    private int attackedDamage;  //一次掉血多少
    private boolean attacked;  //是否被攻击
    private HealthIntComponent hp;
    private boolean dead;

    @Override
    public void onAdded() {
        plantData = entity.getObject("plantData");
        hp=new HealthIntComponent(plantData.hp());
        timer = FXGL.newLocalTimer();
        plantName = plantData.name();
        row=entity.getComponent(PositionComponent.class).getRow();
        column=entity.getComponent(PositionComponent.class).getColumn();
        if(!plantData.components().contains("TriggerComponent") && !plantData.components().contains("ShroomComponent")){
            List<AnimationData> animationData = plantData.animationData();
            for (AnimationData ad : animationData) {
                animMap.put(ad.status(),initAc(ad));
            }
            at=new AnimatedTexture(animMap.get("normal"));
            plantPosition = entity.getPosition();
            entity.getViewComponent().addChild(at);
            at.loopAnimationChannel(animMap.get("normal"));
        }

    }

    @Override
    public void onUpdate(double tpf) {
        if(attacked){
            if (timer.elapsed(damageDuration)) {
                hp.damage(attackedDamage);
                timer.capture();
            }
            if (hp.isZero()) {
                dead = true;
                PVZApp.removePlant(entity);
            }
        }

    }

    public void attacked(ZombieData zombieData){
        timer.capture();
        this.attackedDamage=zombieData.getAttackDamage();
        this.damageDuration=Duration.seconds(zombieData.getDamageDuration());
        this.attacked=true;
    }


    public void unAttacked(){
        this.attackedDamage=0;
        this.damageDuration=Duration.ZERO;
        this.attacked=false;
    }

    public void changeStatus(int type){
        if(this.type==type)return;
        this.type=type;
        animMap.replace("normal",animMap.get("normal"+type));
        at.loopAnimationChannel(animMap.get("normal"));
    }

    private AnimationChannel initAc(AnimationData at) {
        ArrayList<Image> imageArrayList=new ArrayList<>();
        for(int i=0;i<at.FrameNumber();i++){
            imageArrayList.add(FXGL.image(String.format(at.imageName(),i)));
        }
        return new AnimationChannel(imageArrayList,Duration.seconds(at.channelDuration()));
    }

    public boolean isDead(){
        return dead;
    }

    public HealthIntComponent getHp() {
        return hp;
    }

    public void SetHp(int multi){
        hp=new HealthIntComponent(plantData.hp()*multi);
    }

}
