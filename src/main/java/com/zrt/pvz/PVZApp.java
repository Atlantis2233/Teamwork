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
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.Texture;
import com.zrt.pvz.components.BuildIndicatorComponent;
import com.zrt.pvz.data.ConfigData;
import com.zrt.pvz.data.PlantData;
import com.zrt.pvz.data.ZombieData;
import com.zrt.pvz.ui.GameStartupScene;
import com.zrt.pvz.ui.MainMenu;
import javafx.animation.*;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author 曾瑞庭
 * @Description:
 * @date 2023/11/22 12:47
 */
public class PVZApp extends GameApplication {

    public static Entity emptyView;

    public static Point2D point2D;
    public static Texture texture;
    private final ToggleGroup plantBtnGroup = new ToggleGroup();
    private Entity buildIndicator;
    private Rectangle2D space=new Rectangle2D(220,60,650,450);
    private BuildIndicatorComponent buildIndicatorComponent;
    private Entity emptyEntity;
    private boolean canBuilder;
    private HashMap<String, PlantData> plantMap = new HashMap<>();
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
            String plantName = FXGL.gets("selectedPlantName");
            //如果没有选择植物.那么返回
            if (plantName.isEmpty()) {
                return;
            }
            //如果选择了植物,那么移动的时候,动态显示位置
            mouseMove(plantName);
        });
        FXGL.getInput().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            //右键取消选择
//            if (e.getButton() == MouseButton.SECONDARY) {
//                FXGL.set("selectedTowerName", "");
//                Toggle selectedToggle = towerBtnGroup.getSelectedToggle();
//                if (selectedToggle != null) {
//                    selectedToggle.setSelected(false);
//                }
//                hideIndicator();
//                return;
//            }
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
        if (FXGL.geti("gold") < plantData.cost()) {
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
        emptyEntity.setX(x);
        emptyEntity.setY(y);
        List<Entity> towers = FXGL.getGameWorld().getEntitiesByType(EntityType.PLANT);
        emptyEntity.getBoundingBoxComponent().clearHitBoxes();
        emptyEntity.getBoundingBoxComponent().addHitBox(new HitBox(BoundingShape.box(plantData.width(), plantData.height())));
        boolean temp = true;
        for (Entity tower : towers) {
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
        if (FXGL.geti("gold") < plantData.cost()) {
            return;
        }
        Point2D p = FXGL.getInput().getMousePositionWorld();
        double x = p.getX() - plantData.width() / 2.0;
        double y = p.getY() - plantData.height() / 2.0;
        //FXGL.play("placed.wav");
        FXGL.spawn("plant", new SpawnData(x, y).put("plantData", plantData));
        FXGL.inc("gold", -plantData.cost());
        FXGL.set("selectedPlantName", "");
        Toggle selectedToggle = plantBtnGroup.getSelectedToggle();
        if (selectedToggle != null) {
            selectedToggle.setSelected(false);
        }
        hideIndicator();
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("gold", 1000);
        vars.put("kill", 0);
        vars.put("complete", false);
        vars.put("health", ConfigData.INIT_HP);
        vars.put("selectedPlantName", "");
        vars.put("enemyPreview", "");
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

        spawnEntities();

        spawnEnemy();
    }

    private void hideIndicator() {
        buildIndicator.setX(-1000);
        buildIndicator.setY(-1000);
    }

    private void spawnEntities() {
        //建造指示器的创建
        buildIndicator = FXGL.spawn("buildIndicator");
        hideIndicator();
        buildIndicatorComponent = buildIndicator.getComponent(BuildIndicatorComponent.class);

        //用于检测的碰撞的不可见实体
        emptyEntity = FXGL.spawn("empty");
        emptyEntity.setX(-100);
        emptyEntity.setY(-100);
    }

    private void spawnEnemy() {
        ZombieData zombieData = FXGL.getAssetLoader().loadJSON("data/NormalZombie.json", ZombieData.class).get();

        Point2D point2D = new Point2D(700,300);
        FXGL.runOnce(() -> {
            FXGL.run(() -> {
                FXGL.spawn("zombie", new SpawnData(point2D).put("zombieData", zombieData));
            }, Duration.seconds(5),5);
        }, Duration.seconds(10));
    }
    @Override
    protected void initPhysics() {
        super.initPhysics();
    }

    @Override
    protected void initUI() {
        super.initUI();
    }

    public void StartShow(){
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
