package com.zrt.pvz.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.time.LocalTimer;
import com.zrt.pvz.EntityType;
import com.zrt.pvz.PVZApp;
import com.zrt.pvz.data.AnimationData;
import com.zrt.pvz.data.BulletData;
import com.zrt.pvz.data.PlantData;
import com.zrt.pvz.data.ZombieData;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.almasb.fxgl.dsl.FXGL.spawn;

/**
 * @author 曾瑞庭
 * @Description: 模仿者组件
 * @date 2023/12/17 21:38
 */
public class ImitatorComponent extends Component {
    private PlantData plantData;
    private Point2D plantPosition;
    private int row;
    private int column;
    private LocalTimer changeTimer;
    private Duration changeRate=Duration.seconds(2.0); //多久变身（转多久），可调
    private Random random=new Random();
    private boolean start=false;
    @Override
    public void onAdded() {
        plantData = entity.getObject("plantData");
        row=entity.getComponent(PositionComponent.class).getRow();
        column=entity.getComponent(PositionComponent.class).getColumn();
        plantPosition = entity.getPosition();
        changeTimer=FXGL.newLocalTimer();
        FXGL.runOnce(()->{
            ArrayList<Image> imageArrayList=new ArrayList<>();
            List<AnimationData> animationData = plantData.animationData();
            AnimatedTexture at=null;
            AnimationChannel ac;
            for (AnimationData ad : animationData) {
                if (ad.status().equalsIgnoreCase("spin")) {
                    for(int i=0;i<ad.FrameNumber();i++){
                        imageArrayList.add(FXGL.image(String.format(ad.imageName(),i), ad.width(), ad.height()));
                    }
                    ac=new AnimationChannel(imageArrayList,Duration.seconds(ad.channelDuration()));
                    at=new AnimatedTexture(ac);
                }
            }
            at.loop();

            start=true;
            changeTimer.capture();
            entity.getViewComponent().clearChildren();
            entity.getViewComponent().addChild(at);
        },Duration.seconds(1.0)); //过多久后开始变身，可调
    }

    @Override
    public void onUpdate(double tpf) {
        if (!start||!changeTimer.elapsed(changeRate)) {
            return;
        }
        RandomBuildPlant();
    }

    public void RandomBuildPlant(){
        start=false;
        entity.getViewComponent().clearChildren();
        //变身特效动画
        Duration seconds=Duration.seconds(0.3);
        AnimationChannel ac=new AnimationChannel(FXGL.image("plant/Imitator/Effect.png",240,120)
                ,seconds,2);
        AnimatedTexture at=new AnimatedTexture(ac);
        at.play();
        PauseTransition pt=new PauseTransition(seconds);
        ParallelTransition pp=new ParallelTransition(at,pt);
        entity.getViewComponent().addChild(at);
        pp.play();
        pp.setOnFinished(e->{
            PVZApp pvzApp=(PVZApp) FXGL.getApp();
            List<String>allPlantList= pvzApp.getAllPlantList();
            List<String>allZombieList=pvzApp.getAllZombieList();
            //看是生成植物还是僵尸
            if(random.nextBoolean()){
                int randomIndex = random.nextInt(allPlantList.size());
                pvzApp.buildPlant(allPlantList.get(randomIndex),entity.getPosition(),row,column);
                entity.removeFromWorld();
            }
            else{
                int randomIndex = random.nextInt(allZombieList.size());
                ZombieData zombieData= pvzApp.findZombieData(allZombieList.get(randomIndex));
                spawn("zombie",
                        new SpawnData(entity.getPosition())
                                .put("zombieData", zombieData)
                                .put("row",row));
                PVZApp.removePlant(entity);
            }

        });

    }

}
