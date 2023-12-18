package com.zrt.pvz.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.zrt.pvz.data.*;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 曾瑞庭
 * @Description:
 * @date 2023/12/6 17:30
 */
public class BulletComponent extends Component {
    //初始位置
    private Point2D initPosition;
    //伤害
    private int damage;
    //攻击半径
    private int range;
    //攻击效果
    private String effectName;
    private int delay;
    private AnimationChannel anim;
    private AnimatedTexture at;
    private Point2D plantPosition;

    @Override
    public void onAdded() {
        initPosition = entity.getPosition();
        BulletData bulletData = entity.getObject("bulletData");
        EffectData effectData = bulletData.effectData();
        effectName = effectData.name();
        damage = bulletData.attackDamage();
        range = bulletData.range();
        anim = initAc(bulletData);
        at=new AnimatedTexture(anim);
        plantPosition = entity.getPosition();
        entity.getViewComponent().addChild(at);
        at.play();
    }

    @Override
    public void onUpdate(double tpf) {
        Point2D newPosition = entity.getPosition();
        //如果超过射程(攻击范围).那么移除
        if (newPosition.distance(initPosition) > range) {
            if (entity.isActive()) {
                entity.removeFromWorld();
            }
        }

    }
    private AnimationChannel initAc(BulletData bt) {
        ArrayList<Image> imageArrayList=new ArrayList<>();
        for(int i=0;i<bt.FrameNumber();i++){
            imageArrayList.add(FXGL.image(String.format(bt.imageName(),i)));
        }
        return new AnimationChannel(imageArrayList,Duration.seconds(bt.channelDuration()));
    }
    public int getDamage() {
        return damage;
    }

    public String getEffectName() {
        return effectName;
    }
    public int getDelay(){
        return this.delay;
    }
}
