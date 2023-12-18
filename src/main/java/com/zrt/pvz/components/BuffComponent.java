package com.zrt.pvz.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import com.zrt.pvz.data.PlantData;
import javafx.util.Duration;

/**
 * @author 曾瑞庭
 * @Description:  用来给对应植物添加对应buff（释放技能）
 * @date 2023/12/18 14:32
 */
public class BuffComponent extends Component {
    private PlantData plantData;

    @Override
    public void onAdded() {
        plantData=entity.getObject("plantData");
    }

    @Override
    public void onUpdate(double tpf) {
        super.onUpdate(tpf);
    }

    //释放技能函数，根据对对应植物编写
    public void CastSkill(){

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
        //坚果墙，恢复满血，并血量上限翻倍，可调
        else if(plantData.name().equals("WallNut")){
            PlantComponent plantComponent=(PlantComponent) entity.getComponent(PlantComponent.class);
            plantComponent.SetHp(2);
            Texture texture= FXGL.texture("plant/WallNut/InfiNut.png",65,80);
            entity.getViewComponent().clearChildren();
            entity.getViewComponent().addChild(texture);
        }
    }
}
