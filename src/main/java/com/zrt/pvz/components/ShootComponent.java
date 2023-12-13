package com.zrt.pvz.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;
import com.zrt.pvz.EntityType;
import com.zrt.pvz.data.BulletData;
import com.zrt.pvz.data.PlantData;
import javafx.geometry.Point2D;
import javafx.util.Duration;

/**
 * @author 曾瑞庭
 * @Description: 射击子弹组件
 * @date 2023/12/11 9:41
 */
public class ShootComponent extends Component {
    private LocalTimer shootTimer;
    private Duration attackRate;
    private BulletData bulletData;
    private PlantData plantData;
    private Point2D plantPosition;
    private int row;
    private int column;

    @Override
    public void onAdded() {
        plantData = entity.getObject("plantData");
        row=entity.getComponent(PositionComponent.class).getRow();
        column=entity.getComponent(PositionComponent.class).getColumn();
        plantPosition = entity.getPosition();
        attackRate = Duration.seconds(plantData.attackRate());
        bulletData = plantData.bulletData();
        shootTimer = FXGL.newLocalTimer();
        shootTimer.capture();
    }

    @Override
    public void onUpdate(double tpf) {
        if (!shootTimer.elapsed(attackRate)) {
            return;
        }
        attack();
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

    private void shootBullet(Entity enemy) {
        Point2D dir = new Point2D(1,0); //发射子弹方向，可调
        FXGL.spawn("bullet", new SpawnData(
                entity.getCenter().subtract(0, bulletData.height())) //生成子弹的位置，可调
                .put("bulletData", bulletData)
                .put("dir", dir)
        );
    }
}
