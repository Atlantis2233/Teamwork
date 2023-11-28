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

    public PlantButtonComponent() {
        btn = new ToggleButton();
        btn.setToggleGroup(FXGL.<PVZApp>getAppCast().getPlantBtnGroup());
        btn.setPrefSize(45, 60);
    }

    @Override
    public void onAdded() {
        Color transparentBlack = Color.web("#00000090");
        Rectangle rectangle=new Rectangle(45,60,transparentBlack);
        rectangle.setVisible(false);
        entity.getViewComponent().addChild(rectangle);

        btn.setStyle("-fx-background-color: #0000;");
        this.plantData = entity.getObject("plantData");
        entity.getViewComponent().addChild(btn);
//        btn.setDisable(FXGL.geti("gold") < towerData.cost());
//        FXGL.getip("gold").addListener((ob, ov, nv) -> {
//            btn.setDisable(FXGL.geti("gold") < towerData.cost());
//        });

        btn.selectedProperty().addListener((ob, ov, nv) -> {
            FXGL.set("selectedPlantName", nv ? plantData.name() : "");
            if (nv) {
                rectangle.setVisible(true);
            } else {
                rectangle.setVisible(false);
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
}
