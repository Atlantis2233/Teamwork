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
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LeeWyatt
 * 
 * 植物组件, 攻击进入射程范围的敌人
 */
public class PlantComponent extends Component {
    private LocalTimer shootTimer;
    private AnimationChannel ac;
    private AnimatedTexture at;
    private PlantData plantData;
    private String plantName;
    private Duration attackRate;
    private Point2D plantPosition;
    private BulletData bulletData;
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
        attackRate = Duration.seconds(plantData.attackRate());
        ArrayList<Image> imageArrayList=new ArrayList<>();
        for(int i=0;i<plantData.animationData().FrameNumber();i++){
            imageArrayList.add(FXGL.image(String.format(plantData.animationData().imageName(),i)));
        }
        ac=new AnimationChannel(imageArrayList,Duration.seconds(plantData.animationData().channelDuration()));
        at=new AnimatedTexture(ac);

        plantPosition = entity.getPosition();
        bulletData = plantData.bulletData();
        entity.getViewComponent().addChild(at);
        at.loop();
        shootTimer = FXGL.newLocalTimer();
        shootTimer.capture();

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
                entity.removeFromWorld();
            }
        }
        if (!shootTimer.elapsed(attackRate)) {
            return;
        }
            attack();
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

    public boolean isDead(){
        return dead;
    }


    private void attack() {
        FXGL.getGameWorld().getClosestEntity(entity,
                        e -> e.isType(EntityType.ZOMBIE)
                                && e.getComponent(PositionComponent.class).getRow()==row
                                && e.getPosition().distance(plantPosition) < bulletData.range()
                )
                .ifPresent(enemy -> {
                    shootBullet(enemy);
                    shootTimer.capture();
                });
    }

    private void arrowTowerAttack() {
        List<Entity> es = FXGL.getGameWorld().getEntitiesByType(EntityType.PLANT);
        int bulletNum = 0;
        boolean flag = false;
        for (Entity enemy : es) {
            if (bulletNum > ConfigData.MAX_BULLET_AMOUNT) {
                break;
            }
            Point2D ep = enemy.getPosition();
            //判断是否在射程之内 ; TODO 用 enemy.distanceBBox(tower) 这样判断其实更精确
            if (ep.distance(plantPosition) <= bulletData.range()) {
                flag = true;
                bulletNum++;
                shootBullet(enemy);
            }
        }
        if (flag) {
            shootTimer.capture();
        }
    }

    private void shootBullet(Entity enemy) {
        Point2D dir = new Point2D(1,0); //发射子弹方向，可调
        FXGL.spawn("bullet", new SpawnData(
                entity.getCenter().subtract(0, bulletData.height())) //生成子弹的位置，可调
                .put("bulletData", bulletData)
                .put("dir", dir)
        );
    }

}
