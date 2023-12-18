package com.zrt.pvz.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;
import com.zrt.pvz.EntityType;
import com.zrt.pvz.PVZApp;
import com.zrt.pvz.data.BombData;
import com.zrt.pvz.data.PlantData;
import com.zrt.pvz.data.ZombieData;
import javafx.geometry.Point2D;
import javafx.util.Duration;


//修改
public class BombComponent extends Component {
    private boolean isPlant=false,isZombie=false,isBomb=false;
    private LocalTimer bombTimer;
    private Duration prepareDuration,boomDuration;
    private PlantData plantData;
    private ZombieData zombieData;
    private BombData bombData;
    private Point2D position;

    @Override
    public void onAdded(){
        if(entity.isType(EntityType.PLANT)){
            isPlant=true;
            plantData=entity.getObject("plantData");
            bombData = plantData.bombData();
        }
        else if(entity.isType(EntityType.ZOMBIE)){
            isZombie=true;
            zombieData=entity.getObject("zombieData");
            bombData = zombieData.getBombData();
        }
        else if(entity.isType(EntityType.BOMB)){
            isBomb = true;
            bombData = entity.getObject("bombData");
        }
        position=entity.getPosition();
        prepareDuration = Duration.seconds(bombData.prepareDuration());
        boomDuration = Duration.seconds(bombData.boomDuration());
        bombTimer = FXGL.newLocalTimer();
        bombTimer.capture();
    }

    @Override
    public void onUpdate(double tpf){
        if(isPlant||isZombie){
            if(!bombTimer.elapsed(prepareDuration)){
                return;
            }
            boom();
        }
        else if(isBomb){
            if(!bombTimer.elapsed(boomDuration)){
                return;
            }
            finished();
        }
    }

    private void boom(){
        FXGL.spawn("bomb",new SpawnData(entity.getCenter().subtract(bombData.offsetX(), bombData.offsetY()))
                .put("bombData",bombData)
        );
        if(!plantData.components().contains("TriggerComponent")){
            if(plantData.name().equals("DoomShroom")) {
                System.out.println("find DoomShroom");
                FXGL.spawn("crater", new SpawnData(entity.getCenter().subtract(bombData.offsetX(), bombData.offsetY()))
                        .put("row", entity.getComponent(PositionComponent.class).getRow())
                        .put("column", entity.getComponent(PositionComponent.class).getColumn())
                );
                entity.removeFromWorld();
            }
            else
                PVZApp.removePlant(entity);
        }
    }

    private void finished(){
        if(entity.isActive()){
            entity.removeFromWorld();
        }
    }
}
