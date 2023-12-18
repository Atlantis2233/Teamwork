package com.zrt.pvz;

import com.almasb.fxgl.dsl.EntityBuilder;
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
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

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

    private static PlantData imitatorData=FXGL.getAssetLoader().loadJSON("data/plant/Imitator.json", PlantData.class).get();

    //地图背景
    @Spawns("map")
    public Entity newMap(SpawnData data){
        LevelData levelData=data.get("levelData");
        ScrollingBackgroundView bg=new ScrollingBackgroundView(FXGL.image(levelData.map() , levelData.width(), FXGL.getAppHeight()),
                levelData.width(),FXGL.getAppHeight() ,Orientation.HORIZONTAL);
//        Texture bg=new Texture(FXGL.image("map/map1.jpg",1232,533));

        return FXGL.entityBuilder(data)
                .view(bg)
                .build();
    }
    
    @Spawns("emptyView")
    public Entity newEmptyView(SpawnData data){
        return FXGL.entityBuilder(data)
                .type(EntityType.EMPTYVIEW)
                //.with(new LiftComponent().xAxisDistanceDuration(400,Duration.seconds(3)))
                .with(new MoveComponent())
                .build();
    }

    @Spawns("startZombieShow")
    public Entity newStartZombieShow(SpawnData data){
        ZombieData zombieData = data.get("zombieData");
        List<AnimationData> animationData = zombieData.getAnimationData();
        imageArrayList.clear();
        for (AnimationData a : animationData){
            if (a.status().equalsIgnoreCase("show")) {
                for(int i=0;i<a.FrameNumber();i++){
                    imageArrayList.add(FXGL.image(String.format(a.imageName(),i)));
                }
                ac = new AnimationChannel(imageArrayList,Duration.seconds(a.channelDuration()));
            }
        }
        at=new AnimatedTexture(ac);
        at.loop();
        return FXGL.entityBuilder(data)
                .type(EntityType.CHOSENBG)
                .view(at)
                //.with(new ExpireCleanComponent(Duration.seconds(10)))
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
                .type(EntityType.WEEDER)
                .view(weederTexture)
                .bbox(BoundingShape.box(120, 50))//由于一开始移动过weeder位置，所以要设大一点bbox，可调
                .collidable()
                .build();
    }
    
    
    @Spawns("chosenBg")
    public Entity newChosenBg(SpawnData data){
        Texture chosenBgTexture = FXGL.texture("ui/choose/chosenBg.png",400,80);
        double y= data.getY();
        tt = new TranslateTransition(Duration.seconds(0.3), chosenBgTexture);
        tt.setFromY(y-48);
        tt.setToY(y);
        tt.play();
        Label sunshine=new Label("0");
        tt = new TranslateTransition(Duration.seconds(0.3), sunshine);
        tt.setFromY(15);
        tt.setToY(60);
        tt.play();
        sunshine.setLayoutX(25);
        Font font=Font.loadFont(getClass().getResource("/fonts/fzyh.ttf").toExternalForm(), 15);
        sunshine.setFont(font);
        sunshine.textProperty().bind(FXGL.getip("sunshine").asString());

        return FXGL.entityBuilder(data)
                .type(EntityType.CHOSENBG)
                .view(chosenBgTexture)
                .view(sunshine)
                .build();
    }
    @Spawns("chooseplant")
    public Entity newChooseplant(SpawnData data){
        Texture chooseplantTexture = FXGL.texture("ui/choose/ChoosePlant.png",450,530);
        return FXGL.entityBuilder(data)
                .type(EntityType.CHOSENBG)
                .view(chooseplantTexture)
                .build();
    }

    @Spawns("plantButton")
    public Entity newPlantButton(SpawnData data){
        PlantData plantData=data.get("plantData");
        Texture texture = FXGL.texture("ui/choose/"+plantData.name()+".png",45,60);
        double y= data.getY();
        tt = new TranslateTransition(Duration.seconds(0.3),texture);
        tt.setFromY(y-55);
        tt.setToY(y-10);
        tt.play();
        return entityBuilder(data)
                .with(new IrremovableComponent())
                .view(texture)
                .with(new PlantButtonComponent())
                .build();
    }
    @Spawns("chooseButton")
    public Entity newChooseButton(SpawnData data){
        PlantData plantData=data.get("plantData");
        Texture texture = FXGL.texture("ui/choose/"+plantData.name()+".png",45,60);
        return entityBuilder(data)
                .type(EntityType.CHOOSEBUTTON)
                .view(texture)
                .with(new ChooseButtonComponent())
                .build();
    }
    @Spawns("textureButton")
    public Entity newTextureButton(SpawnData data){
        PlantData plantData=data.get("plantData");
        Texture texture = FXGL.texture("ui/choose/"+plantData.name()+".png",45,60);
        return entityBuilder(data)
                .type(EntityType.TEXTUREBUTTON)
                .view(texture)
                .with(new ChooseButtonComponent())
                .build();
    }

    @Spawns("imitatorPlantButton")
    public Entity newimitatorPlantButton(SpawnData data){
        PlantData plantData=data.get("plantData");
        //把图片变成灰阶
        Texture texture = FXGL.texture("ui/choose/"+plantData.name()+".png",45,60);
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setSaturation(-1);
        texture.setEffect(colorAdjust);
        //修改PlantData
        PlantData newPlantData=new PlantData(plantData.cost(),imitatorData.animationData()
                ,imitatorData.name(),imitatorData.icon(), imitatorData.width()
                , imitatorData.bulletData(),imitatorData.bombData(),imitatorData.triggerData(),
                imitatorData.statusData(),imitatorData.attackRate(), imitatorData.height(),
                imitatorData.hp(),imitatorData.CD(),imitatorData.offsetX()
                , imitatorData.offsetY(),imitatorData.shootInterval(),imitatorData.components());
        data.put("plantData",newPlantData);

        double y= data.getY();
        tt = new TranslateTransition(Duration.seconds(0.3),texture);
        tt.setFromY(y-55);
        tt.setToY(y-10);
        tt.play();
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
                .bbox(BoundingShape.box(plantData.width(),plantData.height()))
                .collidable()
                .with(new PositionComponent(data.get("row"),data.get("column")))
                .with(new PlantComponent())
                .with(new BuffComponent())
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

    @Spawns("bomb")
    public Entity newBomb(SpawnData data){
        BombData bombData = data.get("bombData");
        Texture texture=FXGL.texture(bombData.imageName());
        texture.setScaleX(0.5);
        texture.setScaleY(0.5);
        texture.setTranslateY(20);
        /*Canvas canvas = new Canvas(bombData.width(),bombData.height());
        GraphicsContext g2d = canvas.getGraphicsContext2D();
        g2d.setFill(Color.web("#FFFFFF"));
        g2d.fillRect(0,0,bombData.width(),bombData.height());*/

        return FXGL.entityBuilder(data)
                .type(EntityType.BOMB)
                .collidable()
                .view(bombData.imageName())
                //.view(canvas)
                .bbox(BoundingShape.box(bombData.width(),bombData.height()))
                .with(new BombComponent())
                .build();
    }

    @Spawns("trigger")
    public Entity newTrigger(SpawnData data){
        TriggerData triggerData = data.get("triggerData");

        /*Canvas canvas = new Canvas(triggerData.width(),triggerData.height());
        GraphicsContext g2d = canvas.getGraphicsContext2D();
        g2d.setFill(Color.web("#FFFFFF"));
        g2d.fillRect(0,0,triggerData.width(),triggerData.height());*/

        return FXGL.entityBuilder(data)
                .type(EntityType.TRIGGER)
                .collidable()
                //.view(canvas)
                .bbox(BoundingShape.box(triggerData.width(),triggerData.height()))
                .with(new TriggerComponent())
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
                .bbox(BoundingShape.box(zombieData.getWidth()*0.25, zombieData.getHeight()*0.5))
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
        texture.setScaleX(0.9);
        texture.setScaleY(0.9);
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

    @Spawns("home")
    public Entity newhome(SpawnData data){
        return entityBuilder(data)
                .type(EntityType.HOME)
                .bbox(BoundingShape.box(1,533))
                .collidable()
                .build();
    }
    
    @Spawns("shovel")
    public Entity newShovel(SpawnData data){
        Texture texture=FXGL.texture("shovel/shovelBg.png");
        return entityBuilder(data)
                .with(new IrremovableComponent())
                .view(texture)
                .with(new ShovelComponent())
                .build();
    }

    @Spawns("powerBeatBg")
    public Entity newPowerBeatBg(SpawnData data){
        Texture texture=FXGL.texture("powerBeat/powerBeatBg(2).png",120,40);
        return entityBuilder(data)
                .type(EntityType.POWERBEATCOLLECT)
                .with(new IrremovableComponent())
                .view(texture)
                .with(new PowerBeatButtonComponent())
                .zIndex(Integer.MAX_VALUE)
                .bbox(BoundingShape.box(10,10))
                .collidable()
                .build();
    }

    @Spawns("powerBeat")
    public Entity newPowerBeat(SpawnData data){
        return entityBuilder(data)
                .with(new PowerBeatComponent())
                .with(new ExpireCleanComponent(Duration.seconds(10))) //自动清除
                .build();
    }
    
    @Spawns("movePowerBeat")
    public Entity newMovePowerBeat(SpawnData data){
        imageArrayList.clear();
        for(int i=0;i<7;i++){
            imageArrayList.add(FXGL.image(String.format("powerBeat/powerBeat_%d.png",i)));
        }
        ac=new AnimationChannel(imageArrayList,Duration.seconds(1.0));
        at=new AnimatedTexture(ac);
        at.loop();
        return entityBuilder(data)
                .type(EntityType.MOVEPOWERBEAT)
                .viewWithBBox(at)
                .zIndex(Integer.MAX_VALUE-1)
                .collidable()
                .with(new MoveComponent())
                .with(new ExpireCleanComponent(Duration.seconds(20.0)))
                .build();
    }

    @Spawns("reward")
    public Entity newReward(SpawnData data){
        PlantData plantData=data.get("plantData");
        Texture texture = FXGL.texture("ui/choose/"+plantData.name()+".png",45,60);

        Entity entity=entityBuilder(data)
                .view(texture)
                .with(new MoveComponent())
                .with(new RewardComponent())
                .build();
        return entity;
    }

    @Spawns("gameProgressBar")
    public Entity newGameProgressBar(SpawnData data){
        LevelData levelData=data.get("levelData");
        Entity entity=FXGL.entityBuilder(data)
                .with(new GameProgressBarComponent())
                .zIndex(Integer.MAX_VALUE)
                .build();
        return entity;
    }
}
