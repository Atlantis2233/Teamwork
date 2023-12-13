package com.zrt.pvz.ui;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.dsl.FXGL;
import com.zrt.pvz.PVZApp;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import com.almasb.fxgl.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * @author 曾瑞庭
 * @Description:
 * @date 2023/12/12 23:08
 */
public class RewardScene extends SubScene {
    private static final int APP_WIDTH = FXGL.getAppWidth();
    private static final int APP_HEIGHT = FXGL.getAppHeight();

    public RewardScene() {
        // 背景图片
        ImageView background = new ImageView(FXGL.image("ui/levelEnd/RewardBg.jpg"));
        background.setFitWidth(APP_WIDTH);
        background.setFitHeight(APP_HEIGHT);
        getContentRoot().getChildren().add(background);

        // 中央的图片
        PVZApp app = (PVZApp) (FXGL.getApp());
        ImageView plantImageView = new ImageView(app.getRewardPlantImage());
        plantImageView.setFitWidth(75); //大小，可调
        plantImageView.setFitHeight(100);
        plantImageView.setTranslateX((APP_WIDTH - plantImageView.getFitWidth()) / 2.0); // 位置，可调
        plantImageView.setTranslateY((APP_HEIGHT - plantImageView.getFitHeight()) / 3.0);
        getContentRoot().getChildren().add(plantImageView);

        //按钮
        ImageButton continueBtn = new ImageButton("levelEnd/NextLevel", 158, 48, () ->{
            FXGL.getSceneService().popSubScene();
            FXGL.<PVZApp>getAppCast().continueNextLevel();
        });
        ImageButton mainMenuBtn = new ImageButton("levelEnd/MainMenu", 111, 27, () -> {
            FXGL.getSceneService().popSubScene();
            FXGL.getGameController().gotoMainMenu();
        });
        //按钮位置，可调
        continueBtn.setLayoutY(400);
        continueBtn.setLayoutX((APP_WIDTH - continueBtn.getWidth()) / 2.0);
        mainMenuBtn.setLayoutX(700);
        mainMenuBtn.setLayoutY(10);
        getContentRoot().getChildren().add(mainMenuBtn);
        getContentRoot().getChildren().add(continueBtn);

    }
}
