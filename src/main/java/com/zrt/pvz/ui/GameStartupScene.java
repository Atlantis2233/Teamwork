package com.zrt.pvz.ui;

import com.almasb.fxgl.app.scene.StartupScene;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.Texture;
import javafx.animation.PauseTransition;
import javafx.scene.SubScene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * @author 曾瑞庭
 * @Description:
 * @date 2023/11/22 13:19
 */
public class GameStartupScene extends StartupScene {
    public static boolean flag=true;
    public GameStartupScene(int appWidth, int appHeight){
        super(appWidth, appHeight);
        StackPane pane = new StackPane(new ImageView(getClass().getResource("/assets/textures/ui/startUp/bg(1).jpg").toExternalForm()));
        pane.setPrefSize(appWidth, appHeight);

        ImageButton loading=new ImageButton("startUp/loading", 331, 94,()->{
            flag=false;
        });
        loading.setLayoutX(250);
        loading.setLayoutY(400);
        getContentRoot().getChildren().addAll(pane,loading);
    }

    @Override
    public void onCreate() {

    }
}
