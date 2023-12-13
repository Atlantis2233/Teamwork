package com.zrt.pvz;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.*;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.util.LazyValue;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.dsl.components.KeepOnScreenComponent;
import com.almasb.fxgl.dsl.components.OffscreenCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.time.TimerAction;
import com.zrt.pvz.components.*;
import com.zrt.pvz.data.*;
import com.zrt.pvz.ui.GameMenu;
import com.zrt.pvz.ui.GameStartupScene;
import com.zrt.pvz.ui.LevelEndScene;
import com.zrt.pvz.ui.MainMenu;
import javafx.animation.*;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import java.io.*;
import java.util.*;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author 曾瑞庭
 * @Description:
 * @date 2023/11/22 12:47
 */
public class PVZApp extends GameApplication {
    /**
     * 总关卡数
     */
    private static final int MAX_LEVEL = 5;
    /**
     * 开始的关卡
     */
    public static int START_LEVEL = 1;
    public static Entity emptyView;
    public Entity home;
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
    private static Point2D sunshineCollectPoint =new Point2D(200,10);//收集阳光的位置，可调
    private Entity sunshineCollect;
    private Entity selectedPlant=null;
    private boolean shovelCanBuilder;
    private LevelData levelData;
    private Map<String, Integer> zombieMap;//僵尸种类及其权重
    private Map<String,ZombieData>zombieDataMap;//僵尸数据Map
    private Image rewardPlantImage;
    private Point2D lastZombieDiePoint=new Point2D(500,400);

    private LazyValue<LevelEndScene> levelEndSceneLazyValue =
            new LazyValue<>(LevelEndScene::new);
    private List<String>users=new ArrayList<>();

    private static final List<Point2D> bornPoints= Arrays.asList(
            new Point2D(850, 80),
            new Point2D(850, 165),
            new Point2D(850, 250),
            new Point2D(850, 335),
            new Point2D(850, 420)
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
                //读取文件看当前用户是谁
                return new MainMenu();
            }

            @Override
            public StartupScene newStartup(int width, int height) {
                return new GameStartupScene(width, height);
            }

            @Override
            public FXGLMenu newGameMenu() {
                return new GameMenu(MenuType.GAME_MENU);
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

    // 将List对象序列化并写入文件
    private static void serializeList(List<?> list, String fileName) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName))) {
            outputStream.writeObject(list);
            System.out.println("List对象已序列化并写入文件");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 从文件中读取序列化的List对象
    private static List<?> deserializeList(String fileName) {
        List<?> deserializedList = new ArrayList<>();
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName))) {
            deserializedList = (List<?>) inputStream.readObject();
            System.out.println("从文件中反序列化List对象");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return deserializedList;
    }

    public void setLastZombieDiePoint(Point2D lastZombieDiePoint) {
        this.lastZombieDiePoint = lastZombieDiePoint;
    }

    public static Point2D getSunshineCollectPoint() {
        return sunshineCollectPoint;
    }

    @Override
    protected void initInput() {
        FXGL.getInput().addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
            plantName = FXGL.gets("selectedPlantName");
            //如果没有选择植物.那么返回
            if (plantName.isEmpty()||plantName.equals("build")){
                return;
            }
            //如果选择了植物,那么移动的时候,动态显示位置
            if(plantName.equals("shovel")){
                shovelMove();
            }
            else{
                mouseMove(plantName);
            }
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
                List<Entity> plants = FXGL.getGameWorld().getEntitiesByType(EntityType.PLANTPREVIEW);
                for(Entity entity:plants){
                    entity.removeFromWorld();
                }
                return;
            }
            String plantName = FXGL.gets("selectedPlantName");
            if(plantName.equals("shovel")){
                //System.out.println(shovelCanBuilder);
                if(!shovelCanBuilder){
                    return;
                }
                if(selectedPlant!=null){
                    selectedPlant.removeFromWorld();
                }
                FXGL.set("selectedTowerName", "");
                Toggle selectedToggle = plantBtnGroup.getSelectedToggle();
                if (selectedToggle != null) {
                    selectedToggle.setSelected(false);
                }
                hideIndicator();
                shovelCanBuilder=false;
                return;
            }
            if ((plantName.isEmpty()||plantName.equals("build")) || !canBuilder) {
                return;
            }
            //如果选择了植物, 那么建造植物
            buildPlant(plantName);
        });
    }

    private void shovelMove(){
        shovelCanBuilder=false;
        buildIndicatorComponent.updateIndicator("shovel");

        int w = 74;
        int h = 80;
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
        //emptyEntity——用来检测植物指示器的碰撞
        emptyEntity.setX(p.getX());
        emptyEntity.setY(p.getY());
        emptyEntity.getBoundingBoxComponent().clearHitBoxes();
        emptyEntity.getBoundingBoxComponent().addHitBox(new HitBox(BoundingShape.box(1,1)));
        emptyEntity.setType(EntityType.SHOVEL);
        shovelCanBuilder=true;
        buildIndicatorComponent.canBuild(true);
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
        //emptyEntity——用来检测植物指示器的碰撞
        emptyEntity.setX(p.getX());
        emptyEntity.setY(p.getY());
        List<Entity> plants = FXGL.getGameWorld().getEntitiesByType(EntityType.PLANT);
        emptyEntity.getBoundingBoxComponent().clearHitBoxes();
        emptyEntity.getBoundingBoxComponent().addHitBox(new HitBox(BoundingShape.box(1,1)));
        emptyEntity.setType(EntityType.EMPTY);
        boolean temp = true;
        for (Entity tower : plants) {
            //判断可以建造的地方是否有其他炮塔
            if (emptyEntity.isColliding(tower)) {
                temp = false;
                break;
            }
        }
        canBuilder = temp;
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
        Entity plant= FXGL.spawn("plant", new SpawnData(x, y)
                .put("plantData", plantData)
                .put("row",row)
                .put("column",column));
        if(plantData.components().contains("ShootComponent")){
            plant.addComponent(new ShootComponent());
        }
        if(plantData.components().contains("ProduceSunshineComponent")){
            plant.addComponent(new ProduceSunshineComponent());
        }
        FXGL.inc("sunshine", -plantData.cost());
        FXGL.set("selectedPlantName", "build");//指示建造了植物，用来判断CD开始
        Toggle selectedToggle = plantBtnGroup.getSelectedToggle();
        if (selectedToggle != null) {
            selectedToggle.setSelected(false);
        }
        hideIndicator();
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("level", START_LEVEL);
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

        buildAndStartLevel();
    }

    public void buildAndStartLevel(){
        //清除之前的所有对象
        getGameWorld().getEntitiesByType(
                EntityType.BULLET,EntityType.PLANT
                ,EntityType.MAPPOINT,EntityType.SHOVEL
                ,EntityType.PLANTPREVIEW,EntityType.SUNSHINE
                ,EntityType.WEEDER,EntityType.ZOMBIE
        ).forEach(Entity::removeFromWorld);
        home=spawn("home",75,0);
        emptyView=spawn("emptyView",0,FXGL.getAppHeight()/2);
        getGameScene().getViewport().bindToEntity(emptyView,
                0,FXGL.getAppHeight()/2);

        //开场的动画，注意地图在这里生成
        StartShow();
        //加载关卡
        loadLevel();
        //判断关卡是否完成;如果完成,显示完成界面
        checkTheLevelIsEnd();
    }

    private void hideIndicator() {
        buildIndicator.setX(-1000);
        buildIndicator.setY(-1000);
    }
    private void checkTheLevelIsEnd() {
        ChangeListener<Number> numberChangeListener = (ob, ov, nv) -> {
            if (levelData != null && FXGL.geti("kill") == levelData.amount()) {
                FXGL.set("complete", true);
                FXGL.runOnce(() -> {
                    spawn("reward",new SpawnData(lastZombieDiePoint).put("plantData",loadPlantData(levelData.reward())));
                }, Duration.seconds(1));
            }
        };
        FXGL.getip("kill").addListener(numberChangeListener);
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
        //设置初始太阳
        FXGL.set("sunshine", 1000);

        //生成ui相关的实体
        spawnEntities();
        //生成敌军实体
        spawnEnemy();
    }

    /**
     * 挑战失败时,重玩此关
     */
    public void restartLevel() {
        loadLevel();
    }

    /**
     * 继续下一关
     */
    public void continueNextLevel() {
        if (FXGL.geti("level") < MAX_LEVEL) {
            FXGL.inc("level", 1);
        } else {
            FXGL.set("level", 1);
        }
        //加载关卡
        buildAndStartLevel();
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
                    ,point2D,0.5);//速度数值也可以调
            sunshine.getViewComponent().addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
                if (event.getButton() != MouseButton.PRIMARY) {
                    return;
                }
                sunshine.removeComponent(ExpireCleanComponent.class);
                sunshine.getComponent(MoveComponent.class).moveFromTo(sunshine.getPosition()
                        , sunshineCollectPoint,1.5);//速度数值也可以调
            });
        }, Duration.seconds(levelData.produceSunshineInterval()));//这个7.5是个大概值，之后可以设置为关卡变量存储，方便调整
    }

    private void spawnEnemy() {
        zombieMap=levelData.zombieMap();
        //初始化数据Map
        for (String key : zombieMap.keySet()) {
            zombieDataMap.put(key,FXGL.getAssetLoader().loadJSON("data/"+key+".json", ZombieData.class).get());
        }
        //总权重
        int totalWeight = zombieMap.values().stream().mapToInt(Integer::intValue).sum();
        FXGL.runOnce(() -> {
            //用于产生敌人的定时器, 定期尝试产生僵尸,
            spawnEnemyTimerAction = run(() -> {
                int randomNumber = random.nextInt(totalWeight) + 1; // 在总权重范围内生成随机数
                ZombieData zombieData=null;
                int cumulativeWeight = 0;
                for (Map.Entry<String, Integer> entry : zombieMap.entrySet()) {
                    cumulativeWeight += entry.getValue();
                    if (randomNumber <= cumulativeWeight) {
                        zombieData=zombieDataMap.get(entry.getKey());
                        break;
                    }
                }
                //随机抽取数组的一个坐标
                int tmp=random.nextInt(5);
                Point2D point2Dtmp= bornPoints.get(tmp);
                //如果可以产生僵尸,那么生成僵尸
                spawn("zombie",
                        new SpawnData(point2Dtmp)
                                .put("zombieData", zombieData)
                                .put("row",tmp));

            }, Duration.seconds(levelData.interval()));//僵尸生成时间，可调，建议后期与阳光生成时间等关卡变量调成一些统一的变量
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
                if (zombieComponent.isDead() || Math.abs(bullet.getY()-zombie.getY())>5) {
                    lastZombieDiePoint=zombie.getPosition();
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
                int plantrow=plant.getComponent(PositionComponent.class).getRow();
                int zombierow=zombie.getComponent(PositionComponent.class).getRow();
                if(plantrow==zombierow){
                    PlantComponent plantComponent=plant.getComponent(PlantComponent.class);
                    ZombieComponent zombieComponent = zombie.getComponent(ZombieComponent.class);
                    plantComponent.attacked(zombie.getObject("zombieData"));
                    zombieComponent.attack();
                }
            }

            @Override
            protected void onCollisionEnd(Entity plant, Entity zombie) {
                PlantComponent plantComponent=plant.getComponent(PlantComponent.class);
                ZombieComponent zombieComponent = zombie.getComponent(ZombieComponent.class);
                plantComponent.unAttacked();
                if(!zombieComponent.isDead()){
                    zombieComponent.unAttack();
                }
            }
        });

        //僵尸碰到小推车，小推车前进
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.WEEDER,EntityType.ZOMBIE) {
            @Override
            protected void onCollisionBegin(Entity weeder, Entity zombie) {
                if(!weeder.hasComponent(ProjectileComponent.class)){
                    weeder.addComponent(new ProjectileComponent(new Point2D(1,0)
                    ,400));
                    //本来是想写个OffScreenClean组件的，但好像检测有问题，一开始就会被清除掉，所以改成这样了，可调
                    weeder.addComponent(new ExpireCleanComponent(Duration.seconds(2.4)));
                }
                ZombieComponent zombieComponent= zombie.getComponent(ZombieComponent.class);
                zombieComponent.setDead(true);
            }

        });

        //铲子指示器碰到植物，植物变亮
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.SHOVEL, EntityType.PLANT) {
            @Override
            protected void onCollisionBegin(Entity shovel, Entity plant) {
                selectedPlant=plant;
                ColorAdjust color_adjust = new ColorAdjust();

                // 变亮度，可调
                color_adjust.setBrightness(0.3);
                plant.getViewComponent().getChildren().get(0).setEffect(color_adjust);
            }

            @Override
            protected void onCollisionEnd(Entity shovel, Entity plant) {
                selectedPlant=null;
                //结束碰撞，变回去
                ColorAdjust color_adjust = new ColorAdjust();
                color_adjust.setBrightness(0);
                if(!plant.getViewComponent().getChildren().isEmpty()){
                    plant.getViewComponent().getChildren().get(0).setEffect(color_adjust);
                }

            }
        });

        //僵尸进屋
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.HOME, EntityType.ZOMBIE){
            @Override
            protected void onCollisionBegin(Entity home, Entity zombie){
                Point2D point2D1=new Point2D(12,FXGL.getAppHeight()/2);
                Point2D point2D2=new Point2D(170,FXGL.getAppHeight()/2);
                emptyView=spawn("emptyView",170,FXGL.getAppHeight()/2);
                getGameScene().getViewport().bindToEntity(emptyView,
                        0,FXGL.getAppHeight()/2);
                emptyView.getComponent(MoveComponent.class).moveFromTo(point2D2,point2D1,2.5);
                zombie.setY(FXGL.getAppHeight()/2+30);
                zombie.getBoundingBoxComponent().clearHitBoxes();
                FXGL.runOnce(() ->{
                    FXGL.getGameController().pauseEngine();
                    texture = texture("ui/loseGame/EatingYourHead.png");
                    texture.setScaleX(1.506);
                    texture.setScaleY(1.070);
                    Entity entity= entityBuilder()
                            .at(125,75)
                            .view(texture)
                            .buildAndAttach();
                    ScaleTransition st=new ScaleTransition(Duration.seconds(2), texture);
                    st.setFromX(0.5);
                    st.setToX(1);
                    st.setFromY(0.5);
                    st.setToY(1);
                    st.play();
                }, Duration.seconds(0.5));
            }
        });
    }

    public Image getRewardPlantImage() {
        return rewardPlantImage;
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
        zombieMap=new HashMap<>();
        zombieDataMap=new HashMap<>();
        //读取关卡数据
        levelData = FXGL.getAssetLoader().loadJSON("levels/level" + FXGL.geti("level") + ".json", LevelData.class).get();
        FXGL.set("levelData", levelData);
        rewardPlantImage=FXGL.image("ui/choose/"+levelData.reward()+".png",45,60);
        //生成地图
        spawn("map",new SpawnData().put("levelData",levelData));
        //销毁空视角让其停下
        Point2D point2D1=new Point2D(emptyView.getX(),FXGL.getAppHeight()/2);
        Point2D point2D2=new Point2D(emptyView.getX()+465,FXGL.getAppHeight()/2);
        Point2D point2D3=new Point2D(emptyView.getX()+170,FXGL.getAppHeight()/2);
        FXGL.runOnce(() ->{
            emptyView.getComponent(MoveComponent.class).moveFromTo(point2D1,point2D2,2.5);
        }, Duration.seconds(2.8));
        FXGL.runOnce(() ->{
            emptyView.getComponent(MoveComponent.class).moveFromTo(point2D2,point2D3,2.5);
        }, Duration.seconds(6.5));
        emptyView.addComponent(new ExpireCleanComponent(Duration.seconds(8.5)));

        //生成一开始用于展示本关僵尸的僵尸

        for(int i=0;i<6;i++){
            point2D = new Point2D(FXGLMath.random(950.0,1100.0),FXGLMath.random(20.0,450.0));
            spawn("startZombieShow", new SpawnData(point2D));
        }


        point2D =new Point2D(85,100);//可调
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
            point2D=new Point2D(180,-1);//可调
            spawn("chosenBg",point2D);

            //铲子
            point2D=new Point2D(680,-1);//可调
            spawn("shovel",point2D);

            //植物卡片
            point2D=new Point2D(260,10);// 可调
            for(String entry: levelData.plants()){
                spawn("plantButton",new SpawnData(point2D).put("plantData", loadPlantData(entry)));
                point2D=point2D.add(50,0);
            }
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
