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
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxgl.ui.ProgressBar;
import com.zrt.pvz.PVZApp;
import com.zrt.pvz.data.*;
import com.zrt.pvz.data.ZombieData;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.scene.effect.ColorAdjust;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.util.Pair;

import java.util.*;

/**
 * @author zrt
 * 
 * 敌人组件,不同移动方向, 有不同的图片
 *          不同的特殊伤害效果,有不同的表现
 */
public class ZombieComponent extends Component {
    private AnimatedTexture texture;
    private Map<String,AnimationChannel> animMap = new HashMap<>();
    private boolean dead;
    private boolean attack,boom,kill;
    private LocalTimer killTimer;
    private Duration beforeKilledDuration;
    private ProgressBar hpBar;
    private Texture slowDownTexture;
    private Point2D nextWaypoint=new Point2D(-1,0);
    private int type = 0;
    private int index = 0;
    private int moveSpeed;
    private HealthIntComponent hp;
    private ZombieData zombieData;
    private int moveSpeedTemp;
    private SimpleDoubleProperty progress;
    private double lastTime=5;//可改
    private Random random;

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        moveSpeed = 0;
        this.dead = dead;
        FXGL.inc("kill", 1);
        PVZApp app=(PVZApp)FXGL.getApp();
        app.setLastZombieDiePoint(entity.getPosition());
        entity.getViewComponent().removeChild(hpBar);
        entity.getBoundingBoxComponent().clearHitBoxes();
        if(kill){
            entity.removeFromWorld();
        }
        if(!attack){
            texture.setTranslateY(-5);
            texture.setTranslateX(-60);
        }
        else if(attack){
            texture.setTranslateY(-10);
            texture.setTranslateX(-80);
        }
        if(boom){
            texture.setTranslateY(-43);
            texture.setTranslateX(-22);
            texture.playAnimationChannel(animMap.get("boomDie"));
        }
        else {
            texture.playAnimationChannel(animMap.get("die"));
        }
        texture.setOnCycleFinished(() -> entity.removeFromWorld());
    }

    @Override
    public void onAdded() {
        zombieData = entity.getObject("zombieData");
        moveSpeed = zombieData.getMoveSpeed();
        moveSpeedTemp = moveSpeed;
        attack = false;
        boom = false;
        kill = false;
        addHpComponentView(zombieData);
        List<AnimationData> animationData = zombieData.getAnimationData();
        for (AnimationData at : animationData) {
            animMap.put(at.status(),initAc(at));
        }
        texture = new AnimatedTexture(animMap.get("left"));
        texture.setScaleX(0.9);
        texture.setScaleY(0.9);
        texture.setTranslateY(-50);
        texture.setTranslateX(-35);
        entity.getViewComponent().addChild(texture);
//        slowDownTexture = FXGL.texture("buffer/slow.png", entity.getWidth(), entity.getHeight());
//        slowDownTexture.setVisible(false);
//        entity.getViewComponent().addChild(slowDownTexture);
        PVZApp app = (PVZApp) (FXGL.getApp());
        walkAnim();
    }

    private void walkAnim() {
        texture.loopAnimationChannel(animMap.get("left"));

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
        if(kill && killTimer.elapsed(beforeKilledDuration)){
            setDead(true);
        }
        if (hp.isZero()) {
            dead = true;
            return;
        }
        if(progress!=null&&!dead){
            progress.set(progress.get()+tpf);
            if(progress.get()>0.42*lastTime)
                updateBrightness(0);
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
        if (effectName.equalsIgnoreCase(ConfigData.EFFECT_SLOW_DOWN)&&!dead) {
            entity.getComponent(EffectComponent.class).startEffect(new SlowTimeEffect(0.4, Duration.seconds(lastTime)));
            progress=new SimpleDoubleProperty();
            updateBrightness(-7);
        }

        if(zombieData.getName().equals("BucketheadZombie")) {
            if(random==null){
                FXGL.play("shieldhit1.wav");
            }
            else{
                FXGL.play("shieldhit"+random.nextInt(2)+".wav");
            }
        }
        else{
            FXGL.play("splat.wav");
        }

        hp.damage(damage);

        if (hp.isZero()) {
            setDead(true);
        }
    }

    public void boomAttacked(BombData bombData){
        int damage = bombData.attackDamage();
        if(damage>=hp.getValue()){
            boom = true;
            setDead(true);
        }
    }

    public void attack(){
        attack = true;
        moveSpeed = 0;
        texture.loopAnimationChannel(animMap.get("attack"));
    }

    public void unAttack(){
        attack = false;
        moveSpeed = moveSpeedTemp;
        texture.loopAnimationChannel(animMap.get("left"));
    }

    public void trigger(TriggerData triggerData){
        if(triggerData.effect().equals("kill")){
            kill=true;
            beforeKilledDuration = Duration.seconds(triggerData.animDuration());
            killTimer = FXGL.newLocalTimer();
            killTimer.capture();
        }
    }

    public void changeStatus(int type){
        if(this.type==type)return;
        this.type=type;
        animMap.replace("right",animMap.get("right"+type));
        animMap.replace("left",animMap.get("left"+type));
        animMap.replace("attack",animMap.get("attack"+type));
        if(zombieData.getName().equals("NewspaperZombie")){
            zombieData.setDamageDuration(zombieData.getDamageDuration()/2);
            moveSpeedTemp=moveSpeedTemp*3;
        }
        if(attack)attack();
        else unAttack();

    }

    public HealthIntComponent getHp() {
        return hp;
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
        hpBar.setTranslateX((zombieData.getWidth() - 40) / 2.0-35);
        hpBar.setHeight(7);
        hpBar.setTranslateY(-50);
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
    private void updateBrightness(double Hue) {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setHue(Hue);
        colorAdjust.setBrightness(Hue/40);
        if(entity!=null){
            entity.getViewComponent().getChildren().get(1).setEffect(colorAdjust);
        }
    }
}
