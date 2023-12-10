package com.zrt.pvz.components;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.dsl.effects.SlowTimeEffect;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.ui.ProgressBar;
import com.zrt.pvz.PVZApp;
import com.zrt.pvz.data.*;
import com.zrt.pvz.data.ZombieData;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author zrt
 * 
 * 敌人组件,不同移动方向, 有不同的图片
 *          不同的特殊伤害效果,有不同的表现
 */
public class ZombieComponent extends Component {
    private AnimatedTexture texture;
    private AnimationChannel animWalkRight, animWalkLeft,  animDie,animAttack;
    private boolean dead;
    private ProgressBar hpBar;
    private Texture slowDownTexture;
    private Point2D nextWaypoint=new Point2D(-1,0);
    private int index = 0;
    private int moveSpeed;
    private HealthIntComponent hp;
    private ZombieData zombieData;
    private int moveSpeedTemp;

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    @Override
    public void onAdded() {
        zombieData = entity.getObject("zombieData");
        moveSpeed = zombieData.getMoveSpeed();
        addHpComponentView(zombieData);
        List<AnimationData> animationData = zombieData.getAnimationData();
        for (AnimationData at : animationData) {
            if (at.status().equalsIgnoreCase("right")) {
                animWalkRight = initAc(at);
            } else if (at.status().equalsIgnoreCase("left")) {
                animWalkLeft = initAc(at);
            }
             else if (at.status().equalsIgnoreCase("die")) {
                animDie = initAc(at);
            }
            else if (at.status().equalsIgnoreCase("attack")) {
                animAttack = initAc(at);
            }
        }
        texture = new AnimatedTexture(animWalkLeft);
        entity.getViewComponent().addChild(texture);
//        slowDownTexture = FXGL.texture("buffer/slow.png", entity.getWidth(), entity.getHeight());
//        slowDownTexture.setVisible(false);
//        entity.getViewComponent().addChild(slowDownTexture);
        PVZApp app = (PVZApp) (FXGL.getApp());
        walkAnim();
    }

    private void walkAnim() {
        texture.loopAnimationChannel(animWalkLeft);

//        String dir = pointInfos.get(index).getValue();
//        if ("left".equals(dir)) {
//            texture.loopAnimationChannel(animWalkLeft);
//        } else if ("right".equals(dir)) {
//            texture.loopAnimationChannel(animWalkRight);
//        } else if ("up".equals(dir)) {
//            texture.loopAnimationChannel(animWalkUp);
//        } else if ("down".equals(dir)) {
//            texture.loopAnimationChannel(animWalkDown);
//        }
    }

    public void stopMove() {
        moveSpeedTemp = moveSpeed;
        moveSpeed = 0;
        texture.stop();
    }

    public void restartMove() {
        moveSpeed = moveSpeedTemp;
        texture.loop();
    }

    @Override
    public void onUpdate(double tpf) {
        if (dead) {
            return;
        }
//        boolean b = entity.getComponent(EffectComponent.class).hasEffect(SlowTimeEffect.class);
//        slowDownTexture.setVisible(b);
        double speed = tpf * moveSpeed;
        Point2D velocity = nextWaypoint
                //.subtract(entity.getPosition())
                .normalize()
                .multiply(speed);

        entity.translate(velocity);

        //改变方向
//        if (nextWaypoint.distance(entity.getPosition()) < speed) {
//            entity.setPosition(nextWaypoint);
//            walkAnim();
//            index++;
//            if (index < pointInfos.size()) {
//                nextWaypoint = pointInfos.get(index).getKey();
//            }
//        }
    }

    public void attacked(BulletData bulletData) {
        int damage = bulletData.attackDamage();
        String effectName = bulletData.effectData().name();
        //减速
        if (effectName.equalsIgnoreCase(ConfigData.EFFECT_SLOW_DOWN)) {
            entity.getComponent(EffectComponent.class).startEffect(new SlowTimeEffect(0.4, Duration.seconds(5)));
        }

        hp.damage(damage);

        if (hp.isZero()) {
            dead = true;
            FXGL.inc("kill", 1);
            entity.getViewComponent().removeChild(hpBar);
            entity.getBoundingBoxComponent().clearHitBoxes();
            texture.playAnimationChannel(animDie);
            texture.setOnCycleFinished(() -> entity.removeFromWorld());
        }
    }

    public void attack(){
        moveSpeedTemp = moveSpeed;
        moveSpeed = 0;
        texture.loopAnimationChannel(animAttack);
    }

    public void unAttack(){
        moveSpeed = moveSpeedTemp;
        texture.loopAnimationChannel(animWalkLeft);
    }

    private AnimationChannel initAc(AnimationData at) {
        ArrayList<Image> imageArrayList=new ArrayList<>();
        for(int i=0;i<at.FrameNumber();i++){
            imageArrayList.add(FXGL.image(String.format(at.imageName(),i)));
        }
        return new AnimationChannel(imageArrayList,Duration.seconds(at.channelDuration()));
    }

    private void addHpComponentView(ZombieData zombieData) {
        int maxHp = zombieData.getHp();
        hp = new HealthIntComponent(maxHp);
        hpBar = new ProgressBar(false);
        hpBar.setFill(Color.LIGHTGREEN);
        hpBar.setWidth(40);
        hpBar.setTranslateX((zombieData.getWidth() - 40) / 2.0);
        hpBar.setHeight(7);
        hpBar.setTranslateY(-5);
        hpBar.setMaxValue(maxHp);
        hpBar.setCurrentValue(maxHp);
        hpBar.currentValueProperty().bind(hp.valueProperty());
        hp.valueProperty().addListener((ob, ov, nv) -> {
            int value = nv.intValue();
            if (value > maxHp * 0.65) {
                hpBar.setFill(Color.LIGHTGREEN);
            } else if (value > maxHp * 0.25) {
                hpBar.setFill(Color.GOLD);
            } else {
                hpBar.setFill(Color.RED);
            }
        });
        entity.getViewComponent().addChild(hpBar);
        entity.addComponent(hp);
    }
}
