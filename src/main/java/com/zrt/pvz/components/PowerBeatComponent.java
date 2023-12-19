package com.zrt.pvz.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.zrt.pvz.data.AnimationData;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 曾瑞庭
 * @Description:
 * @date 2023/12/18 2:41
 */
public class PowerBeatComponent extends Component {
    private ArrayList<Image> imageArrayList;
    private AnimationChannel ac;
    private AnimatedTexture at;
    @Override
    public void onAdded() {
        //咖啡豆动画参数，可调
        imageArrayList=new ArrayList<>();
        for(int i=0;i<7;i++){
            imageArrayList.add(FXGL.image(String.format("powerBeat/powerBeat_%d.png",i)));
        }
        ac=new AnimationChannel(imageArrayList, Duration.seconds(0.8));
        at=new AnimatedTexture(ac);
        entity.getViewComponent().addChild(at);
        at.loop();
        //更换动画，能量豆溶解
        FXGL.runOnce(()->{
            imageArrayList=new ArrayList<>();
            for(int i=1;i<=11;i++){
                imageArrayList.add(FXGL.image(String.format("powerBeat/Coffeebean_head%d.png",i),39,97));
            }
            AnimationChannel ac=new AnimationChannel(imageArrayList, Duration.seconds(1.0));
            AnimatedTexture at=new AnimatedTexture(ac);
            entity.getViewComponent().clearChildren();
            entity.getViewComponent().addChild(at);
            at.play();
        },Duration.seconds(2.0));
    }
}
