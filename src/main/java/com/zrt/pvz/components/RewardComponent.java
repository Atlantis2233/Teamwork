package com.zrt.pvz.components;

import com.almasb.fxgl.core.util.LazyValue;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.Texture;
import com.zrt.pvz.data.AnimationData;
import com.zrt.pvz.data.PlantData;
import com.zrt.pvz.ui.LevelEndScene;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 曾瑞庭
 * @Description: 奖励卡片的组件，主要实现：1.箭头 2.移动到屏幕中心 3.放白光切换场景
 * @date 2023/12/12 22:12
 */
public class RewardComponent extends Component {
    private Point2D rewardPosition;
    private AnimatedTexture at;
    private LazyValue<LevelEndScene> levelEndSceneLazyValue =
            new LazyValue<>(LevelEndScene::new);
    @Override
    public void onAdded() {
        ArrayList<Image> imageArrayList=new ArrayList<>();
        //为卡片绑定点击事件
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() != MouseButton.PRIMARY) {
                return;
            }
            showReward();
        });

        //生成箭头
        for(int i=0;i<4;i++){
            imageArrayList.add(FXGL.image(String.format("other/PointerDown/PointerDown_%d.png",i)));
        }
        AnimationChannel ac=new AnimationChannel(imageArrayList, Duration.seconds(1.0));
        at=new AnimatedTexture(ac);
        rewardPosition = entity.getPosition();
        at.setLayoutX(entity.getWidth()/2);
        at.setLayoutY(-100);
        entity.getViewComponent().addChild(at);
        at.loop();

    }

    //移到中间展示一下，推送出场景
    public void showReward(){

        //变大动画
        entity.getViewComponent().clearChildren();
        PlantData plantData=entity.getObject("plantData");
        Texture texture = FXGL.texture("ui/choose/"+plantData.name()+".png",45,60);
        Duration duration=Duration.seconds(5);
        ScaleTransition st=new ScaleTransition(duration,texture);
        st.setToX(2);
        st.setToY(2);
        entity.getViewComponent().addChild(texture);
        st.play();
        //移动
        Point2D center=new Point2D((double) FXGL.getAppWidth() /2, (double) FXGL.getAppHeight() /3);
        entity.getComponent(MoveComponent.class).moveFromTo(entity.getPosition(),center,0.2);

        //推送子场景
        FXGL.getSceneService().pushSubScene(levelEndSceneLazyValue.get());
    }
}
