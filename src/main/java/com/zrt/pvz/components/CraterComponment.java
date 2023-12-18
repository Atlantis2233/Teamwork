package com.zrt.pvz.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxgl.time.Timer;
import com.zrt.pvz.PVZApp;
import javafx.util.Duration;

public class CraterComponment extends Component {
    private LocalTimer timer;
    private Duration statusRemain = Duration.seconds(10);
    private Texture texture0, texture1;
    private int status = 0;

    @Override
    public void onAdded() {
        texture0 = FXGL.texture("ui/crater/crater.png");
        texture1 = FXGL.texture("ui/crater/crater_fading.png");
        texture0.setLayoutX(150);
        texture0.setLayoutY(190);
        texture1.setLayoutX(150);
        texture1.setLayoutY(190);
        entity.getViewComponent().clearChildren();
        entity.getViewComponent().addChild(texture0);
        System.out.println(status);
        status = 0;
        timer = FXGL.newLocalTimer();
        timer.capture();
    }

    @Override
    public void onUpdate(double tpf) {
        if (timer.elapsed(statusRemain) && texture0 != null && status == 0) {
            entity.getViewComponent().removeChild(texture0);
            entity.getViewComponent().addChild(texture1);
            status++;
            System.out.println(status);
            timer.capture();
        }
        if (status == 1 && timer.elapsed(statusRemain) && texture1 != null) {
            PVZApp.removePlant(entity);
        }
    }
}