package com.zrt.pvz;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.*;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.time.TimerAction;
import com.zrt.pvz.components.*;
import com.zrt.pvz.data.*;
import com.zrt.pvz.ui.GameStartupScene;
import com.zrt.pvz.ui.MainMenu;
import javafx.animation.*;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.*;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author 曾瑞庭
 * @Description:
 * @date 2023/11/22 12:47
 */
public class PVZApp extends GameApplication {

    public static Entity emptyView;
    private static String plantName;
    public static Point2D point2D;
    public static Texture texture;
    private final ToggleGroup plantBtnGroup = new ToggleGroup();
    private Entity buildIndicator;
    private Rectangle2D space=new Rectangle2D(220,60,650,450);
    private BuildIndicatorComponent buildIndicatorComponent;
    private Entity emptyEntity;
    private boolean canBuilder;
    private HashMap<String, PlantData> plantMap = new HashMap<>();
    private Entity plantPreview;
    private TimerAction spawnSunShineTimerAction;// 生成阳光的定时器
    private Random random=new Random();
    private static final Point2D sunshineCollectPoint =new Point2D(200,10);//收集阳光的位置，可调
    private Entity sunshineCollect;
    private static final List<Point2D> bornPoints= Arrays.asList(
            new Point2D(850, 30),
            new Point2D(850, 115),
            new Point2D(850, 200),
            new Point2D(850, 285),
            new Point2D(850, 370)
    ); //用来记录僵尸出生点的数据，可调，方便使用行号初始化僵尸出现
    private TimerAction spawnEnemyTimerAction;  //生成僵尸的定时器
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Plants Vs Zombies");
        settings.setVersion("0.0");
        settings.setWidth(750);
        settings.setHeight(533);
        settings.setAppIcon("logo.jpg");
        settings.setMainMenuEnabled(true);
        settings.setSceneFactory(new SceneFactory() {

            @Override
            public FXGLMenu newMainMenu() {
                return new MainMenu();
            }

            @Override
            public StartupScene newStartup(int width, int height) {
                return new GameStartupScene(width, height);
            }

//            @Override
//            public LoadingScene newLoadingScene() {
//                return new GameLoadingScene();
//            }
        });
    }

    @Override
    protected void onPreInit() {
        super.onPreInit();
    }

    @Override
    protected void initInput() {
        FXGL.getInput().addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
            plantName = FXGL.gets("selectedPlantName");
            //如果没有选择植物.那么返回
            if (plantName.isEmpty()) {
                return;
            }
            //如果选择了植物,那么移动的时候,动态显示位置
            mouseMove(plantName);
        });
        FXGL.getInput().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            //右键取消选择
            if (e.getButton() == MouseButton.SECONDARY) {
                FXGL.set("selectedTowerName", "");
                Toggle selectedToggle = plantBtnGroup.getSelectedToggle();
                if (selectedToggle != null) {
                    selectedToggle.setSelected(false);
                }
                hideIndicator();
                return;
            }
            String plantName = FXGL.gets("selectedPlantName");
            if (plantName.isEmpty() || !canBuilder) {
                return;
            }
            //如果选择了炮塔, 那么建造炮塔
            buildPlant(plantName);
        });
    }

    private void mouseMove(String plantName) {
        canBuilder = false;
        PlantData plantData = plantMap.get(plantName);
        if (plantData == null) {
            return;
        }
        if (FXGL.geti("sunshine") < plantData.cost()) {
            return;
        }
        buildIndicatorComponent.updateIndicator(plantName);
        int w = plantData.width();
        int h = plantData.height();
        Point2D p = FXGL.getInput().getMousePositionWorld();
        //鼠标点击的位置是炮塔的中心
        double x = p.getX() - w / 2.0;
        double y = p.getY() - h / 2.0;
        buildIndicator.setX(x);
        buildIndicator.setY(y);
        boolean flag = false;

        if(buildIndicator.isWithin(space)){
            flag = true;
        }
        if (!flag) {
            buildIndicatorComponent.canBuild(false);
            return;
        }
        emptyEntity.setX(p.getX());
        emptyEntity.setY(p.getY());
        //       List<Entity> towers = FXGL.getGameWorld().getEntitiesByType(EntityType.PLANT);
        emptyEntity.getBoundingBoxComponent().clearHitBoxes();
        emptyEntity.getBoundingBoxComponent().addHitBox(new HitBox(BoundingShape.box(1,1)));
//        boolean temp = true;
//        for (Entity tower : towers) {
//            //判断可以建造的地方是否有其他炮塔
//            if (emptyEntity.isColliding(tower)) {
//                temp = false;
//                break;
//            }
//        }
        canBuilder = true;
        buildIndicatorComponent.canBuild(canBuilder);
    }

    private void buildPlant(String plantName) {
        PlantData plantData = plantMap.get(plantName);
        if (plantData == null) {
            return;
        }
        if (FXGL.geti("sunshine") < plantData.cost()) {
            return;
        }
        Point2D p = FXGL.getInput().getMousePositionWorld();
        double x=plantPreview.getX();
        double y=plantPreview.getY();
        int row=plantPreview.getComponent(PositionComponent.class).getRow();
        int column=plantPreview.getComponent(PositionComponent.class).getColumn();
        FXGL.play("placed.wav");
        List<Entity> plants = FXGL.getGameWorld().getEntitiesByType(EntityType.PLANTPREVIEW);
        for(Entity entity:plants){
            entity.removeFromWorld();
        }
        FXGL.spawn("plant", new SpawnData(x, y)
                .put("plantData", plantData)
                .put("row",row)
                .put("column",column));
        FXGL.inc("sunshine", -plantData.cost());
        FXGL.set("selectedPlantName", "");
        Toggle selectedToggle = plantBtnGroup.getSelectedToggle();
        if (selectedToggle != null) {
            selectedToggle.setSelected(false);
        }
        hideIndicator();
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("sunshine", 1000);
        vars.put("kill", 0);
        vars.put("complete", false);
        vars.put("health", ConfigData.INIT_HP);
        vars.put("selectedPlantName", "");
        vars.put("enemyHp", 0);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new GameEntityFactory());

        spawn("bg");
        emptyView=spawn("emptyView",0,FXGL.getAppHeight()/2);
        getGameScene().getViewport().bindToEntity(emptyView,
                0,FXGL.getAppHeight()/2);


        StartShow();

        loadLevel();
    }

    private void hideIndicator() {
        buildIndicator.setX(-1000);
        buildIndicator.setY(-1000);
    }


    private void loadLevel() {
        Toggle selectedToggle = plantBtnGroup.getSelectedToggle();
        if (selectedToggle != null) {
            selectedToggle.setSelected(false);
        }
        //地图构建
        MapPointData mapPointData=FXGL.getAssetLoader().loadJSON("data/MapPoint.json", MapPointData.class).get();
        point2D =new Point2D(mapPointData.getStartPointX(),mapPointData.getStartPointY());
        Point2D tmp=new Point2D(mapPointData.getStartPointX(),mapPointData.getStartPointY());
        for(int i=0;i<mapPointData.getNumsY();i++){
            point2D=tmp;
            for(int j=0;j<mapPointData.getNumsX();j++){
                mapPointData.setRow(i);
                mapPointData.setColumn(j);
                spawn("mapPoint", new SpawnData(point2D).put("mapPointData", mapPointData));
                point2D= point2D.add(mapPointData.getIntervalX(),0);
            }
            tmp= tmp.add(0,mapPointData.getIntervalY());
        }

        //清理集合
        //spaceInfos.clear();
        //pointInfos.clear();
        //初始化数据与设置
        FXGL.set("kill", 0);
        FXGL.set("health", ConfigData.INIT_HP);
        FXGL.set("selectedTowerName", "");

        //读取关卡数据
        //levelData = FXGL.getAssetLoader().loadJSON("levels/level" + FXGL.geti("level") + ".json", LevelData.class).get();
        //FXGL.set("levelData", levelData);

        //设置当前关卡
//        String map = levelData.map();
//        FXGL.setLevelFromMap(map);

        //设置初始金币
        FXGL.set("sunshine", 1000);
        //生成ui相关的实体
        spawnEntities();
        //生成敌军实体
        spawnEnemy();
    }
    private void spawnEntities() {
        //建造指示器的创建
        buildIndicator = FXGL.spawn("buildIndicator");
        hideIndicator();
        buildIndicatorComponent = buildIndicator.getComponent(BuildIndicatorComponent.class);

        //用于检测的碰撞的不可见实体
        emptyEntity = spawn("empty");
        emptyEntity.setX(-100);
        emptyEntity.setY(-100);

        //生成收集阳光处的碰撞体，这里调整一下坐标，可调
        sunshineCollect=spawn("sunshineCollect",sunshineCollectPoint);

        //用于产生阳光的定时器, 定期尝试产生阳光
        spawnSunShineTimerAction = run(() -> {
            point2D=new Point2D(random.nextDouble()*630.0+220.0,
                    random.nextDouble()*415.0+80.0);//生成阳光的区间，可调成变量
            Entity sunshine= spawn("sunShine",new Point2D(point2D.getX(),20));
            sunshine.getComponent(MoveComponent.class).moveFromTo(sunshine.getPosition()
                    ,point2D,0.005);//速度数值也可以调
            sunshine.getViewComponent().addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
                if (event.getButton() != MouseButton.PRIMARY) {
                    return;
                }
                sunshine.getComponent(MoveComponent.class).moveFromTo(sunshine.getPosition()
                        , sunshineCollectPoint,0.015);//速度数值也可以调
            });
        }, Duration.seconds(7.5));//这个7.5是个大概值，之后可以设置为关卡变量存储，方便调整
    }

    private void spawnEnemy() {
        ZombieData zombieData = FXGL.getAssetLoader().loadJSON("data/NormalZombie.json", ZombieData.class).get();

        Point2D point2D = new Point2D(700,300);
        FXGL.runOnce(() -> {
            //用于产生敌人的定时器, 定期尝试产生僵尸,
            spawnEnemyTimerAction = run(() -> {
                //随机抽取数组的一个坐标
                int tmp=random.nextInt(5);
                Point2D point2Dtmp= bornPoints.get(tmp);
                //如果可以产生僵尸,那么生成僵尸
                spawn("zombie",
                        new SpawnData(point2Dtmp)
                                .put("zombieData", zombieData)
                                .put("row",tmp));

            }, Duration.seconds(10.0));//僵尸生成时间，可调，建议后期与阳光生成时间等关卡变量调成一些统一的变量
        }, Duration.seconds(10)); //延迟时间（延迟多久后僵尸开始生成，可调）

    }
    @Override
    protected void initPhysics() {
        //实现植物放置时的预览
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.EMPTY,EntityType.MAPPOINT) {
            @Override
            protected void onCollisionBegin(Entity empty, Entity mapPoint) {
                List<Entity> plants = FXGL.getGameWorld().getEntitiesByType(EntityType.PLANTPREVIEW);
                for(Entity entity:plants){
                    entity.removeFromWorld();
                }
                PlantData plantData = plantMap.get(plantName);
                if (plantData == null) {
                    return;
                }
                point2D=new Point2D(mapPoint.getCenter().getX()-plantData.width()/2,mapPoint.getCenter().getY()-plantData.height()/2);
                plantPreview=spawn("plantPreview",new SpawnData(point2D)
                        .put("plantData", plantData)
                        .put("row",mapPoint.getComponent(PositionComponent.class).getRow())
                        .put("column",mapPoint.getComponent(PositionComponent.class).getColumn()));
            }


        });

        //判断阳光与阳光收集的碰撞，实现一个缩小淡入动画
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.SUNSHINE,EntityType.SUNSHINECOLLECT) {
            @Override
            protected void onCollisionBegin(Entity sunshine, Entity sunshineCollect) {
                sunshine.getViewComponent().clearChildren();
                texture=FXGL.texture("plant/SunShine/SunShine_0.png");
                ScaleTransition st=new ScaleTransition(Duration.seconds(.35)
                        ,texture);
                st.setToX(0);
                st.setToY(0);

                FadeTransition ft=new FadeTransition(Duration.seconds(.35)
                        ,texture);
                ft.setToValue(0);

                ParallelTransition pt=new ParallelTransition(st,ft);
                pt.play();
                sunshine.getViewComponent().addChild(texture);
                pt.setOnFinished(event->{
                    sunshine.removeFromWorld();
                    inc("sunshine",25);
                });


            }
        });

        //子弹打中敌人
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BULLET, EntityType.ZOMBIE) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity zombie) {
                ZombieComponent zombieComponent = zombie.getComponent(ZombieComponent.class);
                if (zombieComponent.isDead()) {
                    return;
                }
                zombieComponent.attacked(bullet.getObject("bulletData"));
                bullet.removeFromWorld();
            }
        });

        //僵尸吃植物
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLANT, EntityType.ZOMBIE) {
            @Override
            protected void onCollisionBegin(Entity plant, Entity zombie) {
                PlantComponent plantComponent=plant.getComponent(PlantComponent.class);
                ZombieComponent zombieComponent = zombie.getComponent(ZombieComponent.class);
                plantComponent.attacked(zombie.getObject("zombieData"));
                if(plantComponent.isDead()){
                    zombieComponent.unAttack();
                }
                zombieComponent.attack();
            }

            @Override
            protected void onCollisionEnd(Entity plant, Entity zombie) {
                PlantComponent plantComponent=plant.getComponent(PlantComponent.class);
                ZombieComponent zombieComponent = zombie.getComponent(ZombieComponent.class);
                plantComponent.unAttacked();
                zombieComponent.unAttack();
            }
        });
    }

    @Override
    protected void initUI() {
        super.initUI();
    }

    public void StartShow(){
        //清除前面关卡的东西
        if (spawnEnemyTimerAction != null) {
            spawnEnemyTimerAction.expire();
            spawnEnemyTimerAction = null;
        }
        if (spawnSunShineTimerAction != null) {
            spawnSunShineTimerAction.expire();
            spawnSunShineTimerAction = null;
        }
        //销毁空视角让其停下
        emptyView.addComponent(new ExpireCleanComponent(Duration.seconds(4.75)));

        //生成一开始用于展示本关僵尸的僵尸

        for(int i=0;i<6;i++){
            point2D = new Point2D(FXGLMath.random(950.0,1100.0),FXGLMath.random(20.0,450.0));
            spawn("startZombieShow", new SpawnData(point2D));
        }


        point2D =new Point2D(85,100);
        emptyView.setOnNotActive(()->{
            //生成小推车
            point2D =new Point2D(85,100);
            for(int i=0;i<5;i++){
                spawn("weeder",point2D);
                point2D= point2D.add(0,85);
            }

            //准备 安放 植物!
            PrepareText();

            //选择植物的背景板
            point2D=new Point2D(180,-1);
            spawn("chosenBg",point2D);

            //植物卡片


            point2D=new Point2D(260,10);
            spawn("plantButton",new SpawnData(point2D).put("plantData", loadPlantData("SunFlower")));
            point2D=point2D.add(50,0);
            spawn("plantButton",new SpawnData(point2D).put("plantData", loadPlantData("PeaShooter")));
            point2D=point2D.add(50,0);
            spawn("plantButton",new SpawnData(point2D).put("plantData",loadPlantData("WallNut")));
//            point2D=point2D.add(50,0);
//            spawn("plantButton",new SpawnData(point2D).put("name","WallNut"));
        });

    }

    public void PrepareText(){
        texture = texture("prepareText/prepare.png");

        Entity entity1= entityBuilder()
                .at(getAppCenter().getX(),getAppCenter().getY()-texture.getHeight()/2)
                .view(texture)
                .buildAndAttach();
        ScaleTransition st=new ScaleTransition(Duration.seconds(0.5), texture);

        st.setFromX(0.5);
        st.setToX(1);
        st.setFromY(0.5);
        st.setToY(1);
        st.play();
        st.setOnFinished(event->{
            entity1.removeFromWorld();
            texture = texture("prepareText/puton.png");
            Entity entity2= entityBuilder()
                    .at(getAppCenter().getX(),getAppCenter().getY()-texture.getHeight()/2)
                    .view(texture)
                    .buildAndAttach();
            ScaleTransition st1=new ScaleTransition(Duration.seconds(0.5), texture);

            st1.setFromX(0.5);
            st1.setToX(1);
            st1.setFromY(0.5);
            st1.setToY(1);
            st1.play();
            st1.setOnFinished(event1->{
                entity2.removeFromWorld();
                texture = texture("prepareText/plant.png");
                Entity entity3= entityBuilder()
                        .at(getAppCenter().getX(),getAppCenter().getY()-texture.getHeight()/2)
                        .view(texture)
                        .buildAndAttach();
                ScaleTransition st2=new ScaleTransition(Duration.seconds(0.5), texture);

                st2.setFromX(0.5);
                st2.setToX(1);
                st2.setFromY(0.5);
                st2.setToY(1);
                st2.play();
                st2.setOnFinished(event2->entity3.removeFromWorld());
            });
        });

    }

    public ToggleGroup getPlantBtnGroup() {
        return plantBtnGroup;
    }
    private PlantData loadPlantData(String tName) {
        PlantData plantData = FXGL.getAssetLoader().loadJSON("data/" + tName + ".json", PlantData.class).get();
        plantMap.put(plantData.name(), plantData);
        return plantData;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
