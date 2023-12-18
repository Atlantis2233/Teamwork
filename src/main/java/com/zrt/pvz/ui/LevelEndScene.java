package com.zrt.pvz.ui;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.core.util.LazyValue;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import com.zrt.pvz.PVZApp;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * @author zrt
 *
 * 结束时候的场景:
 *    可与显示玩家的得分, 通关用时, 几星通关等;
 *    因为偷懒, 并没有计算积分, 只是显示按钮
 *    1. 回到主菜单按钮
 *    2. 如果通过,显示继续(下一关)按钮; 显示通过的图片
 *       如果没有通过,那么显示重玩 按钮; 显示未通过的图片
 *
 */
public class LevelEndScene extends SubScene {
    private static final int APP_WIDTH = FXGL.getAppWidth();
    private static final int APP_HEIGHT = FXGL.getAppHeight();
    private LazyValue<RewardScene> rewardSceneLazyValue =
            new LazyValue<>(RewardScene::new);

    public LevelEndScene() {
        Rectangle whiteScreen = new Rectangle(APP_WIDTH, APP_HEIGHT, Color.WHITE);
        getContentRoot().getChildren().add(whiteScreen);

        FadeTransition whiteScreenFade = new FadeTransition(Duration.seconds(5), whiteScreen);
        whiteScreenFade.setFromValue(0.0);
        whiteScreenFade.setToValue(1.0);
        whiteScreenFade.setOnFinished(event -> {
            // 在屏幕变白动画完成后执行的操作，例如切换到下一个场景
            // 这里是示例，实际应用需要替换成你的场景切换逻辑
            FXGL.getSceneService().popSubScene();
            FXGL.getSceneService().pushSubScene(rewardSceneLazyValue.get()); // 切换到下一个场景
        });

        whiteScreenFade.play();
    }

    private LevelEndScene createNextScene() {
        return new LevelEndScene(); // 创建下一个场景，这里是示例，需要根据实际逻辑进行替换
    }
}
