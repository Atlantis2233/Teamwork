package com.zrt.pvz.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.HealthDoubleComponent;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimationChannel;
import com.zrt.pvz.EntityType;
import com.zrt.pvz.data.AnimationData;
import com.zrt.pvz.data.PlantData;
import com.zrt.pvz.data.StatusData;
import com.zrt.pvz.data.ZombieData;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.util.ArrayList;

public class StatusChangeComponent extends Component {
    private boolean isPlant=false,isZombie=false;
    private PlantData plantData;
    private ZombieData zombieData;
    private StatusData statusData;
    private HealthIntComponent hp;

    @Override
    public void onAdded(){
        if(entity.isType(EntityType.PLANT)){
            isPlant=true;
            plantData=entity.getObject("plantData");
            statusData = plantData.statusData();
            if(statusData.type().equals("hp")){
                hp = entity.getComponent(PlantComponent.class).getHp();
            }
        }
        else if(entity.isType(EntityType.ZOMBIE)){
            isZombie=true;
            zombieData=entity.getObject("zombieData");
            statusData = zombieData.getStatusData();
            if(statusData.type().equals("hp")){
                hp = entity.getComponent(ZombieComponent.class).getHp();
            }
        }

    }

    @Override
    public void onUpdate(double tpf){
        if(statusData.type().equals("hp")){
            for(int i=statusData.numOfChange()-1;i>=0;i--){
                if(hp.getValue()<=hp.getMaxValue()*statusData.changeCondition().get(i)){
                    if(isPlant){
                        entity.getComponent(PlantComponent.class).changeStatus(i+1);
                    }
                    else if(isZombie){
                        entity.getComponent(ZombieComponent.class).changeStatus(i+1);
                    }
                    break;
                }
            }
        }


    }


}
