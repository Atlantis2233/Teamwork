package com.zrt.pvz.components;

import com.almasb.fxgl.core.util.LazyValue;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.Texture;
import com.zrt.pvz.data.AnimationData;
import com.zrt.pvz.data.PlantData;
import com.zrt.pvz.ui.LevelEndScene;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleDoubleProperty;
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
    private SimpleDoubleProperty progress =new SimpleDoubleProperty();
    private Texture[] pointerDown;
    private boolean firstFlag=true;
    private boolean[] lockFlag;
    @Override
    public void onAdded() {
        ArrayList<Image> imageArrayList=new ArrayList<>();
        lockFlag=new boolean[4];
        pointerDown=new Texture[4];
        //为卡片绑定点击事件
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() != MouseButton.PRIMARY) {
                return;
            }
            for(int i=0;i<4;i++){
                lockFlag[i]=true;
            }
            FXGL.play("winmusic.mp3");
            showReward();
        });

        //生成箭头
        for(int i=0;i<4;i++){
            pointerDown[i]=FXGL.texture("other/PointerDown/PointerDown_"+i+".png");
            pointerDown[i].setLayoutY(-40);
            pointerDown[i].setLayoutX(10);
        }
        progress.addListener((ob,ov,nv)->{
            for(int i=0;i<4;i++)
            {
                if((int)(nv.doubleValue())%4==i&&!lockFlag[i])
                {
                    if(!firstFlag){
                        lockFlag[(i+3)%4]=false;
                        entity.getViewComponent().removeChild(pointerDown[(i+3)%4]);
                    }
                    entity.getViewComponent().addChild(pointerDown[i]);
                    lockFlag[i]=true;
                    firstFlag=false;
                }
            }
        });
    }

    //移到中间展示一下，推送出场景
    //移到中间展示一下，推送出场景
    public void showReward(){
        //移动动画
        Point2D center=new Point2D((double) FXGL.getAppWidth() /2+150, (double) FXGL.getAppHeight() /2);
        entity.getComponent(MoveComponent.class).moveFromTo(entity.getPosition(),center,2);

        //删除箭头
        entity.getViewComponent().clearChildren();

        //播放音乐
        FXGL.play("winmusic.wav");

        //闪光动画
        Texture texture1 = FXGL.texture("ui/rewardPlant/AwardPickupGlow.png");
        Duration duration1=Duration.seconds(3);
        ScaleTransition st1=new ScaleTransition(duration1,texture1);
        texture1.setLayoutX(-65.2);
        texture1.setLayoutY(-58);
        st1.setToX(20);
        st1.setToY(20);
        entity.getViewComponent().addChild(texture1);
        st1.play();
        st1.setOnFinished(event->{
            FXGL.getSceneService().pushSubScene(levelEndSceneLazyValue.get());
        });

        //变大动画
        PlantData plantData=entity.getObject("plantData");
        Texture texture = FXGL.texture("ui/choose/"+plantData.name()+".png",45,60);
        Duration duration=Duration.seconds(3);
        ScaleTransition st=new ScaleTransition(duration,texture);
        st.setToX(2);
        st.setToY(2);
        entity.getViewComponent().addChild(texture);
        st.play();
    }
    @Override
    public void onUpdate(double tpf){
        progress.set(progress.get()+tpf*8);
    }
}
