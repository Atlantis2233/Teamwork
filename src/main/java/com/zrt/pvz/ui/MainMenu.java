package com.zrt.pvz.ui;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.core.util.LazyValue;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.Texture;
import com.zrt.pvz.PVZApp;
import com.zrt.pvz.data.UserData;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author 曾瑞庭
 * @Description:
 * @date 2023/11/22 13:20
 */
public class MainMenu extends FXGLMenu {
    private TranslateTransition userBoardtt;
    private TranslateTransition changeUsertt;
    private final Pane defaultPane;
    private static Text level;
    private static Text episode;
    private static Text name;
    private  List<String> users;

    public MainMenu(UserData userData, List<String> user) {
        super(MenuType.MAIN_MENU);
        users=user;
        ImageView iv = new ImageView(FXGL.image("ui/mainMenu/bg.png",FXGL.getAppWidth(),FXGL.getAppHeight()));
        //Texture smallGame= texture("ui/mainMenu/smallgame1.png",300,112);
        Texture puzzle=texture("ui/mainMenu/puzzle.png",260,110);
        ImageButton smallGame=new ImageButton("mainMenu/smallGame", 300, 112,
                this::StartAmusementGame);
        ImageButton adventure=new ImageButton("mainMenu/adventure", 310, 118,
                this::startNewGame);
        ImageButton exist=new ImageButton("mainMenu/exist", 59, 26,
                this::exist);
        smallGame.setLayoutY(163);
        smallGame.setLayoutX(380);
        puzzle.setLayoutY(240);
        puzzle.setLayoutX(383);
        adventure.setLayoutY(70);
        adventure.setLayoutX(380);
        exist.setLayoutY(455);
        exist.setLayoutX(668);

        //用户名
        name = new Text(users.get(0)+"!");
        Font font=Font.loadFont(getClass().getResource("/fonts/fzcq.ttf").toExternalForm(), 24);
        name.setFont(font);
        name.setFill(Color.web("#FFFE91"));
        name.setTranslateX(133);
        name.setTranslateY(110);
        name.setVisible(false);

        //关卡
        episode = new Text("1");
        episode.setFont(Font.font("Arial", FontWeight.NORMAL, 15));
        episode.setFill(Color.WHITE);
        episode.setTranslateX(521);
        episode.setTranslateY(166);
        level = new Text(Integer.toString(userData.getLevel()));
        level.setFont(Font.font("Arial", FontWeight.NORMAL, 15));
        level.setFill(Color.WHITE);
        level.setTranslateX(545);
        level.setTranslateY(168);

        updateLevel(userData.getLevel());

        //左上角的用户身份以及切换用户按钮
        Texture userBoard=new Texture(FXGL.image("ui/mainMenu/userBoard.png"));
        ImageButton changeUser=new ImageButton("mainMenu/changeUser", 291, 71,
                this::putChangeUserScene);
        userBoardtt=new TranslateTransition(Duration.seconds(1.0),userBoard);
        changeUsertt=new TranslateTransition(Duration.seconds(1.0),changeUser);
        userBoardtt.setFromX(20);// 动画位置，可调
        userBoardtt.setToX(20);
        userBoardtt.setFromY(-150);
        userBoardtt.setToY(0);
        changeUsertt.setFromX(20);// 动画位置，可调
        changeUsertt.setToX(20);
        changeUsertt.setFromY(-150);
        changeUsertt.setToY(140);
        userBoardtt.setInterpolator(Interpolators.ELASTIC.EASE_OUT());
        changeUsertt.setInterpolator(Interpolators.ELASTIC.EASE_OUT());
        userBoardtt.setOnFinished(e->{name.setVisible(true);});

        defaultPane=new Pane(iv, smallGame,puzzle,adventure,exist,userBoard,changeUser,name,episode);
        getContentRoot().getChildren().setAll(defaultPane,level);
    }

    @Override
    public void onCreate() {
        getContentRoot().getChildren().setAll(defaultPane,level);
        FXGL.loopBGM("mainMenu.wav");
        userBoardtt.play();
        changeUsertt.play();
    }

    @Override
    protected void onUpdate(double tpf) {
    }

    public void putChangeUserScene(){
        FXGL.getSceneService().pushSubScene(new ChangeUserDialog(users));
    }

    public void updateLevel(int newlevel){
        if(newlevel>=8){
            episode.setText("2");
            level.setText(Integer.toString(newlevel-7));
        }
        else{
            episode.setText("1");
            level.setText(Integer.toString(newlevel));
        }
    }

    public void updateUser(String user){
        name.setText(user);
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

        PVZApp pvzApp=(PVZApp) FXGL.getApp();
        pvzApp.setSpecialLevel(false);
        getController().startNewGame();
    }

    public void StartAmusementGame(){
        PVZApp pvzApp=(PVZApp) FXGL.getApp();
        pvzApp.setSpecialLevel(true);
        getController().startNewGame();
    }


    public void exist(){
        getController().exit();
    }
}
