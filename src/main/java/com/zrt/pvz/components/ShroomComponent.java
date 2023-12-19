package com.zrt.pvz.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.time.LocalTimer;
import com.zrt.pvz.PVZApp;
import com.zrt.pvz.data.AnimationData;
import com.zrt.pvz.data.PlantData;
import com.zrt.pvz.data.StatusData;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 曾瑞庭
 * @Description:
 * @date 2023/12/18 18:21
 */
public class ShroomComponent extends Component {
    private Map<String,AnimationChannel> animMap = new HashMap<>();
    //private AnimationChannel animNormal,animSleep;
    private AnimatedTexture at;
    private PlantData plantData;
    private Point2D plantPosition;
    private String type;
    private String plantname;
    private StatusData statusData;
    private Duration prepareDuration;
    private LocalTimer timer;
    private boolean cangrow=false;
    private boolean begintimer=false;
    @Override
    public void onAdded(){
        plantData = entity.getObject("plantData");
        plantname = plantData.name();
        List<AnimationData> animationData = plantData.animationData();
        for(AnimationData ad:animationData){
            animMap.put(ad.status(),initAc(ad));
        }
        PVZApp pvzApp=(PVZApp) FXGL.getApp();
        if(pvzApp.getLevelData().type().equals("night")){
            this.type="normal";
            at=new AnimatedTexture(animMap.get("normal"));
            plantPosition = entity.getPosition();
            entity.getViewComponent().addChild(at);
            shroomappear(type);
        }
        else if(pvzApp.getLevelData().type().equals("day")){
            this.type="sleep";
            at=new AnimatedTexture(animMap.get("sleep"));
            plantPosition = entity.getPosition();
            entity.getViewComponent().addChild(at);
            shroomappear(type);
        }
        if(plantname.equals("SunShroom")){
            cangrow = true;
            statusData=plantData.statusData();
            prepareDuration = Duration.seconds(statusData.changeCondition().get(0));
        }
    }
    public void onUpdate(double tpf){
        if(cangrow&&type.equals("normal")){
            if(!begintimer){
                timer = FXGL.newLocalTimer();
                timer.capture();
                begintimer=true;
            }
            else{
                if(timer.elapsed(prepareDuration)){
                    changeStatus("big");
                    FXGL.play("plantgrow.wav");
                    cangrow=false;
                }
            }
        }
    }
    private AnimationChannel initAc(AnimationData at) {
        ArrayList<Image> imageArrayList=new ArrayList<>();
        for(int i=0;i<at.FrameNumber();i++){
            imageArrayList.add(FXGL.image(String.format(at.imageName(),i),at.width(),at.height()));
        }
        return new AnimationChannel(imageArrayList, Duration.seconds(at.channelDuration()));
    }
    public void changeStatus(String type){
        if(this.type.equals(type))return;
        this.type=type;
        animMap.replace("normal",animMap.get(type));
        at.loopAnimationChannel(animMap.get("normal"));
    }
    public void shroomappear(String type){
        if(type.equals("sleep")){
            at.loopAnimationChannel(animMap.get("sleep"));
        }
        else if(type.equals("normal")){
            at.loopAnimationChannel(animMap.get("normal"));
            if(plantData.components().contains("ShootComponent")){
                entity.addComponent(new ShootComponent());
            }
            if(plantData.components().contains("ProduceSunshineComponent")){
                entity.addComponent(new ProduceSunshineComponent());
            }
            if(plantData.components().contains("BombComponent")){
                entity.addComponent(new BombComponent());
            }
        }
    }
    public void setCoffee(){
        type="normal";
        shroomappear(type);
    }
}