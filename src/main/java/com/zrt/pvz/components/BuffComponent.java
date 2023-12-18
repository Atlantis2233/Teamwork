package com.zrt.pvz.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.time.LocalTimer;
import com.zrt.pvz.EntityType;
import com.zrt.pvz.data.PlantData;
import javafx.util.Duration;

/**
 * @author 曾瑞庭
 * @Description:  用来给对应植物添加对应buff（释放技能）
 * @date 2023/12/18 14:32
 */
public class BuffComponent extends Component {
    private PlantData plantData;
    private LocalTimer timer;
    private boolean isStart;
    private Duration delay=Duration.seconds(2.0);
    @Override
    public void onAdded() {
        timer= FXGL.newLocalTimer();
        isStart=false;
        plantData=entity.getObject("plantData");
    }

    @Override
    public void onUpdate(double tpf) {
        if (!isStart||!timer.elapsed(delay)) {
            return;
        }
        CastSkill();
    }

    public void CastSkillDelay(){
        timer.capture();
        isStart=true;
    }

    //释放技能函数，根据对对应植物编写
    public void CastSkill(){
        isStart=false;
        //向日葵，连续产5个阳光，可调
        if(plantData.name().equals("SunFlower")){
            ProduceSunshineComponent produceSunshineComponent=
                    (ProduceSunshineComponent) entity.getComponent(ProduceSunshineComponent.class);
            FXGL.run(()->{
                produceSunshineComponent.spawnSunshine();
            }, Duration.seconds(0.3),5);
        }
        //豌豆射手，连续射10个子弹，可调
        else if(plantData.name().equals("PeaShooter")){
            ShootComponent shootComponent=(ShootComponent) entity.getComponent(ShootComponent.class);
            FXGL.run(()->{
                shootComponent.attack();
            }, Duration.seconds(0.12),10);
        }
        //坚果墙，恢复满血，并血量上限翻倍，可调
        else if(plantData.name().equals("WallNut")){
            PlantComponent plantComponent=(PlantComponent) entity.getComponent(PlantComponent.class);
            plantComponent.SetHp(2);
            Texture texture= FXGL.texture("plant/WallNut/InfiNut.png",65,80);
            entity.getViewComponent().clearChildren();
            entity.getViewComponent().addChild(texture);
        }
        //寒冰射手，
        else if(plantData.name().equals("SnowPea")){
            ShootComponent shootComponent=(ShootComponent) entity.getComponent(ShootComponent.class);
            FXGL.run(()->{
                shootComponent.attack();
            }, Duration.seconds(0.12),10);
        }
        //食人花,将射程内的僵尸吸到面前
        else if(plantData.name().equals("Chomper")){
            Texture texture=FXGL.texture("other/vortex.png",100,100);
            texture.setLayoutX(40);
            entity.getViewComponent().addChild(texture);
            PositionComponent plantPositionComponent=entity.getComponent(PositionComponent.class);
            int row=plantPositionComponent.getRow();
            int range=plantData.bulletData().range();
            for(Entity entry: FXGL.getGameWorld().getEntitiesByType(EntityType.ZOMBIE)){
                if(entity.distance(entry)<range){
                    ZombieComponent zombieComponent=(ZombieComponent) entry.getComponent(ZombieComponent.class);
                    zombieComponent.stopMove();
                    MoveComponent moveComponent= entry.getComponent(MoveComponent.class);
                    moveComponent.moveFromTo(entry.getPosition(),entity.getPosition(),0.6);
                    PositionComponent positionComponent=entry.getComponent(PositionComponent.class);
                    positionComponent.setRow(row);
                }
            }
            FXGL.runOnce(()->{
                if(entity!=null){
                    entity.getViewComponent().removeChild(texture);
                }
            },Duration.seconds(4.0));
        }
    }
}
