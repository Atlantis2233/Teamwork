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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * @author 曾瑞庭
 * @Description:
 * @date 2023/12/12 23:08
 */
public class RewardScene extends SubScene {
    private static final int APP_WIDTH = FXGL.getAppWidth();
    private static final int APP_HEIGHT = FXGL.getAppHeight();

    public RewardScene() {
        PVZApp app = (PVZApp) (FXGL.getApp());
        // 背景图片
        ImageView background = new ImageView(FXGL.image("ui/levelEnd/RewardBg.jpg"));
        background.setFitWidth(APP_WIDTH);
        background.setFitHeight(APP_HEIGHT);
        getContentRoot().getChildren().add(background);

        //你得到一株新植物！
        Text text=new Text("你得到一株新植物！");
        Font font=Font.loadFont(getClass().getResource("/fonts/fzjz.ttf").toExternalForm(), 25);
        text.setFont(font);
        text.setFill(Color.web("#D49E2B"));
        text.setTranslateX(275);
        text.setTranslateY(53);
        getContentRoot().getChildren().add(text);

        //introductionName
        Text introductionName=new Text(app.getRewardPlantIntroductionName());
        Font introductionNameFont=Font.loadFont(getClass().getResource("/fonts/fzjz.ttf").toExternalForm(), 20);
        introductionName.setFont(introductionNameFont);
        introductionName.setFill(Color.web("#D39D2A"));
        introductionName.setTranslateX(340);
        introductionName.setTranslateY(290);
        getContentRoot().getChildren().add(introductionName);

        //introduction
        Text introduction=new Text(app.getRewardPlantIntroduction());
        Font introductionFont=Font.loadFont(getClass().getResource("/fonts/fzcq.ttf").toExternalForm(), 15);
        introduction.setFont(introductionFont);
        introduction.setFill(Color.web("#31385C"));
        introduction.setTranslateX(250);
        introduction.setTranslateY(333);
        getContentRoot().getChildren().add(introduction);

        // 中央的图片
        ImageView plantImageView = new ImageView(app.getRewardPlantImage());
        plantImageView.setFitWidth(75); //大小，可调
        plantImageView.setFitHeight(100);
        plantImageView.setTranslateX((APP_WIDTH - plantImageView.getFitWidth()) / 2.0); // 位置，可调
        plantImageView.setTranslateY((APP_HEIGHT - plantImageView.getFitHeight()) / 3.0);
        getContentRoot().getChildren().add(plantImageView);

        //按钮
        ImageButton continueBtn = new ImageButton("levelEnd/NextLevel", 158, 48, () ->{
            FXGL.getSceneService().popSubScene();
            FXGL.getSceneService().popSubScene();
            FXGL.<PVZApp>getAppCast().continueNextLevel();
        });
        ImageButton mainMenuBtn = new ImageButton("levelEnd/MainMenu", 111, 27, () -> {
            FXGL.getSceneService().popSubScene();
            FXGL.getGameController().gotoMainMenu();
        });
        //按钮位置，可调
        continueBtn.setLayoutY(440);
        continueBtn.setLayoutX((APP_WIDTH - continueBtn.getWidth()) / 2.0-80);
        mainMenuBtn.setLayoutX(630);
        mainMenuBtn.setLayoutY(10);
        getContentRoot().getChildren().add(mainMenuBtn);
        getContentRoot().getChildren().add(continueBtn);

    }
}
