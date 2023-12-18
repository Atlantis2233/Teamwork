package com.zrt.pvz.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
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
public class PowerBeatButtonComponent extends Component {
    private final ToggleButton btn;
    private int latestPowerBeatNum;
    public PowerBeatButtonComponent() {
        btn = new ToggleButton();
        btn.setToggleGroup(FXGL.<PVZApp>getAppCast().getPlantBtnGroup());
        btn.setPrefSize(120, 40);
        latestPowerBeatNum=2;
    }

    @Override
    public void onUpdate(double tpf) {
        if(FXGL.geti("powerBeat")==latestPowerBeatNum){
            return;
        }
        latestPowerBeatNum=FXGL.geti("powerBeat");
        entity.getViewComponent().clearChildren();
        Texture texture=FXGL.texture("powerBeat/powerBeatBg("+latestPowerBeatNum+").png",120,40);
        entity.getViewComponent().addChild(texture);
        entity.getViewComponent().addChild(btn);
    }

    @Override
    public void onAdded() {
        btn.setStyle("-fx-background-color: #0000;");
        entity.getViewComponent().addChild(btn);

        btn.selectedProperty().addListener((ob, ov, nv) -> {
            if(FXGL.geti("powerBeat")<=0)return;
            FXGL.set("selectedPlantName", nv ? "powerBeat" : "");
            //下面的逻辑还没写，需要调整能量豆个数
            if (nv) {

            } else {

            }
        });
    }
}
