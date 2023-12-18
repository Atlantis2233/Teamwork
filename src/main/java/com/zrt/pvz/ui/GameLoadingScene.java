package com.zrt.pvz.ui;

import com.almasb.fxgl.app.scene.LoadingScene;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.image.ImageView;

/**
 * @author 曾瑞庭
 * @Description:
 * @date 2023/12/18 19:22
 */
public class GameLoadingScene extends LoadingScene {
    public GameLoadingScene(){
        //场景不变
        ImageView iv = new ImageView(FXGL.image("map/map1LoadingBG.jpg",FXGL.getAppWidth()+5,FXGL.getAppHeight()));
        getContentRoot().getChildren().addAll(iv);
    }
}
