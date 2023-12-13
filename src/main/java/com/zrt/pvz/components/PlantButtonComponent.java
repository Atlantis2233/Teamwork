package com.zrt.pvz.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.Texture;
import com.zrt.pvz.PVZApp;
import com.zrt.pvz.data.PlantData;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * @author 曾瑞庭
 * @Description:
 * @date 2023/11/26 21:34
 */
public class PlantButtonComponent extends Component {
    private final ToggleButton btn;
    private PlantData plantData;
    private Rectangle rectangle,rectangleBG;
    private double height=60,weight=47;
    private int CD;//要加入data
    private double startTime,elapsedTime;
    private boolean cooling=true,afford=false;
    private int cost;
    public PlantButtonComponent() {
        btn = new ToggleButton();
        btn.setToggleGroup(FXGL.<PVZApp>getAppCast().getPlantBtnGroup());
        btn.setPrefSize(weight, height);
    }

    @Override
    public void onAdded() {
        startTime=FXGL.getGameTimer().getNow();
        //挡板变暗效果
        Color transparentBlack = Color.web("#00000090");
        Color transparentGrey = Color.web("#00000099");
        rectangle=new Rectangle(weight,height,transparentBlack);
        rectangleBG=new Rectangle(weight,height,transparentGrey);
        rectangle.setVisible(false);
        rectangleBG.setVisible(false);
        entity.getViewComponent().addChild(rectangle);
        entity.getViewComponent().addChild(rectangleBG);
        btn.setStyle("-fx-background-color: #0000;");
        this.plantData = entity.getObject("plantData");
        entity.getViewComponent().addChild(btn);
        cost= plantData.cost();
        CD=plantData.CD();
//        btn.setDisable(FXGL.geti("sunhine") < towerData.cost());
//        FXGL.getip("sunshine").addListener((ob, ov, nv) -> {
//            btn.setDisable(FXGL.geti("sunshine") < towerData.cost());
//        });

        btn.selectedProperty().addListener((ob, ov, nv) -> {
            if(cost>FXGL.geti("sunshine"))
                afford=false;
            else
                afford=true;
            if(!FXGL.gets("selectedPlantName").equals("build")&&!cooling&&afford)
                FXGL.set("selectedPlantName", nv ? plantData.name() : "");
            if (nv&&afford) {
                rectangle.setVisible(true);
                rectangleBG.setVisible(true);
            } else {
                if(FXGL.gets("selectedPlantName").equals("build"))
                {
                    rectangle.setVisible(true);
                    rectangleBG.setVisible(true);
                    startTime=FXGL.getGameTimer().getNow();
                    cooling=true;
                    FXGL.set("selectedPlantName","");
                }
                else if(!cooling){
                    rectangle.setVisible(false);
                    rectangleBG.setVisible(false);
                }

            }
        });
        //鼠标移入时显示植物详情。。。
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            //FXGL.<PVZApp>getAppCast().showDetailPane(entity.getX() - 275, entity.getY() - 2, towerData);
        });
        //鼠标移除时隐藏详情页面
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            //FXGL.<TowerDefenseApp>getAppCast().hideDetailPane();
        });
    }
    @Override
    public void onUpdate(double tpf) {
        if(cost>FXGL.geti("sunshine")){
            afford=false;
            rectangleBG.setVisible(true);
        }
        else{
            afford=true;
            rectangleBG.setVisible(false);
        }
        if(cooling)
        {
            elapsedTime=FXGL.getGameTimer().getNow()-startTime;
            if(elapsedTime>CD)
            {
                rectangle.setVisible(false);
                if(afford)
                    rectangleBG.setVisible(false);
                rectangle.setHeight(height);
                cooling=false;
            }
            else
            {
                rectangle.setVisible(true);
                rectangleBG.setVisible(true);
                rectangle.setHeight(height-height*elapsedTime/CD);
            }
        }
    }
}

