package com.zrt.pvz;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.*;
import com.almasb.fxgl.dsl.views.ScrollingBackgroundView;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.entity.components.TimeComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.Texture;
import com.zrt.pvz.components.*;
import com.zrt.pvz.data.*;
import javafx.animation.TranslateTransition;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
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

    private static ArrayList<Image> imageArrayList=new ArrayList<>();
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
        imageArrayList.clear();
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
        Label sunshine=new Label("0");
        sunshine.setLayoutX(20);
        sunshine.setLayoutY(60);
        Font font=Font.loadFont(getClass().getResource("/fonts/fzyh.ttf").toExternalForm(), 15);
        sunshine.setFont(font);
        sunshine.textProperty().bind(FXGL.getip("sunshine").asString());

        return FXGL.entityBuilder(data)
                .view(chosenBgTexture)
                .view(sunshine)
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
                .zIndex(Integer.MAX_VALUE-1)
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
                .view(new Rectangle(2,2,Color.BLACK))
                .zIndex(Integer.MAX_VALUE)
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
                .with(new PositionComponent(data.get("row"),data.get("column")))
                .with(new PlantComponent())
                .build();
    }
    
    
    @Spawns("bullet")
    public Entity newBullet(SpawnData data){
        BulletData bulletData = data.get("bulletData");
        return FXGL.entityBuilder(data)
                .type(EntityType.BULLET)
                .collidable()
                .viewWithBBox(bulletData.imageName())
                .with(new ProjectileComponent(
                        data.get("dir"),
                        bulletData.speed()))
                .with(new OffscreenCleanComponent())
                .with(new BulletComponent())
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
                .with(new PositionComponent(data.get("row"),10))
                .with(new ZombieComponent())
                .build();
    }


    //生成地图对应空间用于检测
    @Spawns("mapPoint")
    public Entity newMapPoint(SpawnData data){
        MapPointData mapPointData=data.get("mapPointData");
        double w=mapPointData.getWidth();
        double h=mapPointData.getHeight();
        return entityBuilder(data)
                .type(EntityType.MAPPOINT)
                //.view(new Rectangle(w,h,Color.web("#00000030")))
                .with(new PositionComponent(mapPointData.getRow(), mapPointData.getColumn()))
                .bbox(BoundingShape.box(w,h))
                .collidable()
                .build();
    }

    //生成放置植物时的预览
    @Spawns("plantPreview")
    public Entity newPlantPreview(SpawnData data){
        PlantData plantData = data.get("plantData");
        Texture texture=FXGL.texture(plantData.icon());
        texture.setOpacity(0.5);
        return entityBuilder(data)
                .type(EntityType.PLANTPREVIEW)
                .with(new PositionComponent(data.get("row"),data.get("column")))
                .view(texture)
                .build();
    }

    @Spawns("sunShine")
    public Entity newSunShine(SpawnData data){
        imageArrayList.clear();
        for(int i=0;i<29;i++){
            imageArrayList.add(FXGL.image(String.format("plant/SunShine/SunShine_%d.png",i)));
        }
        ac=new AnimationChannel(imageArrayList,Duration.seconds(3.0));
        at=new AnimatedTexture(ac);
        at.loop();
        return entityBuilder(data)
                .type(EntityType.SUNSHINE)
                .viewWithBBox(at)
                .collidable()
                .with(new MoveComponent())
                .with(new ExpireCleanComponent(Duration.seconds(13.0)))
                .build();
    }

    @Spawns("sunshineCollect")
    public Entity newSunshineCollect(SpawnData data){
        return entityBuilder(data)
                .type(EntityType.SUNSHINECOLLECT)
                .zIndex(Integer.MAX_VALUE)
                .bbox(BoundingShape.box(10,10))
                .collidable()
                .build();
    }
}
