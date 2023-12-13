package com.zrt.pvz.ui;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.Texture;
import javafx.animation.PauseTransition;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author 曾瑞庭
 * @Description:
 * @date 2023/11/22 13:20
 */
public class MainMenu extends FXGLMenu {

    public MainMenu() {
        super(MenuType.MAIN_MENU);
        ImageView iv = new ImageView(FXGL.image("ui/mainMenu/bg.png",FXGL.getAppWidth(),FXGL.getAppHeight()));
        Texture smallGame= texture("ui/mainMenu/smallgame.png",300,112);
        Texture puzzle=texture("ui/mainMenu/puzzle.png",260,110);
        ImageButton adventure=new ImageButton("mainMenu/adventure(1)", 310, 130,
                this::startNewGame);
        smallGame.setLayoutY(150);
        smallGame.setLayoutX(380);
        puzzle.setLayoutY(230);
        puzzle.setLayoutX(383);
        adventure.setLayoutY(70);
        adventure.setLayoutX(380);
        getContentRoot().getChildren().addAll(iv, smallGame,puzzle,adventure);
    }

    @Override
    public void onCreate() {
        FXGL.loopBGM("mainMenu.wav");
    }

    @Override
    protected void onUpdate(double tpf) {
    }

    public void startNewGame() {
        Image image=FXGL.image("ui/mainMenu/ZombieHand.png");

        AnimatedTexture at=new AnimatedTexture(new AnimationChannel(image
                , Duration.seconds(1.5),7));
        at.setOnRotationFinished(event->{
            getController().startNewGame();
        });
        at.play();
        Pane pane=new Pane(at);
        getContentRoot().getChildren().addAll(at);
        getController().startNewGame();
    }
}
