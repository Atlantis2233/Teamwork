package com.zrt.pvz.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.zrt.pvz.PVZApp;
import com.zrt.pvz.data.PlantData;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Collections;

/**
 * @author 曾瑞庭
 * @Description:
 * @date 2023/12/11 21:07
 */
public class ShovelComponent extends Component {
    private final ToggleButton btn;

    public ShovelComponent() {
        btn = new ToggleButton();
        btn.setToggleGroup(FXGL.<PVZApp>getAppCast().getPlantBtnGroup());
        btn.setPrefSize(71, 35);
    }

    @Override
    public void onAdded() {
        btn.setStyle("-fx-background-color: #0000;");
        entity.getViewComponent().addChild(btn);

        btn.selectedProperty().addListener((ob, ov, nv) -> {
            FXGL.set("selectedPlantName", nv ? "shovel" : "");
            if (nv) {
                entity.getViewComponent().clearChildren();
                entity.getViewComponent().addChild(FXGL.texture("shovel/withoutShovelBg.png"));
                FXGL.play("shovel.wav");
                entity.getViewComponent().addChild(btn);
            } else {
                entity.getViewComponent().clearChildren();
                entity.getViewComponent().addChild(FXGL.texture("shovel/ShovelBg.png"));
                entity.getViewComponent().addChild(btn);
            }
        });
    }
}
