package com.zrt.pvz.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import com.zrt.pvz.PVZApp;
import com.zrt.pvz.data.PlantData;
import com.zrt.pvz.ui.PowerBeatRecharge;
import javafx.scene.control.Button;
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
    private final Button rechargeBtn;
    private int latestPowerBeatNum;
    public PowerBeatButtonComponent() {
        // 创建按钮
        rechargeBtn = new Button();

        // 设置按钮的透明度
        rechargeBtn.setStyle("-fx-background-color: rgba(0,0,0,0); -fx-text-fill: rgba(255,255,255,0); -fx-border-color: rgba(255,255,255,0);");

        // 设置按钮的大小
        rechargeBtn.setPrefWidth(30);
        rechargeBtn.setPrefHeight(30);
        rechargeBtn.setLayoutX(130);
        rechargeBtn.setLayoutY(25);
        rechargeBtn.setOnAction(e->{
            FXGL.getSceneService().pushSubScene(new PowerBeatRecharge());
        });
        btn = new ToggleButton();
        btn.setToggleGroup(FXGL.<PVZApp>getAppCast().getPlantBtnGroup());
        btn.setPrefSize(160, 60);
        latestPowerBeatNum=2;
    }

    @Override
    public void onUpdate(double tpf) {
        if(FXGL.geti("powerBeat")==latestPowerBeatNum){
            return;
        }
        latestPowerBeatNum=FXGL.geti("powerBeat");
        entity.getViewComponent().clearChildren();
        Texture texture=FXGL.texture("powerBeat/powerBeatBg("+latestPowerBeatNum+").png",180,60);
        entity.getViewComponent().addChild(texture);
        entity.getViewComponent().addChild(btn);
    }

    @Override
    public void onAdded() {
        btn.setStyle("-fx-background-color: #0000;");
        entity.getViewComponent().addChild(btn);
        entity.getViewComponent().addChild(rechargeBtn);
        System.out.println(rechargeBtn.getLayoutX());
        System.out.println(rechargeBtn.getLayoutY());

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
