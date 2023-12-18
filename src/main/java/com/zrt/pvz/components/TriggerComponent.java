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
import com.zrt.pvz.data.PlantData;
import com.zrt.pvz.data.TriggerData;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

//修改
public class TriggerComponent extends Component {
    private String effect;
    //判断是否为一次性，false为一次性
    private boolean isReusable;
    private boolean isPlant = false,isTrigger = false;
    private boolean isTriggered = false,isCreated = false,isSetUnprepared = false;
    private AnimatedTexture texture;
    private AnimationChannel animUnprepared,animPrepared, animBeingTriggered;
    private LocalTimer triggerTimer;
    private Duration prepareDuration,CD;
    private PlantData plantData;
    private TriggerData triggerData;
    private Entity attachedPlant;
    private Point2D position;

    @Override
    public void onAdded(){
        if(entity.isType(EntityType.PLANT)){
            isPlant = true;
            plantData = entity.getObject("plantData");
            triggerData = plantData.triggerData();
        }
        else if(entity.isType(EntityType.TRIGGER)){
            isTrigger = true;
            triggerData = entity.getObject("triggerData");
            attachedPlant = entity.getObject("attachedPlant");
        }

        isReusable = triggerData.isReusable();
        effect = triggerData.effect();
        prepareDuration = Duration.seconds(triggerData.prepareDuration());
        CD = Duration.seconds(triggerData.CD());
        if(isPlant){
            List<AnimationData> animationData = plantData.animationData();
            for(AnimationData ad:animationData){
                if(ad.status().equalsIgnoreCase("unprepared")){
                    animUnprepared = initAc(ad);
                }
                else if(ad.status().equalsIgnoreCase("prepared")){
                    animPrepared = initAc(ad);
                }
                else if(ad.status().equalsIgnoreCase("beingTriggered")){
                    animBeingTriggered = initAc(ad);
                }
            }
            if(triggerData.prepareDuration()==0){
                texture = new AnimatedTexture(animPrepared);
                entity.getViewComponent().addChild(texture);
                texture.loopAnimationChannel(animPrepared);
                createTrigger();
            }
            else {
                texture = new AnimatedTexture(animUnprepared);
                entity.getViewComponent().addChild(texture);
                texture.loopAnimationChannel(animUnprepared);
            }
        }
        triggerTimer = FXGL.newLocalTimer();
        triggerTimer.capture();
    }

    @Override
    public void onUpdate(double tpf){
        if(isPlant){
            if(!isTriggered){
                if(!triggerTimer.elapsed(prepareDuration)||triggerData.prepareDuration()==0){
                    return;
                }
            }
            else if(!isReusable||!triggerTimer.elapsed(CD)){
                return;
            }
            if(!isCreated){
                createTrigger();
            }
        }
    }

    private AnimationChannel initAc(AnimationData at) {
        ArrayList<Image> imageArrayList=new ArrayList<>();
        for(int i=0;i<at.FrameNumber();i++){
            imageArrayList.add(FXGL.image(String.format(at.imageName(),i)));
        }
        return new AnimationChannel(imageArrayList,Duration.seconds(at.channelDuration()));
    }

    private void createTrigger(){
        isCreated = true;
        texture.loopAnimationChannel(animPrepared);
        FXGL.spawn("trigger",new SpawnData(entity.getCenter().subtract(triggerData.offsetX(),triggerData.offsetY()))
                .put("triggerData",triggerData)
                .put("attachedPlant",entity)
        );

    }

    public void triggered(){
        if(isPlant){
            isTriggered = true;
            isSetUnprepared = false;
            texture.playAnimationChannel(animBeingTriggered);
            if(triggerData.effect().equals("bomb")){
                entity.addComponent(new BombComponent());
                texture.setOnCycleFinished(()->PVZApp.removePlant(entity));
            }
            else if(triggerData.effect().equals("kill")){

                if(!isReusable){
                    texture.setOnCycleFinished(()-> PVZApp.removePlant(entity));
                }
                else {
                    texture.setOnCycleFinished(()->setUnprepared());
                    triggerTimer.capture();
                    isCreated = false;
                }
            }
        }
        else if(isTrigger){
            attachedPlant.getComponent(TriggerComponent.class).triggered();
            entity.removeFromWorld();
        }
    }

    private void setUnprepared(){
        if(!isSetUnprepared){
            texture.loopAnimationChannel(animUnprepared);
        }
        isSetUnprepared = true;
    }
}
