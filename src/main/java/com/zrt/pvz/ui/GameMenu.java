package com.zrt.pvz.ui;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.Texture;
import javafx.beans.binding.Bindings;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.geto;
import static com.almasb.fxgl.dsl.FXGL.texture;
import static java.lang.Long.SIZE;

/**
 * @author 曾瑞庭
 * @Description:
 * @date 2023/11/22 20:33
 */
public class GameMenu extends FXGLMenu {
    public GameMenu(MenuType type) {
        super(type);
        ImageView iv = new ImageView(FXGL.image("ui/gameMenu/Menu.png",412,483)); //可调
        ImageButton Return=new ImageButton("gameMenu/return", 360, 110,
                this::fireResume);
        ImageButton Restart=new ImageButton("gameMenu/restart", 209, 48,
                 this::startNewGame);
        ImageButton MainMenu=new ImageButton("gameMenu/mainMenu", 209, 47,
                this::goToMainMenu);

        Return.setLayoutY(10);
        Return.setLayoutX(300);
        Restart.setLayoutY(230);
        Restart.setLayoutX(383);
        MainMenu.setLayoutY(70);
        MainMenu.setLayoutX(380);
        StackPane pane = new StackPane(iv);
        getContentRoot().getChildren().addAll(pane,Return,MainMenu,Restart);
    }

    public void startNewGame() {
        getController().startNewGame();
    }

    public void goToMainMenu() {
        getController().gotoMainMenu();
    }
}
