package com.zrt.pvz.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.zrt.pvz.PVZApp;
import com.zrt.pvz.data.AnimationData;
import com.zrt.pvz.data.PlantData;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
/**
 * @author 曾瑞庭
 * @Description:
 * @date 2023/12/18 18:21
 */
public class ShroomComponent extends Component {
    private AnimationChannel animNormal,animSleep;

    private AnimatedTexture at;
    private PlantData plantData;
    private Point2D plantPosition;
    private boolean coffee=false;
    @Override
    public void onAdded(){
        plantData = entity.getObject("plantData");
        List<AnimationData> animationData = plantData.animationData();
        for(AnimationData ad:animationData){
            if(ad.status().equalsIgnoreCase("normal")){
                animNormal = initAc(ad);
            }
            else if(ad.status().equalsIgnoreCase("sleep")){
                animSleep = initAc(ad);
            }
        }
        PVZApp pvzApp=(PVZApp) FXGL.getApp();
        if(pvzApp.getLevelData().type().equals("night")){
            at=new AnimatedTexture(animNormal);
            plantPosition = entity.getPosition();
            entity.getViewComponent().addChild(at);
            at.loopAnimationChannel(animNormal);
            if(plantData.components().contains("ShootComponent")){
                entity.addComponent(new ShootComponent());
            }
        }
        else if(pvzApp.getLevelData().type().equals("day")){
            at=new AnimatedTexture(animSleep);
            plantPosition = entity.getPosition();
            entity.getViewComponent().addChild(at);
            at.loopAnimationChannel(animSleep);
        }
    }
    public void onUpdate(double tpf){
        if(coffee){
            at.loopAnimationChannel(animNormal);
            if(plantData.components().contains("ShootComponent")){
                entity.addComponent(new ShootComponent());
            }
            coffee=false;
        }
    }
    private AnimationChannel initAc(AnimationData at) {
        ArrayList<Image> imageArrayList=new ArrayList<>();
        for(int i=0;i<at.FrameNumber();i++){
            imageArrayList.add(FXGL.image(String.format(at.imageName(),i)));
        }
        return new AnimationChannel(imageArrayList, Duration.seconds(at.channelDuration()));
    }

}