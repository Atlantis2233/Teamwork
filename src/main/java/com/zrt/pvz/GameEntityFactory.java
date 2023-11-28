package com.zrt.pvz;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.dsl.components.LiftComponent;
import com.almasb.fxgl.dsl.views.ScrollingBackgroundView;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.entity.components.TimeComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.Texture;
import com.zrt.pvz.components.*;
import com.zrt.pvz.data.ZombieData;
import com.zrt.pvz.data.PlantData;
import javafx.animation.TranslateTransition;
import javafx.geometry.Orientation;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.util.ArrayList;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

/**
 * @author 曾瑞庭
 * @Description:
 * @date 2023/11/23 21:28
 */
public class GameEntityFactory implements EntityFactory {

    private static AnimationChannel ac;
    private static AnimatedTexture at;
    private static TranslateTransition tt;

    @Spawns("bg")
    public Entity newBg(SpawnData data){
        ScrollingBackgroundView bg=new ScrollingBackgroundView(FXGL.image("map/map1.jpg",1232,533),
                1232,533, Orientation.HORIZONTAL);

//        Texture bg=new Texture(FXGL.image("map/map1.jpg",1232,533));


        return FXGL.entityBuilder(data)
                .view(bg)
                .build();
    }
    
    @Spawns("emptyView")
    public Entity newEmptyView(SpawnData data){
        return FXGL.entityBuilder(data)
                .with(new LiftComponent().
                        xAxisDistanceDuration(400,Duration.seconds(3)))
                .build();
    }

    @Spawns("startZombieShow")
    public Entity newStartZombieShow(SpawnData data){
        ArrayList<Image> imageArrayList=new ArrayList<>();
        for(int i=0;i<5;i++){
            imageArrayList.add(FXGL.image(String.format("zombie/NormalZombie/ZombieShow/ZombieShow_%d.png",i)));
        }
        for(int i=4;i>=0;i--){
            imageArrayList.add(FXGL.image(String.format("zombie/NormalZombie/ZombieShow/ZombieShow_%d.png",i)));
        }
        ac=new AnimationChannel(imageArrayList,Duration.seconds(0.8));
        at=new AnimatedTexture(ac);
        at.loop();

        return FXGL.entityBuilder(data)
                .view(at)
                .with(new ExpireCleanComponent(Duration.seconds(10)))
                .build();
    }

    @Spawns("weeder")
    public Entity newWeeder(SpawnData data){
        Texture weederTexture = FXGL.texture("weeder/weeder.png",60,50);
        double x= data.getX();
        tt = new TranslateTransition(Duration.seconds(0.5), weederTexture);
        tt.setFromX(x-50);
        tt.setToX(x);
        tt.play();
        return FXGL.entityBuilder(data)
                .view(weederTexture)
                .bbox(BoundingShape.box(weederTexture.getWidth(), weederTexture.getHeight()))
                .build();
    }
    
    
    @Spawns("chosenBg")
    public Entity newChosenBg(SpawnData data){
        Texture chosenBgTexture = FXGL.texture("ui/choose/chosenBg.png",400,80);
        double y= data.getY();
        tt = new TranslateTransition(Duration.seconds(0.5), chosenBgTexture);
        tt.setFromY(y-48);
        tt.setToY(y);
        tt.play();

        return FXGL.entityBuilder(data)
                .view(chosenBgTexture)
                .build();
    }

    @Spawns("plantButton")
    public Entity newPlantButton(SpawnData data){
        PlantData plantData=data.get("plantData");
        Texture texture = FXGL.texture("ui/choose/"+plantData.name()+".png",45,60);

        return entityBuilder(data)
                .with(new IrremovableComponent())
                .view(texture)
                .with(new PlantButtonComponent())
                .build();
    }

    /**
     * 建造指示器; 一个图标在中间
     * @param data
     * @return
     */
    @Spawns("buildIndicator")
    public Entity newBuildIndicator(SpawnData data) {
        return entityBuilder(data)
                .with(new BuildIndicatorComponent())
                .zIndex(Integer.MAX_VALUE)
                .build();
    }

    /**
     * 空白的实体, 仅仅用于检测碰撞; 比如炮塔移动,就把空白实体移动到对应的位置,进行碰撞检测
     * 如果在可以建造的区域,并且不和周围的炮塔碰撞,那么此刻就能建造炮塔
     */
    @Spawns("empty")
    public Entity newEmpty(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(EntityType.EMPTY)
                .collidable()
                .neverUpdated()
                .build();
    }

    @Spawns("plant")
    public Entity newPlant(SpawnData data){
        PlantData plantData = data.get("plantData");

//        Texture texture=new Texture(FXGL.image(plantData.icon()));
        return FXGL.entityBuilder(data)
                .type(EntityType.PLANT)
                .bbox(BoundingShape.box(plantData.width(), plantData.height()))
                .collidable()
                .with(new PlantComponent())
                .build();
    }
    
    
    @Spawns("bullet")
    public Entity newBullet(SpawnData data){
        return entityBuilder(data)
                
                .build();
    }

    /**
     * 僵尸 数据 存储在 data/zombie_.json 文件里
     * 僵尸不同运动方向的图片
     * @param data
     * @return
     */
    @Spawns("zombie")
    public Entity newZombie(SpawnData data) {
        ZombieData zombieData = data.get("zombieData");
        return FXGL.entityBuilder(data)
                .type(EntityType.ZOMBIE)
                //减速特效,需要时间组件
                .with(new TimeComponent())
                .with(new EffectComponent())
                .collidable()
                .bbox(BoundingShape.box(zombieData.getWidth(), zombieData.getHeight()))
                .with(new ZombieComponent())
                .build();
    }
}
