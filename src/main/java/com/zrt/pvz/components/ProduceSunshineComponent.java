package com.zrt.pvz.components;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.time.LocalTimer;
import com.zrt.pvz.EntityType;
import com.zrt.pvz.PVZApp;
import com.zrt.pvz.data.AnimationData;
import com.zrt.pvz.data.BulletData;
import com.zrt.pvz.data.PlantData;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 曾瑞庭
 * @Description: 生成阳光组件
 * @date 2023/12/11 9:49
 */
public class ProduceSunshineComponent extends Component {
    private LocalTimer produceTimer;
    private Duration produceRate;
    private BulletData bulletData;
    private PlantData plantData;
    private Point2D plantPosition;
    private int row;
    private int column;
    private static Point2D sunshineCollectPoint;
    private double brightness = 0.0;
    private double brightnessDelta = 0.05;//初始亮度和亮度增加值，可调
    private double contrast=0.0;
    private double contrastDelta = 0.05;//初始对比度和对比度增加值，可调
    private boolean reverseAnimation = false; // 是否执行反向动画
    private Timeline timeline; // 将 Timeline 定义为类的成员变量

    @Override
    public void onAdded() {
        plantData = entity.getObject("plantData");
        row=entity.getComponent(PositionComponent.class).getRow();
        column=entity.getComponent(PositionComponent.class).getColumn();
        plantPosition = entity.getPosition();
        produceRate = Duration.seconds(plantData.attackRate());
        bulletData = plantData.bulletData();
        produceTimer = FXGL.newLocalTimer();
        produceTimer.capture();
        sunshineCollectPoint=PVZApp.getSunshineCollectPoint();
    }

    @Override
    public void onUpdate(double tpf) {
        if (!produceTimer.elapsed(produceRate)) {
            return;
        }
        produceSunshine();
    }

    private void produceSunshine() { //生成阳光，可以调整一下速度、范围啥的接近一下原版的抛物线效果
        // 创建时间轴以增加亮度效果
        timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.1), event -> {
                    // 增加亮度
                    brightness += brightnessDelta;
                    contrast+=contrastDelta;
                    if (brightness >= 0.4 ) {
                        brightnessDelta = -brightnessDelta; // 在边界时改变方向
                        contrastDelta=-contrastDelta;
                        spawnSunshine();
                    }
                    if(brightness<=0.0&&reverseAnimation){
                        brightnessDelta=-brightnessDelta;
                        contrastDelta=-contrastDelta;
                        timeline.stop();
                    }

                    // 更新效果
                    updateBrightness(brightness,contrast);
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        reverseAnimation=false;
        produceTimer.capture();
    }

    public void spawnSunshine(){
        if(entity==null)return;
        reverseAnimation=true;
        Point2D point2D=new Point2D(FXGLMath.random(entity.getX()-10,entity.getX()+30)//生成阳光的范围，可调
                , entity.getY()+ entity.getHeight()/2);
        Entity sunshine= FXGL.getGameWorld().spawn("sunShine",point2D.add(0,-entity.getHeight()));
        sunshine.getComponent(MoveComponent.class).moveFromTo(sunshine.getPosition()
                ,point2D,0.5);//速度数值可调
        sunshine.getViewComponent().addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() != MouseButton.PRIMARY) {
                return;
            }
            sunshine.removeComponent(ExpireCleanComponent.class);
            sunshine.getComponent(MoveComponent.class).moveFromTo(sunshine.getPosition()
                    , sunshineCollectPoint,4.0);//速度数值也可以调
            FXGL.play("points.wav");
        });

    }

    // 更新亮度效果
    private void updateBrightness(double brightness,double contrast) {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(brightness);
        colorAdjust.setContrast(contrast);
        if(entity!=null){
            entity.getViewComponent().getChildren().get(0).setEffect(colorAdjust);
        }

    }


}
